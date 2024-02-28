package io.confluent.ps.streaming.demo.models;


import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FreeFormSyndigoPojo {

    private String id;
    private String name;
    private String type;
    private Properties properties;

    @JsonProperty("data")
    private Payload data;

    // Getters and Setters

    public static class Properties {
        @JsonProperty("createdService")
        private String createdService;
        @JsonProperty("createdBy")
        private String createdBy;
        @JsonProperty("createdDate")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
        private Date createdDate;
        @JsonProperty("modifiedService")
        private String modifiedService;
        @JsonProperty("modifiedBy")
        private String modifiedBy;
        @JsonProperty("modifiedDate")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
        private Date modifiedDate;

        // Getters and Setters
    }

    @Data
    public static class Payload {
        private Map<String, Attributes.Attribute> attributes;
        private Relationships relationships;

        // Getters and Setters

        @Data
        public static class Attributes {
            private Map<String, Attribute> attributes;


            @JsonAnySetter // This annotation tells Jackson to call this method for any unrecognized fields.
            public void setDynamicAttribute(String name, Attribute value) {
                if (this.attributes == null) {
                    this.attributes = new HashMap<>();
                }
                this.attributes.put(name, value);
            }

            @JsonAnyGetter
            // This annotation is for serialization, to include all dynamic attributes in the serialized JSON.
            public Map<String, Attribute> getDynamicAttributes() {
                return attributes;
            }




            // Getters and Setters

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Attribute {
                private List<Value> values;

                // Getters and Setters
                @Data
                public static class Value {
                    private String id;
                    private String value;
                    private String locale;
                    private String source;
                    private String os;
                    private String ostype;
                    private String osid;

                    private Map<String, String> properties;

                    // Getters and Setters
                }
            }
        }

        @Data
        public static class Relationships {
            private Map<String, List<Relationship>> relationships;

            // Getters and Setters
            @JsonAnySetter // This annotation tells Jackson to call this method for any unrecognized fields.
            public void setDynamicRelations(String name, List<Relationship> value) {
                if (this.relationships == null) {
                    this.relationships = new HashMap<>();
                }

                this.relationships.put(name, value);
            }

            @JsonAnyGetter
            // This annotation is for serialization, to include all dynamic relations in the serialized JSON.
            public Map<String, List<Relationship>> getDynamicRelations() {
                return relationships;
            }


            @Data
            public static class Relationship {
                private String id;
                private RelTo relTo;
                private Map<String, String> properties;

                // Getters and Setters

                @Data
                public static class RelTo {
                    private String id;
                    private String type;

                    // Getters and Setters
                }
            }
        }
    }
}