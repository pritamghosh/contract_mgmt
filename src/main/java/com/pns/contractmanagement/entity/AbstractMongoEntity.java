package com.pns.contractmanagement.entity;

import java.io.IOException;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 *
 */
public abstract class AbstractMongoEntity {

    @BsonId
    @JsonProperty("_id")
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private ObjectId oid;

    public final void setId(String id) {
        this.oid = id == null ? new ObjectId() : new ObjectId(id);
    }
    
    /**
     * @return the oid
     */
    @JsonIgnore
    public ObjectId getOid() {
        return oid;
    }

    public void setOid(ObjectId oid) {
        this.oid = oid;
    }
    
    /**
     * @return the id
     */
    @BsonIgnore
    public String getId() {
        return oid.toHexString();
    }
    
    public class ObjectIdDeserializer extends JsonDeserializer<ObjectId>{
        
        public ObjectIdDeserializer() { 
            super(); 
        } 
     
        /** {@inheritDoc} */
        @Override
        public ObjectId deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
            JsonNode node = jp.getCodec().readTree(jp);
            String oid = node.get("$oid").asText();
            return new ObjectId(oid);
        }
        
    }
}
