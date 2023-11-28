package com.ambition.bi.manager.mongodb;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class MongoDBUtil {

    @Value("${spring.data.mongodb.uri}")
    private String mongoURI;

    @Value("${spring.data.mongodb.database}")
    private String mongoDBName;

    @Value("${spring.data.mongodb.table}")
    private String mongoTable;

    public MongoClient getMongoClient() {
        return MongoClients.create(mongoURI);
    }

    public MongoDatabase getMongoDatabase(String dbName) {
        MongoClient mongoClient = getMongoClient();
        return mongoClient.getDatabase(dbName).withCodecRegistry(CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        ));
    }

    public <T> MongoCollection<T> getMongoCollection(Class<T> clazz) {
        MongoDatabase mongoDatabase = getMongoDatabase(mongoDBName);
        return mongoDatabase.getCollection(mongoTable, clazz);
    }
}
