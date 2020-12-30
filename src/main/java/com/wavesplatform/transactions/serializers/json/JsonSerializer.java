package com.wavesplatform.transactions.serializers.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wavesplatform.transactions.common.*;
import com.wavesplatform.transactions.data.*;
import com.wavesplatform.transactions.invocation.*;
import com.wavesplatform.transactions.serializers.Scheme;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PublicKey;
import im.mak.waves.crypto.base.Base58;
import im.mak.waves.crypto.base.Base64;
import im.mak.waves.transactions.*;
import im.mak.waves.transactions.common.*;
import im.mak.waves.transactions.data.*;
import com.wavesplatform.transactions.exchange.Order;
import com.wavesplatform.transactions.exchange.OrderType;
import im.mak.waves.transactions.invocation.*;
import com.wavesplatform.transactions.mass.Transfer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class JsonSerializer {

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    
    public static Order orderFromJson(JsonNode json) throws IOException {
        int version = json.get("version").asInt();

        OrderType type;
        String jsType = json.get("orderType").asText();
        if (jsType.equals(OrderType.BUY.value()))
            type = OrderType.BUY;
        else if (jsType.equals(OrderType.SELL.value()))
            type = OrderType.SELL;
        else throw new IOException("Unknown order type \"" + jsType + "\"");

        List<Proof> proofs = new ArrayList<>();
        if (version == 1)
            proofs.add(Proof.as(json.get("signature").asText()));
        else for (JsonNode proof : json.get("proofs"))
            proofs.add(Proof.as(proof.asText()));

        Amount fee = Amount.of(
                json.get("matcherFee").asLong(),
                json.has("matcherFeeAssetId") ? assetIdFromJson(json.get("matcherFeeAssetId")) : AssetId.WAVES
        );

        return new Order(
                PublicKey.as(json.get("senderPublicKey").asText()),
                type,
                Amount.of(json.get("amount").asLong(), assetIdFromJson(json.get("assetPair").get("amountAsset"))),
                Amount.of(json.get("price").asLong(), assetIdFromJson(json.get("assetPair").get("priceAsset"))),
                PublicKey.as(json.get("matcherPublicKey").asText()),
                json.has("chainId") ? (byte) json.get("chainId").asInt() : WavesConfig.chainId(),
                fee,
                json.get("timestamp").asLong(),
                json.get("expiration").asLong(),
                version,
                proofs
        );
    }

    public static Order orderFromJson(String json) throws IOException {
        return orderFromJson(JSON_MAPPER.readTree(json));
    }

    public static Transaction fromJson(JsonNode json) throws IOException {
        int type = json.get("type").asInt();
        int version = json.hasNonNull("version") ? json.get("version").asInt() : 1;
        byte chainId = json.has("chainId") ? (byte) json.get("chainId").asInt() : WavesConfig.chainId();
        PublicKey sender = json.hasNonNull("senderPublicKey")
                ? PublicKey.as(json.get("senderPublicKey").asText())
                : PublicKey.as(new byte[PublicKey.BYTES_LENGTH]);
        //todo validate sender address if exists? configurable? jsonNode.get("sender").asText(sender.address())
        Amount fee = Amount.of(
                json.get("fee").asLong(),
                json.hasNonNull("feeAssetId") ? assetIdFromJson(json.get("feeAssetId")) : AssetId.WAVES
        );
        long timestamp = json.get("timestamp").asLong();
        //todo validate id if exists? configurable?

        Scheme scheme = Scheme.of(type, version);

        List<Proof> proofs = new ArrayList<>();
        if (json.has("proofs")) {
            JsonNode jProofs = json.get("proofs");
            int size = jProofs.size();
            for (int i = 0; i < size; i++)
                proofs.add(Proof.as(jProofs.get(i).asText()));
        }

        if (type == GenesisTransaction.TYPE) {
            Address recipient = Address.as(json.get("recipient").asText());
            return new GenesisTransaction(recipient, json.get("amount").asLong(), timestamp, Proof.as(json.get("signature").asText()));
        } else if (type == PaymentTransaction.TYPE) {
            Address recipient = Address.as(json.get("recipient").asText());
            return new PaymentTransaction(sender, recipient, json.get("amount").asLong(), fee, timestamp,
                    Proof.as(json.get("proofs").get(0).asText()));
        } else if (type == IssueTransaction.TYPE) {
            if (!fee.assetId().isWaves())
                throw new IOException("feeAssetId field must be null for ReissueTransaction");
            if (version == 1 && json.has("signature"))
                proofs = Proof.list(Proof.as(json.get("signature").asText()));
            return new IssueTransaction(sender, json.get("name").asText(), json.get("description").asText(),
                    json.get("quantity").asLong(), json.get("decimals").asInt(), json.get("reissuable").asBoolean(),
                    scriptFromJson(json), chainId, fee, timestamp, version, proofs);
        } if (type == TransferTransaction.TYPE) {
            Recipient recipient = recipientFromJson(json.get("recipient"));
            if (version < 3)
                chainId = recipient.chainId();
            AssetId assetId = assetIdFromJson(json.get("assetId"));
            Base58String attachment = json.has("attachment")
                    ? new Base58String(json.get("attachment").asText()) : Base58String.empty();

            if (version == 1 && json.has("signature"))
                proofs = Proof.list(Proof.as(json.get("signature").asText()));
            return new TransferTransaction(sender, recipient, Amount.of(json.get("amount").asLong(), assetId),
                    attachment, chainId, fee, timestamp, version, proofs);
        } else if (type == ReissueTransaction.TYPE) {
            if (!fee.assetId().isWaves())
                throw new IOException("feeAssetId field must be null for ReissueTransaction");

            if (version == 1 && json.has("signature"))
                proofs = Proof.list(Proof.as(json.get("signature").asText()));
            return new ReissueTransaction(
                    sender, Amount.of(json.get("quantity").asLong(), assetIdFromJson(json.get("assetId"))),
                    json.get("reissuable").asBoolean(), chainId, fee, timestamp, version, proofs);
        } else if (type == BurnTransaction.TYPE) {
            if (!fee.assetId().isWaves())
                throw new IOException("feeAssetId field must be null for BurnTransaction");

            if (version == 1 && json.has("signature"))
                proofs = Proof.list(Proof.as(json.get("signature").asText()));

            long amount = json.get(scheme == Scheme.PROTOBUF ? "quantity" : "amount").asLong();

            return new BurnTransaction(
                    sender, Amount.of(amount, assetIdFromJson(json.get("assetId"))),
                    chainId, fee, timestamp, version, proofs);
        } else if (type == ExchangeTransaction.TYPE) {
            if (!fee.assetId().isWaves())
                throw new IOException("feeAssetId field must be null for ExchangeTransaction");

            if (version == 1 && json.has("signature"))
                proofs = Proof.list(Proof.as(json.get("signature").asText()));

            return new ExchangeTransaction(sender, orderFromJson(json.get("order1")), orderFromJson(json.get("order2")),
                    json.get("amount").asLong(), json.get("price").asLong(), json.get("buyMatcherFee").asLong(),
                    json.get("sellMatcherFee").asLong(), chainId, fee, timestamp, version, proofs);
        } else if (type == LeaseTransaction.TYPE) {
            if (!fee.assetId().isWaves())
                throw new IOException("feeAssetId field must be null for LeaseTransaction");

            Recipient recipient = recipientFromJson(json.get("recipient"));
            if (version < 3)
                chainId = recipient.chainId();
            if (version == 1 && json.has("signature"))
                proofs = Proof.list(Proof.as(json.get("signature").asText()));

            return new LeaseTransaction(
                    sender, recipient, json.get("amount").asLong(), chainId, fee, timestamp, version, proofs);
        } else if (type == LeaseCancelTransaction.TYPE) {
            if (!fee.assetId().isWaves())
                throw new IOException("feeAssetId field must be null for LeaseCancelTransaction");

            if (version == 1 && json.has("signature"))
                proofs = Proof.list(Proof.as(json.get("signature").asText()));
            return new LeaseCancelTransaction(
                    sender, Id.as(json.get("leaseId").asText()), chainId, fee, timestamp, version, proofs);
        } else if (type == CreateAliasTransaction.TYPE) {
            if (!fee.assetId().isWaves())
                throw new IOException("feeAssetId field must be null for CreateAliasTransaction");

            if (version == 1 && json.has("signature"))
                proofs = Proof.list(Proof.as(json.get("signature").asText()));
            return new CreateAliasTransaction(
                    sender, json.get("alias").asText(), chainId, fee, timestamp, version, proofs);
        } if (type == MassTransferTransaction.TYPE) {
            //todo check transferCount, totalAmount?
            JsonNode jsTransfers = json.get("transfers");
            List<Transfer> transfers = new ArrayList<>();
            for (JsonNode jsTransfer : jsTransfers) {
                Recipient recipient = recipientFromJson(jsTransfer.get("recipient"));
                long amount = jsTransfer.get("amount").asLong();
                transfers.add(Transfer.to(recipient, amount));
            }
            AssetId assetId = assetIdFromJson(json.get("assetId"));
            Base58String attachment = json.hasNonNull("attachment")
                    ? new Base58String(json.get("attachment").asText()) : Base58String.empty();
            if (version == 1 && transfers.size() > 0)
                chainId = transfers.get(0).recipient().chainId();

            if (version == 1 && json.has("signature"))
                proofs = Proof.list(Proof.as(json.get("signature").asText()));
            return new MassTransferTransaction(
                    sender, assetId, transfers, attachment, chainId, fee, timestamp, version, proofs);
        } else if (type == DataTransaction.TYPE) {
            if (!fee.assetId().isWaves())
                throw new IOException("feeAssetId field must be null for DataTransaction");

            List<DataEntry> data = dataEntriesFromJson(json.get("data"));
            return new DataTransaction(sender, data, chainId, fee, timestamp, version, proofs);
        } else if (type == SetScriptTransaction.TYPE) {
            if (!fee.assetId().isWaves())
                throw new IOException("feeAssetId field must be null for DataTransaction");

            return new SetScriptTransaction(sender, scriptFromJson(json), chainId, fee, timestamp, version, proofs);
        } else if (type == SponsorFeeTransaction.TYPE) {
            if (!fee.assetId().isWaves())
                throw new IOException("feeAssetId field must be null for SponsorFeeTransaction");

            return new SponsorFeeTransaction(sender, assetIdFromJson(json.get("assetId")),
                    json.get("minSponsoredAssetFee").asLong(), chainId, fee, timestamp, version, proofs);
        } else if (type == SetAssetScriptTransaction.TYPE) {
            if (!fee.assetId().isWaves())
                throw new IOException("feeAssetId field must be null for SetAssetScriptTransaction");

            AssetId assetId = assetIdFromJson(json.get("assetId"));
            return new SetAssetScriptTransaction(sender, assetId, scriptFromJson(json), chainId, fee, timestamp, version, proofs);
        } else if (type == InvokeScriptTransaction.TYPE) {
            Recipient dApp = recipientFromJson(json.get("dApp"));
            Function function = Function.asDefault();
            if (json.hasNonNull("call")) {
                JsonNode call = json.get("call");
                List<Arg> args = call.hasNonNull("args") ? argsFromJson(call.get("args")) : new ArrayList<>();
                function = Function.as(call.get("function").asText(), args);
            }
            List<Amount> payments = new ArrayList<>();
            String paymentsFieldName = "payment";
            if (json.hasNonNull(paymentsFieldName))
                json.get(paymentsFieldName).forEach(p ->
                        payments.add(Amount.of(p.get("amount").asLong(), assetIdFromJson(p.get("assetId")))));
            return new InvokeScriptTransaction(
                    sender, dApp, function, payments, chainId, fee, timestamp, version, proofs);
        } else if (type == UpdateAssetInfoTransaction.TYPE) {
            if (!fee.assetId().isWaves())
                throw new IOException("feeAssetId field must be null for UpdateAssetInfoTransaction");

            AssetId assetId = assetIdFromJson(json.get("assetId"));
            String name = json.get("name").asText();
            String description = json.get("description").asText();
            return new UpdateAssetInfoTransaction(sender, assetId, name, description, chainId, fee, timestamp, version, proofs);
        }

        throw new IOException("Can't parse json of transaction with type " + type);
    }
    
    public static Transaction fromJson(String json) throws IOException {
        return fromJson(JSON_MAPPER.readTree(json));
    }

    public static List<DataEntry> dataEntriesFromJson(JsonNode json) {
        List<DataEntry> data = new ArrayList<>();

        for (int i = 0; i < json.size(); i++)
            data.add(dataEntryFromJson(json.get(i)));

        return data;
    }

    public static DataEntry dataEntryFromJson(JsonNode json) {
            String key = json.get("key").asText();
            String entryType = json.hasNonNull("type") ? json.get("type").asText() : "";
            if (entryType.isEmpty())
                return new DeleteEntry(key);
            else if (entryType.equals("binary"))
                return new BinaryEntry(key, Base64.decode(json.get("value").asText()));
            else if (entryType.equals("boolean"))
                return new BooleanEntry(key, json.get("value").asBoolean());
            else if (entryType.equals("integer"))
                return new IntegerEntry(key, json.get("value").asLong());
            else if (entryType.equals("string"))
                return new StringEntry(key, json.get("value").asText());
            else throw new IllegalArgumentException("Unknown type `" + entryType + "` of entry with key `" + key + "`");
    }

    public static JsonNode toJsonObject(TransactionOrOrder txOrOrder) {
        ObjectNode jsObject = JSON_MAPPER.createObjectNode();
        Scheme scheme = Scheme.of(txOrOrder);

        if (txOrOrder instanceof Order) {
            Order order = (Order) txOrOrder;
            jsObject.put("id", order.id().toString())
                    .put("orderType", order.type().value())
                    .put("version", order.version())
                    .put("senderPublicKey", order.sender().toString())
                    .put("sender", order.sender().address(WavesConfig.chainId()).toString());
            jsObject.putObject("assetPair")
                    .put("amountAsset", assetIdToJson(order.amount().assetId()))
                    .put("priceAsset", assetIdToJson(order.price().assetId()));
            jsObject.put("amount", order.amount().value())
                    .put("price", order.price().value())
                    .put("matcherPublicKey", order.matcher().toString())
                    .put("matcherFee", order.fee().value())
                    .put("matcherFeeAssetId", assetIdToJson(order.fee().assetId()))
                    .put("timestamp", order.timestamp())
                    .put("expiration", order.expiration());
            if (order.proofs().size() > 0)
                    jsObject.put("signature", order.proofs().get(0).toString());

            if (order.version() < 3)
                jsObject.remove("matcherFeeAssetId");

            ArrayNode proofs = JSON_MAPPER.createArrayNode();
            order.proofs().forEach(p -> proofs.add(p.toString()));
            jsObject.set("proofs", proofs);
        } else {
            Transaction tx = (Transaction) txOrOrder;
            jsObject.put("id", tx.id().toString())
                    .put("type", tx.type())
                    .put("version", tx.version())
                    .put("chainId", tx.chainId())
                    .put("senderPublicKey", tx.sender().toString())
                    .put("sender", tx.sender().address(tx.chainId()).toString());

            ArrayNode proofs = JSON_MAPPER.createArrayNode();
            tx.proofs().forEach(p -> proofs.add(p.toString()));

            String signature = null;
            if (tx instanceof GenesisTransaction) {
                GenesisTransaction gtx = (GenesisTransaction) tx;
                jsObject.put("recipient", gtx.recipient().toString())
                        .put("amount", gtx.amount());
                jsObject.remove("version");
                jsObject.remove("chainId");
                jsObject.remove("senderPublicKey");
                jsObject.remove("sender");
                signature = gtx.proofs().get(0).toString();
            } else if (tx instanceof PaymentTransaction) {
                PaymentTransaction ptx = (PaymentTransaction) tx;
                jsObject.put("recipient", ptx.recipient().toString())
                        .put("amount", ptx.amount());
                jsObject.remove("version");
                jsObject.remove("chainId");
            } else if (tx instanceof IssueTransaction) {
                IssueTransaction itx = (IssueTransaction) tx;
                jsObject.put("name", itx.name())
                        .put("description", itx.description())
                        .put("quantity", itx.quantity())
                        .put("decimals", itx.decimals())
                        .put("reissuable", itx.reissuable())
                        .put("script", scriptToJson(itx.script()));
                if (itx.version() == 1) {
                    jsObject.remove("chainId");
                    signature = itx.proofs().get(0).toString();
                }
            } else if (tx instanceof TransferTransaction) {
                TransferTransaction ttx = (TransferTransaction) tx;
                jsObject.put("recipient", ttx.recipient().toString())
                        .put("amount", ttx.amount().value())
                        .put("assetId", assetIdToJson(ttx.amount().assetId()))
                        .put("attachment", Base58.encode(ttx.attachment().bytes()));
                if (ttx.version() < 3)
                    jsObject.remove("chainId");
                if (ttx.version() == 1)
                    signature = ttx.proofs().get(0).toString();
            } else if (tx instanceof ReissueTransaction) {
                ReissueTransaction rtx = (ReissueTransaction) tx;
                jsObject.put("assetId", assetIdToJson(rtx.amount().assetId()))
                        .put("quantity", rtx.amount().value())
                        .put("reissuable", rtx.reissuable());
                if (rtx.version() == 1) {
                    jsObject.remove("chainId");
                    signature = rtx.proofs().get(0).toString();
                }
            } else if (tx instanceof BurnTransaction) {
                BurnTransaction btx = (BurnTransaction) tx;
                jsObject.put("assetId", assetIdToJson(btx.amount().assetId()))
                        .put(scheme == Scheme.PROTOBUF ? "quantity" : "amount", btx.amount().value());
                if (btx.version() == 1) {
                    jsObject.remove("chainId");
                    signature = btx.proofs().get(0).toString();
                }
            } else if (tx instanceof ExchangeTransaction) {
                ExchangeTransaction etx = (ExchangeTransaction) tx;
                jsObject.set("order1", toJsonObject(etx.orders().get(0)));
                jsObject.set("order2", toJsonObject(etx.orders().get(1)));
                jsObject.put("amount", etx.amount())
                        .put("price", etx.price())
                        .put("buyMatcherFee", etx.buyMatcherFee())
                        .put("sellMatcherFee", etx.sellMatcherFee());
                if (scheme != Scheme.PROTOBUF)
                    jsObject.remove("chainId");
                if (scheme == Scheme.WITH_SIGNATURE)
                    signature = etx.proofs().get(0).toString();
            } else if (tx instanceof LeaseTransaction) {
                LeaseTransaction ltx = (LeaseTransaction) tx;
                jsObject.put("recipient", ltx.recipient().toString())
                        .put("amount", ltx.amount());
                if (ltx.version() == 1)
                    signature = ltx.proofs().get(0).toString();
                if (ltx.version() < 3)
                    jsObject.remove("chainId");
            } else if (tx instanceof LeaseCancelTransaction) {
                LeaseCancelTransaction lctx = (LeaseCancelTransaction) tx;
                jsObject.put("leaseId", lctx.leaseId().toString());
                if (lctx.version() == 1) {
                    jsObject.remove("chainId");
                    signature = lctx.proofs().get(0).toString();
                }
            } else if (tx instanceof CreateAliasTransaction) {
                CreateAliasTransaction catx = (CreateAliasTransaction) tx;
                jsObject.put("alias", catx.alias().name());
                if (catx.version() == 1)
                    signature = catx.proofs().get(0).toString();
                if (catx.version() < 3)
                    jsObject.remove("chainId");
            } else if (tx instanceof MassTransferTransaction) {
                MassTransferTransaction mtTx = (MassTransferTransaction) tx;
                jsObject.put("assetId", assetIdToJson(mtTx.assetId()))
                        .put("attachment", Base58.encode(mtTx.attachment().bytes()));
                ArrayNode jsTransfers = jsObject.putArray("transfers");
                for (Transfer transfer : mtTx.transfers()) {
                    jsTransfers.addObject()
                            .put("recipient", transfer.recipient().toString())
                            .put("amount", transfer.amount());
                }
                if (mtTx.version() == 1)
                    jsObject.remove("chainId");
            } else if (tx instanceof DataTransaction) {
                DataTransaction dtx = (DataTransaction) tx;
                ArrayNode data = jsObject.putArray("data");
                dtx.data().forEach(e -> {
                    ObjectNode entry = JSON_MAPPER.createObjectNode().put("key", e.key());
                    if (e instanceof BinaryEntry)
                        entry.put("type", "binary").put("value", ((BinaryEntry) e).value().encodedWithPrefix());
                    else if (e instanceof BooleanEntry)
                        entry.put("type", "boolean").put("value", ((BooleanEntry) e).value());
                    else if (e instanceof IntegerEntry)
                        entry.put("type", "integer").put("value", ((IntegerEntry) e).value());
                    else if (e instanceof StringEntry)
                        entry.put("type", "string").put("value", ((StringEntry) e).value());
                    else if (e instanceof DeleteEntry) {
                        entry.putNull("value").remove("type");
                    } else throw new IllegalArgumentException("Can't serialize entry with type " + e.type());
                    data.add(entry);
                });
                if (dtx.version() == 1)
                    jsObject.remove("chainId");
            } else if (tx instanceof SetScriptTransaction) {
                SetScriptTransaction ssTx = (SetScriptTransaction) tx;
                jsObject.put("script", scriptToJson(ssTx.script()));
            } else if (tx instanceof SponsorFeeTransaction) {
                SponsorFeeTransaction sfTx = (SponsorFeeTransaction) tx;
                if (sfTx.version() == 1)
                    jsObject.remove("chainId");
                jsObject.put("assetId", assetIdToJson(sfTx.assetId()))
                        .put("minSponsoredAssetFee", sfTx.minSponsoredFee());
            } else if (tx instanceof SetAssetScriptTransaction) {
                SetAssetScriptTransaction sasTx = (SetAssetScriptTransaction) tx;
                jsObject.put("assetId", assetIdToJson(sasTx.assetId()))
                        .put("script", scriptToJson(sasTx.script()));
            } else if (tx instanceof InvokeScriptTransaction) {
                InvokeScriptTransaction isTx = (InvokeScriptTransaction) tx;
                jsObject.put("dApp", isTx.dApp().toString());
                if (!isTx.function().isDefault()) {
                    ObjectNode call = jsObject.putObject("call");
                    call.put("function", isTx.function().name());
                    argsToJson(call.putArray("args"), isTx.function().args());
                }
                ArrayNode payments = jsObject.putArray("payment");
                isTx.payments().forEach(p -> {
                    ObjectNode payment = payments.addObject();
                    payment.put("amount", p.value()).
                            put("assetId", assetIdToJson(p.assetId()));
                });
                if (isTx.version() == 1)
                    jsObject.remove("chainId");
            } else if (tx instanceof UpdateAssetInfoTransaction) {
                UpdateAssetInfoTransaction uaiTx = (UpdateAssetInfoTransaction) tx;
                jsObject.put("assetId", assetIdToJson(uaiTx.assetId()))
                        .put("name", uaiTx.name())
                        .put("description", uaiTx.description());
            }

            jsObject.put("fee", tx.fee().value());
            if (!(tx instanceof GenesisTransaction))
                jsObject.put("feeAssetId", assetIdToJson(tx.fee().assetId()));
            jsObject.put("timestamp", tx.timestamp());

            if (signature != null)
                jsObject.put("signature", signature);
            if (!(tx instanceof GenesisTransaction))
                jsObject.set("proofs", proofs); //todo configurable for v1, true by default
        }

        return jsObject;
    }

    public static String toPrettyJson(TransactionOrOrder txOrOrder) {
        return toJsonObject(txOrOrder).toPrettyString();
    }

    public static String toJson(TransactionOrOrder txOrOrder) {
        return toJsonObject(txOrOrder).toString();
    }

    public static AssetId assetIdFromJson(JsonNode json) {
        return AssetId.as(json.asText(null));
    }

    public static String assetIdToJson(AssetId assetId) {
        return assetId.isWaves() ? null : assetId.toString();
    }

    public static List<Arg> argsFromJson(JsonNode json) throws IOException {
        List<Arg> args = new ArrayList<>();
        for (int i = 0; i < json.size(); i++) {
            JsonNode arg = json.get(i);
            String argType = arg.get("type").asText();
            if (argType.equals("binary"))
                args.add(BinaryArg.as(arg.get("value").asText()));
            else if (argType.equals("boolean"))
                args.add(BooleanArg.as(arg.get("value").asBoolean()));
            else if (argType.equals("integer"))
                args.add(IntegerArg.as(arg.get("value").asLong()));
            else if (argType.equals("string"))
                args.add(StringArg.as(arg.get("value").asText()));
            else if (argType.equals("list"))
                args.add(ListArg.as(argsFromJson(arg.get("value"))));
            else throw new IOException("Unknown arg type " + argType);
        }
        return args;
    }

    public static void argsToJson(ArrayNode json, List<Arg> args) {
        args.forEach(a -> {
            ObjectNode arg = json.addObject();
            if (a instanceof BinaryArg)
                arg.put("type", "binary").put("value", ((BinaryArg) a).value().encodedWithPrefix());
            else if (a instanceof BooleanArg)
                arg.put("type", "boolean").put("value", ((BooleanArg) a).value());
            else if (a instanceof IntegerArg)
                arg.put("type", "integer").put("value", ((IntegerArg) a).value());
            else if (a instanceof StringArg)
                arg.put("type", "string").put("value", ((StringArg) a).value());
            else if (a instanceof ListArg) {
                arg.put("type", "list");
                argsToJson(arg.putArray("value"), ((ListArg) a).value());
            } else throw new IllegalArgumentException("Unknown arg type");
        });
    }

    public static Recipient recipientFromJson(JsonNode json) {
        String value = json.asText();
        return Address.isValid(value) ? Address.as(value) : Alias.as(value);
    }

    public static Base64String scriptFromJson(JsonNode json) {
        return json.hasNonNull("script") ? new Base64String(json.get("script").asText()) : Base64String.empty();
    }

    public static String scriptToJson(Base64String script) {
        return script == null || script.bytes().length == 0 ? null : script.encodedWithPrefix();
    }

}
