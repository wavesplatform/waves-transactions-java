package com.wavesplatform.transactions;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PublicKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.common.Proof;
import com.wavesplatform.transactions.serializers.json.JsonSerializer;
import im.mak.waves.crypto.base.Base64;
import im.mak.waves.transactions.common.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class GenesisTransactionTest {

    static PublicKey sender = PublicKey.as("AXbaBkJNocyrVpwqTzD4TpUY8fQ6eeRto9k1m2bNCzXV");
    static PublicKey emptySender = PublicKey.as(new byte[PublicKey.BYTES_LENGTH]);
    static long timestamp = 1600000000000L;
    static long amount = Long.MAX_VALUE;

    @BeforeAll
    static void beforeAll() {
        WavesConfig.chainId('R');
    }

    static Stream<Arguments> transactionsProvider() {
        Address address = Address.from(WavesConfig.chainId(), sender);
        return Stream.of(
                arguments(address, Id.as("4KEjTYoGriY1THot4fSr7bgnRUxFn6Ug6mBr8kmXgmK4WqYF1xxJFS8Bhe9WcQpkqEQo746bC1UqQtyjdAzwD44c"),
                        Proof.as("4KEjTYoGriY1THot4fSr7bgnRUxFn6Ug6mBr8kmXgmK4WqYF1xxJFS8Bhe9WcQpkqEQo746bC1UqQtyjdAzwD44c"),
                        Base64.decode("AQAAAXSHboAAAVInicmF011n1oXcDpi6LyFthr6PvAaApjB//////////w=="),
                        Base64.decode("AQAAAXSHboAAAVInicmF011n1oXcDpi6LyFthr6PvAaApjB//////////w=="),
                        "{\"type\":1,\"id\":\"4KEjTYoGriY1THot4fSr7bgnRUxFn6Ug6mBr8kmXgmK4WqYF1xxJFS8Bhe9WcQpkqEQo746bC1UqQtyjdAzwD44c\",\"fee\":0,\"timestamp\":1600000000000,\"signature\":\"4KEjTYoGriY1THot4fSr7bgnRUxFn6Ug6mBr8kmXgmK4WqYF1xxJFS8Bhe9WcQpkqEQo746bC1UqQtyjdAzwD44c\",\"recipient\":\"3M4qwDomRabJKLZxuXhwfqLApQkU592nWxF\",\"amount\":9223372036854775807}"
                )
        );
    }

    @ParameterizedTest(name = "{index}: v{0} to {1} of {2} wavelets")
    @MethodSource("transactionsProvider")
    void genesisTransaction(Address recipient, Id expectedId, Proof proof, byte[] expectedBody, byte[] expectedBytes,
                            String expectedJson) throws IOException {
        GenesisTransaction constructedTx = new GenesisTransaction(recipient, amount, timestamp);

        assertAll("Tx created via constructor must be equal to expected bytes",
                () -> Assertions.assertThat(constructedTx.proofs()).containsOnly(proof),
                () -> assertThat(constructedTx.bodyBytes()).isEqualTo(expectedBody),
                () -> Assertions.assertThat(constructedTx.id()).isEqualTo(expectedId),
                () -> assertThat(constructedTx.toBytes()).isEqualTo(expectedBytes)
        );

        GenesisTransaction deserTx = GenesisTransaction.fromBytes(expectedBytes);

        assertAll("Tx must be deserializable from expected bytes",
                () -> Assertions.assertThat(deserTx.recipient()).isEqualTo(recipient),
                () -> assertThat(deserTx.amount()).isEqualTo(amount),

                () -> assertThat(deserTx.version()).isEqualTo(1),
                () -> assertThat(deserTx.chainId()).isEqualTo(WavesConfig.chainId()),
                () -> Assertions.assertThat(deserTx.sender()).isEqualTo(emptySender),
                () -> Assertions.assertThat(deserTx.fee()).isEqualTo(Amount.of(0, AssetId.WAVES)),
                () -> assertThat(deserTx.timestamp()).isEqualTo(timestamp),
                () -> Assertions.assertThat(deserTx.proofs()).containsOnly(proof),

                () -> assertThat(deserTx.bodyBytes()).isEqualTo(expectedBody),
                () -> assertThat(deserTx.toBytes()).isEqualTo(expectedBytes),
                () -> Assertions.assertThat(deserTx.id()).isEqualTo(expectedId)
        );

        assertThat(constructedTx)
                .describedAs("Tx must be equal to deserialized tx")
                .isEqualTo(deserTx);

        Assertions.assertThat(JsonSerializer.JSON_MAPPER.readTree(Transaction.fromJson(expectedJson).toJson()))
                .describedAs("Tx serialized to json must be equal to expected")
                .isEqualTo(JsonSerializer.JSON_MAPPER.readTree(expectedJson));
    }

}
