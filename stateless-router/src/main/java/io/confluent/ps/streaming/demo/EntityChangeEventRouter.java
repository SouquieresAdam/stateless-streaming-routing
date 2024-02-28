package io.confluent.ps.streaming.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.michelin.kstreamplify.initializer.KafkaStreamsStarter;
import io.confluent.ps.streaming.demo.models.StructuredSyndigoPojo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static io.confluent.ps.streaming.demo.Constants.InternalTopics;
import static io.confluent.ps.streaming.demo.Constants.PublicTopic.*;
import static io.confluent.ps.streaming.demo.Constants.getTopicName;

@Component
@Slf4j
public class EntityChangeEventRouter extends KafkaStreamsStarter {

    private final DomainConfiguration domainconfiguration;

    public EntityChangeEventRouter(DomainConfiguration domainconfiguration) {
        this.domainconfiguration = domainconfiguration;
    }

    @Override
    public void topology(StreamsBuilder streamsBuilder) {
        Serde<String> stringSerde = Serdes.String();
        Serde<JsonNode> entityChangedSerde = Constants.Serdes.JSON_NODE_SERDE;

        var changeStream = streamsBuilder
                .stream(getTopicName(domainconfiguration.getEnv(), PRODUCT_PIM_EXP), Consumed.with(stringSerde, entityChangedSerde));

        changeStream
                .filter((k,v)-> "sku".equals(v.get("type").asText()))
                .to(getTopicName(domainconfiguration.getEnv(),  SKU_CHANGE), Produced.with(stringSerde, entityChangedSerde));

        changeStream
                .filter((k,v)-> "tradename".equals(v.get("type").asText()))
                .to(getTopicName(domainconfiguration.getEnv(),  TRADENAME_CHANGE), Produced.with(stringSerde, entityChangedSerde));
        changeStream
                .filter((k,v)-> "tradegrade".equals(v.get("type").asText()))
                .to(getTopicName(domainconfiguration.getEnv(), TRADEGRADE_CHANGE), Produced.with(stringSerde, entityChangedSerde));
    }

    @Override
    public String dlqTopic() {
        return InternalTopics.DLQ_TOPIC_NAME;
    }
}
