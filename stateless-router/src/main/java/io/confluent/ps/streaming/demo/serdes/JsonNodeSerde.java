package io.confluent.ps.streaming.demo.serdes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class JsonNodeSerde implements Serde<JsonNode> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Serializer<JsonNode> serializer() {
        return (topic, data) -> {
            try {
                return mapper.writeValueAsBytes(data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Deserializer<JsonNode> deserializer() {
        return (topic, data) -> {
            try {
                return mapper.readTree(data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}