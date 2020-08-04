package com.pns.contractmanagement.dao;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 *
 */
@Component
public class MongoCollectionUtil {

    // @Value("${app.database:userdb}")
    private final String databaseName = "userdb";
    private final MongoDatabase db;

    public MongoCollectionUtil(final MongoClient mongoClient) {
        db = mongoClient.getDatabase(databaseName);
    }

    public <TDocument> MongoCollection<TDocument>getCollection(final String collectionName, final Class<TDocument> collectionType) {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        return db.getCollection(collectionName, collectionType).withCodecRegistry(pojoCodecRegistry );
    }
    
    public <TDocument> MongoCollection<Document> getCollection(final String collectionName) {
        return db.getCollection(collectionName);
    }

    /**
     * @return the db
     */
    public MongoDatabase getDb() {
        return db;
    }
    
    

}
