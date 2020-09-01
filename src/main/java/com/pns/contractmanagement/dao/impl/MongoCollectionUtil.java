package com.pns.contractmanagement.dao.impl;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
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

	private final MongoDatabase contractDatabase;
	private final MongoDatabase userProfileDatabase;

	public MongoCollectionUtil(final MongoClient mongoClient,
			@Value("${app.database.name.contract:contract}") String contractDb,@Value("${app.database.name.userprofile:userprofile}") String userProfile) {
		this.userProfileDatabase =  mongoClient.getDatabase(userProfile);
        contractDatabase = mongoClient.getDatabase(contractDb);
	}

	public <T> MongoCollection<T> getContractCollection(final String collectionName, final Class<T> collectionType) {
		return getCollection(collectionName, collectionType,contractDatabase);
	}

    private <T> MongoCollection<T> getCollection(final String collectionName, final Class<T> collectionType, MongoDatabase database) {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		return database.getCollection(collectionName, collectionType).withCodecRegistry(pojoCodecRegistry);
    }

	public MongoCollection<Document> getContractCollection(final String collectionName) {
		return contractDatabase.getCollection(collectionName);
	}
	
	public <T> MongoCollection<T> getProfileCollection(final String collectionName, final Class<T> collectionType) {
        return getCollection(collectionName, collectionType,userProfileDatabase);
    }

	/**
	 * @return the db
	 */
	public MongoDatabase getContractDatabase() {
		return contractDatabase;
	}
	
	public MongoDatabase getUserProfileDatabase() {
        return userProfileDatabase;
    }


}
