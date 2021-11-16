package com.wavesplatform.transactions;

import com.wavesplatform.crypto.base.Base64;
import com.wavesplatform.transactions.account.PublicKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.Base64String;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.common.Proof;
import com.wavesplatform.transactions.serializers.json.JsonSerializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class InvokeExpressionTransactionTest {
    static Stream<Arguments> transactions() {
        return Stream.of(
                arguments(
                        1,
                        new Base64String("base64:/wYFAAAAA25pbBYQh30="),
                        (byte) 'C',
                        PublicKey.as("3V6yoMusXyifrKATxaZsMaoagnvJ84c791fCvQrtQbGu"),
                        Amount.of(1000000),
                        1634662478958L,
                        Collections.singletonList(Proof.as("3Bf3BTKddwzuHws68jg26jrQk8tiexU1RQQiBaYrzimRRxGZY94PivUB5m4PsztExTLa87EWoAr5J5JjzsZMvwkh")),
                        Id.as("FWRJ3QoQKN8rGAr2gjaTaYNBptUwt5r1hV3zdfQP2SY"),
                        "{\"type\":18,\"id\":\"FWRJ3QoQKN8rGAr2gjaTaYNBptUwt5r1hV3zdfQP2SY\",\"fee\":1000000,\"feeAssetId\":null,\"timestamp\":1634662478958,\"version\":1,\"chainId\":67,\"sender\":\"3F7kSyoViE2Npqc1Mr8kHv9dBjsNB8HH4cM\",\"senderPublicKey\":\"3V6yoMusXyifrKATxaZsMaoagnvJ84c791fCvQrtQbGu\",\"proofs\":[\"3Bf3BTKddwzuHws68jg26jrQk8tiexU1RQQiBaYrzimRRxGZY94PivUB5m4PsztExTLa87EWoAr5J5JjzsZMvwkh\"],\"expression\":\"base64:/wYFAAAAA25pbBYQh30=\"}",
                        Base64.decode("CEMSICTqZbbZWoHqBuHv8Kd0/54ep/qdoNsabzriovjIcX0KGgQQwIQ9IO7o58vJLygBsgcQCg7/BgUAAAADbmlsFhCHfQ=="),
                        Base64.decode("CkYIQxIgJOplttlageoG4e/wp3T/nh6n+p2g2xpvOuKi+MhxfQoaBBDAhD0g7ujny8kvKAGyBxAKDv8GBQAAAANuaWwWEId9EkBtOSHLODFXO8Sp9qq+ewRN29D/g2uncSO2OAIe14YkzfmdBiu5c2G5u1avwoTLHdMTpxEPsHIhcM57vh8X6x4G")
                )
        );
    }

    @ParameterizedTest(name = "{index}: v{0}")
    @MethodSource("transactions")
    void invokeExpressionTransaction(int version, Base64String expression, byte chainId, PublicKey sender, Amount fee, long timestamp, List<Proof> proofs, Id expectedId, String expectedJson, byte[] expectedBody, byte[] expectedBytes) throws IOException {
        InvokeExpressionTransaction builtTx = InvokeExpressionTransaction
                .builder(expression)
                .chainId(chainId)
                .fee(fee)
                .timestamp(timestamp)
                .sender(sender)
                .version(version)
                .getUnsigned()
                .addProofs(proofs);

        assertAll("Tx created via builder must be equal to expected bytes",
                () -> assertThat(builtTx.bodyBytes()).isEqualTo(expectedBody),
                () -> assertThat(builtTx.id()).isEqualTo(expectedId),
                () -> assertThat(builtTx.toBytes()).isEqualTo(expectedBytes)
        );

        InvokeExpressionTransaction constructedTx = new InvokeExpressionTransaction(sender, expression,
                chainId, fee, timestamp, version, proofs);

        assertAll("Txs created via builder and constructor are equal",
                () -> assertThat(builtTx.bodyBytes()).isEqualTo(constructedTx.bodyBytes()),
                () -> assertThat(builtTx.id()).isEqualTo(constructedTx.id()),
                () -> assertThat(builtTx.toBytes()).isEqualTo(constructedTx.toBytes())
        );

        InvokeExpressionTransaction deserTx = InvokeExpressionTransaction.fromBytes(expectedBytes);

        assertAll("Tx must be deserializable from expected bytes",
                () -> assertThat(deserTx.expression()).isEqualTo(expression),

                () -> assertThat(deserTx.version()).isEqualTo(version),
                () -> assertThat(deserTx.chainId()).isEqualTo(chainId),
                () -> assertThat(deserTx.sender()).isEqualTo(sender),
                () -> assertThat(deserTx.fee()).isEqualTo(fee),
                () -> assertThat(deserTx.timestamp()).isEqualTo(timestamp),
                () -> assertThat(deserTx.proofs()).isEqualTo(proofs),

                () -> assertThat(deserTx.bodyBytes()).isEqualTo(expectedBody),
                () -> assertThat(deserTx.toBytes()).isEqualTo(expectedBytes),
                () -> assertThat(deserTx.id()).isEqualTo(expectedId)
        );

        Assertions.assertThat(JsonSerializer.JSON_MAPPER.readTree(Transaction.fromJson(expectedJson).toJson()))
                .describedAs("Tx serialized to json must be equal to expected")
                .isEqualTo(JsonSerializer.JSON_MAPPER.readTree(expectedJson));
    }
}
