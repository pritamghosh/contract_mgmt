package com.pns.contractmanagement.dao;

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

	private final String databaseName;
	private final MongoDatabase db;

	public MongoCollectionUtil(final MongoClient mongoClient,
			@Value("${app.database.name:userdb}") String databaseName) {
		this.databaseName = databaseName;
		db = mongoClient.getDatabase(databaseName);
	}

	public <T> MongoCollection<T> getCollection(final String collectionName, final Class<T> collectionType) {
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		return db.getCollection(collectionName, collectionType).withCodecRegistry(pojoCodecRegistry);
	}

	public MongoCollection<Document> getCollection(final String collectionName) {
		return db.getCollection(collectionName);
	}

	/**
	 * @return the db
	 */
	public MongoDatabase getDb() {
		return db;
	}

	public String getDatabaseName() {
		return databaseName;
	}

}
