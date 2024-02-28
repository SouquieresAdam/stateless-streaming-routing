package io.confluent.ps.streaming.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.confluent.ps.streaming.demo.models.FreeFormSyndigoPojo;
import io.confluent.ps.streaming.demo.models.StructuredSyndigoPojo;
import io.confluent.ps.streaming.demo.serdes.JsonNodeSerde;
import io.confluent.ps.streaming.demo.serdes.JsonSerde;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.serialization.Serde;

import java.util.Map;

public interface Constants {
    interface StateStores {
        // Stateless streaming
    }

    interface InternalTopics {
      String DLQ_TOPIC_NAME = "entity-change-router-stream";

    }

    interface PublicTopic {

        // in
        String PRODUCT_PIM_EXP = "products-pim-exp";

        // out
        String SKU_CHANGE = "sku-changed";
        String TRADENAME_CHANGE = "tradename-changed";
        String TRADEGRADE_CHANGE = "tradegrade-changed";
    }

    interface Serdes {
        Serde<StructuredSyndigoPojo> SYNDIGO_POJO_SERDE = new JsonSerde<>() {
            @Override
            protected TypeReference<StructuredSyndigoPojo> getTypeReference() {
                return new TypeReference<>() {
                };
            }
        };

        Serde<FreeFormSyndigoPojo> SYNDIGO_FREEFORM_POJO_SERDE = new JsonSerde<>() {
            @Override
            protected TypeReference<FreeFormSyndigoPojo> getTypeReference() {
                return new TypeReference<>() {
                };
            }
        };

        Serde<JsonNode> JSON_NODE_SERDE = new JsonNodeSerde();
    }


    static String getTopicName(String env, String name) {

        if(StringUtils.isEmpty(env)) {
            return name;
        }
        return String.join(".", env, name);
    }
}
