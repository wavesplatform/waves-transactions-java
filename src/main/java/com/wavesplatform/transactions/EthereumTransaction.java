package com.wavesplatform.transactions;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.account.PublicKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.common.Proof;
import com.wavesplatform.transactions.invocation.*;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Int64;
import org.web3j.abi.datatypes.generated.StaticArray2;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.*;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class EthereumTransaction extends Transaction {
    public static final BigInteger AMOUNT_MULTIPLIER = BigInteger.valueOf(10_000_000_000L);
    public static final int TYPE_TAG = 18;
    public static final String ERC20_PREFIX = "0xa9059cbb";
    public static final int ADDRESS_LENGTH = 20;
    public static final BigInteger DEFAULT_GAS_PRICE = Convert.toWei("10", Convert.Unit.GWEI).toBigInteger();

    private final BigInteger gasPrice;
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

    public EthereumTransaction(byte chainId, long timestamp, BigInteger gasPrice, long fee, Payload payload, Sign.SignatureData signatureData, PublicKey sender) {
        super(TYPE_TAG, 1, chainId, sender, Amount.of(fee), timestamp, Collections.emptyList());
        this.gasPrice = gasPrice;
        this.payload = payload;
        this.signatureData = signatureData;
    }

    public EthereumTransaction(Id id, byte chainId, long timestamp, BigInteger gasPrice, long fee, Payload payload, Sign.SignatureData signatureData, PublicKey sender) {
        super(TYPE_TAG, 1, chainId, sender, Amount.of(fee), timestamp, Collections.emptyList());
        this.id = id;
        this.gasPrice = gasPrice;
        this.payload = payload;
        this.signatureData = signatureData;
    }

    @Override
    public Id id() {
        if (id == null) {
            return Id.as(Hash.sha3(toBytes()));
        }
        return id;
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

    public BigInteger gasPrice() {
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
    public String toJson() {
        return super.toJson();
    }

    public interface Payload {
        RawTransaction toRawTransaction(long timestamp, BigInteger gasPrice, long fee);
    }

    public static byte[] publicKeyBytes(BigInteger publicKey) {
        byte[] publicKeyBytes = publicKey.toByteArray();
        return Arrays.copyOfRange(
                publicKeyBytes,
                publicKeyBytes.length - PublicKey.ETH_BYTES_LENGTH,
                publicKeyBytes.length
        );
    }

    @Override
    public byte[] toBytes() {
        return encode(payload.toRawTransaction(timestamp(), gasPrice, fee().value()), signatureData);
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

        public Address recipient() {
            return recipient;
        }

        public Amount amount() {
            return amount;
        }

        @Override
        public RawTransaction toRawTransaction(long timestamp, BigInteger gasPrice, long fee) {
            if (amount.assetId().isWaves()) {
                return RawTransaction.createEtherTransaction(
                        BigInteger.valueOf(timestamp),
                        gasPrice,
                        BigInteger.valueOf(fee),
                        Numeric.toHexString(recipient.publicKeyHash()),
                        BigInteger.valueOf(amount.value()).multiply(AMOUNT_MULTIPLIER)
                );
            } else {
                return RawTransaction.createTransaction(
                        BigInteger.valueOf(timestamp),
                        gasPrice,
                        BigInteger.valueOf(fee),
                        Numeric.toHexString(amount.assetId().bytes(), 0, ADDRESS_LENGTH, true),
                        BigInteger.ZERO,
                        ERC20_PREFIX +
                                TypeEncoder.encode(new org.web3j.abi.datatypes.Address(new BigInteger(1, recipient.publicKeyHash()))) +
                                TypeEncoder.encode(new Uint256(amount.value()))
                );
            }
        }
    }

    public static class Invocation implements Payload {
        private final Address dapp;
        private final Function function;
        private final List<Amount> payments;

        public Invocation(Address dapp, Function function, List<Amount> payments) {
            this.dapp = dapp;
            this.function = function;
            this.payments = payments;
        }

        public Address dApp() {
            return dapp;
        }

        public Function function() {
            return function;
        }

        public List<Amount> payments() {
            return payments;
        }

        private void addArgs(ArrayList<Type> target, List<Arg> source, boolean allowNesting) {
            for (Arg arg : source) {
                if (arg instanceof BinaryArg) {
                    target.add(new DynamicBytes(((BinaryArg) arg).value().bytes()));
                } else if (arg instanceof StringArg) {
                    target.add(new Utf8String(((StringArg) arg).value()));
                } else if (arg instanceof IntegerArg) {
                    target.add(new Int64(((IntegerArg) arg).value()));
                } else if (arg instanceof BooleanArg) {
                    target.add(new Uint8(((BooleanArg) arg).value() ? 1 : 0));
                } else if (arg instanceof ListArg) {
                    if (!allowNesting) {
                        throw new IllegalArgumentException("Nested lists are not supported");
                    }
                    ArrayList<Type> listValues = new ArrayList<>();
                    addArgs(listValues, ((ListArg) arg).value(), false);
                    target.add(new DynamicArray<>(listValues));
                }
            }
        }

        @Override
        public RawTransaction toRawTransaction(long timestamp, BigInteger gasPrice, long fee) {
            ArrayList<Type> params = new ArrayList<>();
            addArgs(params, function.args(), true);

            List<StaticArray2> encodedPayments = payments.stream().map(a -> new StaticArray2(Uint256.class, new Uint256(1), new Uint256(10))).collect(Collectors.toList());
            DynamicArray<StaticArray2> paymentArgs = new DynamicArray<>(StaticArray2.class, encodedPayments);
            params.add(paymentArgs);

            org.web3j.abi.datatypes.Function f = new org.web3j.abi.datatypes.Function(
                    function.isDefault() ? "default" : function.name(),
                    params,
                    Collections.emptyList()
            );
            return RawTransaction.createTransaction(
                    BigInteger.valueOf(timestamp),
                    gasPrice,
                    BigInteger.valueOf(fee),
                    Numeric.toHexString(dapp.publicKeyHash()),
                    FunctionEncoder.encode(f)
            );
        }
    }

    public static PublicKey recoverFromSignature(Sign.SignatureData signatureData, byte chainId, RawTransaction rawTransaction) {
        return PublicKey.as(publicKeyBytes(Sign.recoverFromSignature(
                Sign.getRecId(signatureData, chainId),
                new ECDSASignature(
                        new BigInteger(1, signatureData.getR()),
                        new BigInteger(1, signatureData.getS())),
                TransactionEncoder.encode(rawTransaction, (long) chainId)
        )));
    }

    public static EthereumTransaction transfer(Address recipient, Amount amount,
                                               BigInteger gasPrice, byte chainId, long fee, long timestamp, Sign.SignatureData signatureData) {
        Payload payload = new Transfer(recipient, amount);
        PublicKey sender = recoverFromSignature(signatureData, chainId, payload.toRawTransaction(timestamp, gasPrice, fee));

        return new EthereumTransaction(chainId, timestamp, gasPrice, fee, payload, signatureData, sender);
    }

    public static EthereumTransaction transfer(Address recipient, Amount amount,
                                               BigInteger gasPrice, byte chainId, long fee, long timestamp, ECKeyPair keyPair) {
        return createAndSign(new Transfer(recipient, amount), gasPrice, chainId, fee, timestamp, keyPair);
    }

    public static EthereumTransaction invocation(Address dapp, Function function, List<Amount> payments,
                                                 BigInteger gasPrice, byte chainId, long fee, long timestamp, Sign.SignatureData signatureData) {
        Payload payload = new Invocation(dapp, function, payments);
        PublicKey sender = recoverFromSignature(signatureData, chainId, payload.toRawTransaction(timestamp, gasPrice, fee));

        return new EthereumTransaction(chainId, timestamp, gasPrice, fee, payload, signatureData, sender);
    }

    public static EthereumTransaction invocation(Address dapp, Function function, List<Amount> payments,
                                                 BigInteger gasPrice, byte chainId, long fee, long timestamp, ECKeyPair keyPair) {
        return createAndSign(new Invocation(dapp, function, payments), gasPrice, chainId, fee, timestamp, keyPair);
    }

    public static EthereumTransaction createAndSign(Payload payload, BigInteger gasPrice, byte chainId, long fee, long timestamp, ECKeyPair keyPair) {
        RawTransaction rawTransaction = payload.toRawTransaction(timestamp, gasPrice, fee);
        byte[] transactionBytes = TransactionEncoder.encode(rawTransaction, (long) chainId);
        Sign.SignatureData signatureData = TransactionEncoder.createEip155SignatureData(Sign.signMessage(transactionBytes, keyPair), (long) chainId);
        PublicKey sender = PublicKey.as(publicKeyBytes(keyPair.getPublicKey()));

        return new EthereumTransaction(chainId, timestamp, gasPrice, fee, payload, signatureData, sender);
    }


    public static EthereumTransaction parse(String transactionBytesAsHex) {
        SignedRawTransaction srt = (SignedRawTransaction) TransactionDecoder.decode(transactionBytesAsHex);
        String data = Numeric.cleanHexPrefix(srt.getTransaction().getData());
        if (data.isEmpty() && !srt.getTransaction().getValue().equals(BigInteger.ZERO)) {
            return EthereumTransaction.transfer(
                    Address.fromPart(srt.getChainId().byteValue(), Numeric.hexStringToByteArray(srt.getTo())),
                    Amount.of(srt.getValue().divide(AMOUNT_MULTIPLIER).longValueExact()),
                    srt.getGasPrice(),
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
                    srt.getGasPrice(),
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
