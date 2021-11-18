package com.wavesplatform.transactions;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.account.PublicKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.common.Proof;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.utils.Numeric;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class EthereumTransaction extends Transaction {
    public static final BigInteger AMOUNT_MULTIPLIER = BigInteger.valueOf(10_000_000_000L);
    public static final int TYPE_TAG = 19;
    public static final String ERC20_PREFIX = "0xa9059cbb";
    public static final int ADDRESS_LENGTH = 20;

    private final long gasPrice;
    private final Payload payload;
    private final Sign.SignatureData signatureData;

    private static Method encodeMethod;

    static {
        try {
            encodeMethod = TransactionEncoder.class.getDeclaredMethod("encode", RawTransaction.class, Sign.SignatureData.class);
            encodeMethod.setAccessible(true);
        } catch (NoSuchMethodException nsme) {
            encodeMethod = null;
        }
    }

    private EthereumTransaction(byte chainId, long timestamp, long gasPrice, long fee, Payload payload, Sign.SignatureData signatureData, PublicKey sender) {
        super(0, 0, chainId, sender, Amount.of(fee), timestamp, Collections.emptyList());
        this.gasPrice = gasPrice;
        this.payload = payload;
        this.signatureData = signatureData;
    }

    @Override
    public Id id() {
        return Id.as(Hash.sha3(encode(payload.toRawTransaction(timestamp(), gasPrice, fee().value()), signatureData)));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(id().bytes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EthereumTransaction that = (EthereumTransaction) o;
        return Arrays.equals(id().bytes(), that.id().bytes());
    }

    public long gasPrice() {
        return gasPrice;
    }

    public Payload payload() {
        return payload;
    }

    public Sign.SignatureData signatureData() {
        return signatureData;
    }

    @Override
    public <T extends TransactionOrOrder> T addProof(Proof proof) {
        throw new UnsupportedOperationException("addProof");
    }

    @Override
    public <T extends TransactionOrOrder> T addProof(PrivateKey privateKey) {
        throw new UnsupportedOperationException("addProof");
    }

    @Override
    public <T extends TransactionOrOrder> T addProofs(List<Proof> proofs) {
        throw new UnsupportedOperationException("addProofs");
    }

    @Override
    public <T extends TransactionOrOrder> T setProof(int index, Proof proof) {
        throw new UnsupportedOperationException("setProof");
    }

    @Override
    public <T extends TransactionOrOrder> T setProof(int index, PrivateKey privateKey) {
        throw new UnsupportedOperationException("setProof");
    }

    @Override
    public int version() {
        throw new UnsupportedOperationException("version");
    }

    @Override
    public String toJson() {
        return super.toJson();
    }

    public interface Payload {
        RawTransaction toRawTransaction(long timestamp, long gasPrice, long fee);
    }

    public static byte[] publicKeyBytes(BigInteger publicKey) {
        byte[] publicKeyBytes = publicKey.toByteArray();
        return Arrays.copyOfRange(
                publicKeyBytes,
                publicKeyBytes.length - PublicKey.ETH_BYTES_LENGTH,
                publicKeyBytes.length
        );
    }

    public static byte[] encode(RawTransaction transaction, Sign.SignatureData signatureData) {
        requireNonNull(encodeMethod, "encode is not available");
        try {
            return (byte[]) encodeMethod.invoke(null, transaction, signatureData);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Reflective error while encoding transaction", e);
        }
    }

    public static class Transfer implements Payload {
        private final Address recipient;
        private final Amount amount;


        public Transfer(Address recipient, Amount amount) {
            this.recipient = recipient;
            this.amount = amount;
        }

        @Override
        public RawTransaction toRawTransaction(long timestamp, long gasPrice, long fee) {
            if (amount.assetId().isWaves()) {
                return RawTransaction.createEtherTransaction(
                        BigInteger.valueOf(timestamp),
                        BigInteger.valueOf(gasPrice),
                        BigInteger.valueOf(fee),
                        Numeric.toHexString(recipient.publicKeyHash()),
                        BigInteger.valueOf(amount.value()).multiply(AMOUNT_MULTIPLIER)
                );
            } else {
                return RawTransaction.createTransaction(
                        BigInteger.valueOf(timestamp),
                        BigInteger.valueOf(gasPrice),
                        BigInteger.valueOf(fee),
                        Numeric.toHexString(amount.assetId().bytes(), 0, ADDRESS_LENGTH, true),
                        BigInteger.ZERO,
                        ERC20_PREFIX +
                                Numeric.toHexStringNoPrefix(recipient.publicKeyHash()) +
                                TypeEncoder.encode(new Uint256(amount.value()))
                );
            }
        }
    }

    public static EthereumTransaction transfer(Address recipient, Amount amount, long gasPrice,
                                               byte chainId, long fee, long timestamp, Sign.SignatureData signatureData) {
        Payload payload = new Transfer(recipient, amount);
        PublicKey sender = PublicKey.as(publicKeyBytes(Sign.recoverFromSignature(
                Sign.getRecId(signatureData, chainId),
                new ECDSASignature(
                        new BigInteger(1, signatureData.getR()),
                        new BigInteger(1, signatureData.getS())),
                TransactionEncoder.encode(payload.toRawTransaction(timestamp, gasPrice, fee), (long) chainId)
        )));

        return new EthereumTransaction(chainId, timestamp, gasPrice, fee, payload, signatureData, sender);
    }

    public static EthereumTransaction createAndSign(Payload payload, long gasPrice, byte chainId, long fee, long timestamp, ECKeyPair keyPair) {
        RawTransaction rawTransaction = payload.toRawTransaction(timestamp, gasPrice, fee);
        byte[] transactionBytes = TransactionEncoder.encode(rawTransaction, (long) chainId);
        Sign.SignatureData signatureData = TransactionEncoder.createEip155SignatureData(Sign.signMessage(transactionBytes, keyPair), (long) chainId);
        PublicKey sender = PublicKey.as(publicKeyBytes(keyPair.getPublicKey()));

        return new EthereumTransaction(chainId, timestamp, gasPrice, fee, payload, signatureData, sender);
    }

    public static EthereumTransaction transfer(Address recipient, Amount amount, long gasPrice,
                                               byte chainId, long fee, long timestamp, ECKeyPair keyPair) {
        Payload payload = new Transfer(recipient, amount);
        return createAndSign(payload, gasPrice, chainId, fee, timestamp, keyPair);
    }

    public static EthereumTransaction parse(String transactionBytesAsHex) {
        SignedRawTransaction srt = (SignedRawTransaction) TransactionDecoder.decode(transactionBytesAsHex);
        String data = Numeric.cleanHexPrefix(srt.getTransaction().getData());
        if (data.isEmpty() && !srt.getTransaction().getValue().equals(BigInteger.ZERO)) {
            return EthereumTransaction.transfer(
                    Address.fromPart(srt.getChainId().byteValue(), Numeric.hexStringToByteArray(srt.getTo())),
                    Amount.of(srt.getValue().divide(AMOUNT_MULTIPLIER).longValueExact()),
                    srt.getGasPrice().longValueExact(),
                    srt.getChainId().byteValue(),
                    srt.getGasLimit().longValueExact(),
                    srt.getNonce().longValueExact(),
                    srt.getSignatureData()
            );
        } else if (data.startsWith(ERC20_PREFIX) && srt.getTransaction().getValue().equals(BigInteger.ZERO)) {
            return EthereumTransaction.transfer(
                    Address.fromPart(
                            srt.getChainId().byteValue(),
                            Numeric.hexStringToByteArray(new org.web3j.abi.datatypes.Address(data.substring(8, 71)).toString())
                    ),
                    Amount.of(
                            Numeric.toBigInt(data.substring(72)).divide(AMOUNT_MULTIPLIER).longValueExact(),
                            AssetId.as(Numeric.hexStringToByteArray(srt.getTo()))
                    ),
                    srt.getGasPrice().longValueExact(),
                    srt.getChainId().byteValue(),
                    srt.getGasLimit().longValueExact(),
                    srt.getNonce().longValueExact(),
                    srt.getSignatureData()
            );
        }

        throw new IllegalArgumentException("Could not parse transaction");
    }

    public static EthereumTransaction parse(byte[] transactionBytes) {
        return parse(Numeric.toHexString(transactionBytes));
    }
}
