package com.wavesplatform.transactions;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PublicKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Numeric;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class EthereumTransactionTest {
    static Stream<Arguments> transfers() {
        return Stream.of(
                arguments(
                        Amount.of(2_00000000L),
                        PublicKey.as(""),
                        Address.as(""),
                        new byte[0],
                        new byte[0]
                ),
                arguments(
                        Amount.of(2_00000000L, AssetId.as("")),
                        PublicKey.as(""),
                        Address.as(""),
                        new byte[0],
                        Hex.decode("")
                )
        );
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("transfers")
    void ethereumTransaction(
            Amount transferAmount,
            PublicKey sender,
            Address recipient,
            byte[] expectedTxBytes,
            byte[] expectedDataBytes) {

    }

    private static final String MNEMONIC = "river denial defense vintage umbrella upon lumber bleak wave wasp demise behind";

    static Stream<Arguments> transferSigning() {
        return Stream.of(
                arguments(
                        (byte) 'E',
                        0,
                        "0xbb2ed0C3435dc6B80eD364f569E741e5Cd782995",
                        Amount.of(15_00000000L)
                ),
                arguments(
                        (byte) 'C',
                        1,
                        "0xcd770D905e3DA77EcA631459b5782F276cEea9e6",
                        Amount.of(2000, AssetId.as("DG2xFkPdDwKUoBkzGAhQtLpSGzfXLiCYPEzeKH2Ad24p"))
                )
        );
    }

    @ParameterizedTest
    @MethodSource("transferSigning")
    void testTransferSigning(byte chainId, int index, String expectedEthereumAddress, Amount amount) {
        Credentials credentials = MetamaskHelper.generateCredentials(MNEMONIC, index);

        assertThat(credentials.getAddress()).isEqualToIgnoringCase(expectedEthereumAddress);
        Address expectedWavesSenderAddress = Address.fromPart(chainId, Numeric.hexStringToByteArray(expectedEthereumAddress));

        System.out.println(expectedWavesSenderAddress);

        EthereumTransaction transfer = EthereumTransaction.transfer(
                Address.fromPart(chainId, new byte[20]),
                amount,
                10,
                chainId,
                100000L,
                System.currentTimeMillis(),
                credentials.getEcKeyPair()
        );

        assertThat(transfer.sender().address(chainId)).isEqualTo(expectedWavesSenderAddress);
    }
}
