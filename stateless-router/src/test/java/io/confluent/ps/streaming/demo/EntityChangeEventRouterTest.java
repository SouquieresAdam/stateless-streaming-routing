package io.confluent.ps.streaming.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michelin.kstreamplify.KafkaStreamsStarterTest;
import com.michelin.kstreamplify.initializer.KafkaStreamsStarter;
import com.michelin.kstreamplify.utils.TopicWithSerde;
import io.confluent.ps.streaming.demo.models.StructuredSyndigoPojo;
import org.apache.avro.data.Json;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityChangeEventRouterTest extends KafkaStreamsStarterTest {
    private final Serde<String> stringSerde = Serdes.String();

    private TestInputTopic<String, JsonNode> inputTopic;

    private TestOutputTopic<String, JsonNode> outputSku;
    private TestOutputTopic<String, JsonNode> outputTradeName;
    private TestOutputTopic<String, JsonNode> outputTradeGrade;

    private final DomainConfiguration configuration;

    EntityChangeEventRouterTest() {
        configuration = new DomainConfiguration();
        configuration.setEnv("dev");
        configuration.setDomain("dsl");
    }

    @Override
    protected KafkaStreamsStarter getKafkaStreamsStarter() {
        return new EntityChangeEventRouter(configuration);
    }

    @BeforeEach
    void setUp() {

        Serde<StructuredSyndigoPojo> entityChangedSerde = Constants.Serdes.SYNDIGO_POJO_SERDE;

        Constants.Serdes.SYNDIGO_POJO_SERDE.configure(new HashMap<>(), false);


        inputTopic = createInputTestTopic(
                new TopicWithSerde<>(Constants.getTopicName(configuration.getEnv(), Constants.PublicTopic.PRODUCT_PIM_EXP), stringSerde,  Constants.Serdes.JSON_NODE_SERDE));
        outputSku = createOutputTestTopic(
                new TopicWithSerde<>(Constants.getTopicName(configuration.getEnv(), Constants.PublicTopic.SKU_CHANGE), stringSerde, Constants.Serdes.JSON_NODE_SERDE));
        outputTradeGrade = createOutputTestTopic(
                new TopicWithSerde<>(Constants.getTopicName(configuration.getEnv(), Constants.PublicTopic.TRADEGRADE_CHANGE), stringSerde, Constants.Serdes.JSON_NODE_SERDE));
        outputTradeName = createOutputTestTopic(
                new TopicWithSerde<>(Constants.getTopicName(configuration.getEnv(), Constants.PublicTopic.TRADENAME_CHANGE), stringSerde, Constants.Serdes.JSON_NODE_SERDE));
    }

    public static String loadJsonAsString(String fileName) throws IOException {
        // Build the path to the file in the test/resources directory
        String path = "src/test/resources/" + fileName;
        // Read the file content and return it as a string
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    @Test
    public void shouldRouteSku() throws IOException {
        // Given
        String inJson = loadJsonAsString("exampleSKU.json");
        JsonNode skuChangeEvent = new ObjectMapper().readTree(inJson);


        // When
        inputTopic.pipeInput(skuChangeEvent, Instant.ofEpochSecond(0));


        var dlqList = dlqTopic.readValuesToList();
        assertEquals(0, dlqList.size());

        var skuResult = outputSku.readValuesToList();
        assertEquals(1, skuResult.size());
        assertEquals(skuChangeEvent, skuResult.get(0));
    }

    @Test
    public void shouldRouteTradename() throws IOException {
        // Given
        String inJson = loadJsonAsString("exampleTradename.json");
        JsonNode skuChangeEvent = new ObjectMapper().readTree(inJson);


        // When
        inputTopic.pipeInput(skuChangeEvent, Instant.ofEpochSecond(0));


        var dlqList = dlqTopic.readValuesToList();
        assertEquals(0, dlqList.size());

        var skuResult = outputTradeName.readValuesToList();
        assertEquals(1, skuResult.size());
        assertEquals(skuChangeEvent, skuResult.get(0));

    }

    @Test
    public void shouldRouteTradegrade() throws IOException {
        // Given
        String inJson = loadJsonAsString("exampleTradegrade.json");
        JsonNode skuChangeEvent = new ObjectMapper().readTree(inJson);


        // When
        inputTopic.pipeInput(skuChangeEvent, Instant.ofEpochSecond(0));

        var dlqList = dlqTopic.readValuesToList();
        assertEquals(0, dlqList.size());

        var skuResult = outputTradeGrade.readValuesToList();
        assertEquals(1, skuResult.size());
        assertEquals(skuChangeEvent, skuResult.get(0));
    }
}