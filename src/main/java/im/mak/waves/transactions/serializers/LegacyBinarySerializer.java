package im.mak.waves.transactions.serializers;

import im.mak.waves.crypto.Bytes;
import im.mak.waves.crypto.Bytes.ByteReader;
import im.mak.waves.crypto.account.Address;
import im.mak.waves.crypto.account.PublicKey;
import im.mak.waves.transactions.LeaseCancelTransaction;
import im.mak.waves.transactions.LeaseTransaction;
import im.mak.waves.transactions.Transaction;
import im.mak.waves.transactions.TransferTransaction;
import im.mak.waves.transactions.common.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static im.mak.waves.crypto.Bytes.concat;
import static im.mak.waves.crypto.Bytes.of;
import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class LegacyBinarySerializer {

    public static byte[] bodyBytes(Transaction tx) {
        byte[] result = Bytes.empty();
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {

            if (tx instanceof TransferTransaction) {
                TransferTransaction ttx = (TransferTransaction) tx;
                if (ttx.version() > 2)
                    throw new RuntimeException("not legacy");

                boolean withProofs = ttx.version() == 2;

                stream.write(Bytes.of((byte) ttx.type()));
                if (withProofs)
                    stream.write(Bytes.of((byte) ttx.version()));

                stream.write(ttx.sender().bytes());
                if (ttx.asset().isWaves()) {
                    stream.write(Bytes.of((byte) 0));
                } else {
                    stream.write(Bytes.of((byte) 1));
                    stream.write(ttx.asset().bytes());
                }
                if (ttx.feeAsset().isWaves()) {
                    stream.write(Bytes.of((byte) 0));
                } else {
                    stream.write(Bytes.of((byte) 1));
                    stream.write(ttx.feeAsset().bytes());
                }
                stream.write(recipientToBytes(ttx.recipient()));
                stream.write(Bytes.fromLong(ttx.amount()));
                stream.write(Bytes.fromLong(ttx.fee()));
                stream.write(Bytes.fromLong(ttx.timestamp()));
                stream.write(Bytes.toSizedByteArray(ttx.attachment().getBytes(UTF_8)));
            } if (tx instanceof LeaseTransaction) {
                LeaseTransaction ltx = (LeaseTransaction) tx;
                if (ltx.version() > 2)
                    throw new RuntimeException("not legacy");

                boolean withProofs = ltx.version() == 2;

                stream.write(Bytes.of((byte) ltx.type()));
                if (withProofs)
                    stream.write(Bytes.of((byte) ltx.version(), (byte) 0));

                stream.write(ltx.sender().bytes());
                stream.write(recipientToBytes(ltx.recipient()));
                stream.write(Bytes.fromLong(ltx.amount()));
                stream.write(Bytes.fromLong(ltx.fee()));
                stream.write(Bytes.fromLong(ltx.timestamp()));
            } else if (tx instanceof LeaseCancelTransaction) {
                LeaseCancelTransaction lctx = (LeaseCancelTransaction) tx;
                if (lctx.version() > 2)
                    throw new RuntimeException("not legacy");

                boolean withProofs = lctx.version() == 2;

                stream.write(Bytes.of((byte) lctx.type()));
                if (withProofs)
                    stream.write(Bytes.of((byte) lctx.version(), lctx.chainId()));

                stream.write(lctx.sender().bytes());
                stream.write(Bytes.fromLong(lctx.fee()));
                stream.write(Bytes.fromLong(lctx.timestamp()));
                stream.write(lctx.leaseId().bytes());
            } //todo other types

            result = stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace(); //todo
        }

        return result;
    }

    public static byte[] bytes(Transaction tx) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            if (tx instanceof TransferTransaction) {
                TransferTransaction ttx = (TransferTransaction) tx;
                boolean withProofs = ttx.version() == 2;
                if (withProofs)
                    stream.write(Bytes.of((byte) 0));
                stream.write(ttx.bodyBytes());
                stream.write(proofsToBytes(ttx.proofs(), withProofs));
            } else if (tx instanceof LeaseTransaction) {
                LeaseTransaction ltx = (LeaseTransaction) tx;
                boolean withProofs = ltx.version() == 2;
                if (withProofs)
                    stream.write(Bytes.of((byte) 0));
                stream.write(ltx.bodyBytes());
                stream.write(proofsToBytes(ltx.proofs(), withProofs));
            } else if (tx instanceof LeaseCancelTransaction) {
                LeaseCancelTransaction lctx = (LeaseCancelTransaction) tx;
                boolean withProofs = lctx.version() == 2;
                if (withProofs)
                    stream.write(Bytes.of((byte) 0));
                stream.write(lctx.bodyBytes());
                stream.write(proofsToBytes(lctx.proofs(), withProofs));
            } //todo other types

            return stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace(); //todo
        }
        return null; //todo exception
    }

    public static Transaction fromBytes(byte[] bytes) throws IOException {
        if (bytes.length < 4)
            throw new IOException("Byte array in too short to parse a transaction");
        boolean withProofs = bytes[0] == 0;
        int index = withProofs ? 1 : 0;

        int type = bytes[index];
        int version = withProofs ? bytes[index + 1] : 1;
        byte[] data = Bytes.chunk(bytes, withProofs ? 3 : 1)[1];

        Transaction transaction;
        ByteReader reader = new ByteReader(data);
        if (type == 1) throw new IOException("Genesis transactions are not supported"); //todo
        else if (type == 2) throw new IOException("Payment transactions are not supported"); //todo
        else if (type == TransferTransaction.TYPE) transaction = transfer(reader, version, withProofs);
        else if (type == LeaseTransaction.TYPE) transaction = lease(reader, version, withProofs);
        else if (type == LeaseCancelTransaction.TYPE) transaction = leaseCancel(reader, version, withProofs);
        //todo other types
        else throw new IOException("Unknown transaction type " + type);

        if (reader.hasNext())
            throw new IOException("The size of " + bytes.length
                    + " bytes is " + (bytes.length - reader.rest())
                    + " greater than expected for type " + type + " and version " + version + " of the transaction");

        return transaction;
    }

    protected static TransferTransaction transfer(ByteReader data, int version, boolean withProofs) throws IOException {
        PublicKey sender = PublicKey.as(data.read(PublicKey.BYTES_LENGTH));
        boolean isAsset = data.readBoolean();
        Asset asset = isAsset ? Asset.id(data.read(Asset.BYTE_LENGTH)) : Asset.WAVES;
        boolean isFeeAsset = data.readBoolean();
        Asset feeAsset = isFeeAsset ? Asset.id(data.read(Asset.BYTE_LENGTH)) : Asset.WAVES;
        Recipient recipient = readRecipient(data);
        long amount = data.readLong();
        long fee = data.readLong();
        long timestamp = data.readLong();
        byte[] attachment = data.readArray(); //fixme not typed, NODE-2145
        List<Proof> proofs = readProofs(data, withProofs);

        TransferTransaction tx = new TransferTransaction(sender, recipient, amount, asset, new String(attachment), recipient.chainId(), fee, feeAsset, timestamp, version);
        proofs.forEach(p -> tx.proofs().add(p)); //todo `Proofs extends List` or move proofs to builder
        return tx;
    }

    protected static LeaseTransaction lease(ByteReader data, int version, boolean withProofs) throws IOException {
        if (version == 2 && data.read() != 0)
            throw new IOException("Reserved field must be 0");

        PublicKey sender = PublicKey.as(data.read(PublicKey.BYTES_LENGTH));
        Recipient recipient = readRecipient(data);
        long amount = data.readLong();
        long fee = data.readLong();
        long timestamp = data.readLong();
        List<Proof> proofs = readProofs(data, withProofs);

        LeaseTransaction tx = new LeaseTransaction(sender, recipient, amount, recipient.chainId(), fee, timestamp, version);
        proofs.forEach(p -> tx.proofs().add(p)); //todo `Proofs extends List` or move proofs to builder
        return tx;
    }

    protected static LeaseCancelTransaction leaseCancel(ByteReader data, int version, boolean withProofs) throws IOException {
        byte chainId = version == 2 ? data.read() : Waves.chainId;
        PublicKey sender = PublicKey.as(data.read(PublicKey.BYTES_LENGTH));
        long fee = data.readLong();
        long timestamp = data.readLong();
        TxId leaseId = TxId.id(data.read(TxId.BYTE_LENGTH));
        List<Proof> proofs = readProofs(data, withProofs);

        LeaseCancelTransaction tx = new LeaseCancelTransaction(sender, leaseId, chainId, fee, timestamp, version);
        proofs.forEach(p -> tx.proofs().add(p)); //todo `Proofs extends List` or move proofs to builder
        return tx;
    }

    protected static Recipient readRecipient(ByteReader data) throws IOException {
        byte recipientType = data.read(); //todo Recipient.from(bytes) or Alias.from(bytes)
        if (recipientType == 1)
            return Recipient.as(Address.as(concat(of(recipientType), data.read(25)))); //todo Address.LENGTH
        else if (recipientType == 2) {
            return Recipient.as(Alias.as(data.read(), new String(data.readArray()))); //todo Alias.as(bytes)
        } else throw new IOException("Unknown recipient type");
    }

    protected static byte[] recipientToBytes(Recipient recipient) {
        if (recipient.isAlias())
            return Bytes.concat(
                    Bytes.of((byte) 2, recipient.chainId()),
                    Bytes.toSizedByteArray(recipient.alias().value().getBytes()));
        else
            return recipient.address().bytes();
    }

    protected static List<Proof> readProofs(ByteReader data, boolean withProofs) throws IOException {
        if (withProofs) {
            byte version = data.read(); //todo Proofs.VERSION = 1
            if (version != 1)
                throw new IOException("Wrong proofs version " + version + " but " + 1 + " expected");

            List<Proof> result = Proof.emptyList();
            short proofsCount = data.readShort();
            for (short i = 0; i < proofsCount; i++)
                result.add(Proof.as(data.readArray()));

            return result;
        } else {
            return Proof.list(Proof.as(data.read(64)));
        }
    }

    protected static byte[] proofsToBytes(List<Proof> proofs, boolean withProofs) {
        if (withProofs) {
            byte[] proofsVersion = Bytes.of((byte) 1);
            byte[] proofsBytes = Bytes.fromShort((short) proofs.size());
            for (Proof proof : proofs)
                proofsBytes = Bytes.concat(proofsBytes, Bytes.toSizedByteArray(proof.bytes()));
            return Bytes.concat(proofsVersion, proofsBytes);
        } else {
            if (proofs.size() != 1)
                throw new IllegalArgumentException("Transaction of this type and version must have only 1 proof");
            return proofs.get(0).bytes();
        }
    }

}
