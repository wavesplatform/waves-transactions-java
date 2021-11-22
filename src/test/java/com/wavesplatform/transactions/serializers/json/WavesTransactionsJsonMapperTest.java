package com.wavesplatform.transactions.serializers.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.transactions.common.AssetId;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class WavesTransactionsJsonMapperTest {
    private final ObjectMapper objectMapper = new WavesTransactionsJsonMapper();

    private static Collection<Arguments> testCaseSource() {
        return Arrays.asList(
                Arguments.of("test-object-with-asset-id.json",TestObjectWithAssetId.class),
                Arguments.of("test-object-without-asset-id.json",TestObjectWithAssetId.class),
                Arguments.of("parent-object-with-asset-id.json",ParentObjectWithTestObjectWithAssetId.class)
        );
    }

    @ParameterizedTest
    @MethodSource("testCaseSource")
    <T extends Object> void testDeserializer(String jsonFile, Class<T> type) throws IOException {
        URL resource = WavesTransactionsJsonMapperTest.class.getResource("/json/"+jsonFile);

        T sut = this.objectMapper.readValue(resource,type);

        assertNotNull(sut);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "parent-object-with-asset-id.json"
    })
    void testDeserializerForParent(String jsonFile) throws IOException {
        URL resource = WavesTransactionsJsonMapperTest.class.getResource("/json/"+jsonFile);

        ParentObjectWithTestObjectWithAssetId sut = this.objectMapper.readValue(resource,ParentObjectWithTestObjectWithAssetId.class);

        assertNotNull(sut);
        assertNotNull(sut.getSomeprop());
        assertEquals(1337,sut.getSomeprop().getValue());
        assertEquals(AssetId.WAVES,sut.getSomeprop().getAssetId());
    }

    static class TestObjectWithAssetId {
        private long value;
        private AssetId assetId;

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public AssetId getAssetId() {
            return assetId;
        }

        public void setAssetId(AssetId assetId) {
            this.assetId = assetId;
        }
    }

    static class ParentObjectWithTestObjectWithAssetId {
        private TestObjectWithAssetId someprop;

        public TestObjectWithAssetId getSomeprop() {
            return someprop;
        }

        public void setSomeprop(TestObjectWithAssetId someprop) {
            this.someprop = someprop;
        }
    }
}