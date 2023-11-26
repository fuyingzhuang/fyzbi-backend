package com.ambition.bi.test;

import com.ambition.bi.manager.mongodb.MongoDBUtil;
import com.ambition.bi.model.entity.mongo.AnalyzeRawData;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import org.bson.conversions.Bson;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @author Ambition
 * @date 2023/11/26 20:09
 * 测试MongoDB
 */
@SpringBootTest
public class MongoDB {


    @Resource
    private MongoDBUtil mongoDBUtil;

    /**
     * 测试向mongodb中插入数据
     */
    @Test
    public void testMongoDB() {
        InsertOneResult insertOneResult = mongoDBUtil.getMongoCollection(AnalyzeRawData.class).insertOne(new AnalyzeRawData(1, "测试数据"));

        System.out.println(insertOneResult);
    }

    /**
     * 测试向mongodb根据id查询数据
     */
    @Test
    public void testMongoDBFindById() {
        long id = 1L; // 要查询的id
        // 注意查询条件是_id 而不是id
        Bson query = Filters.eq("_id", id);
        System.out.println("mongoDBUtil");
        System.out.println(mongoDBUtil);

        AnalyzeRawData analyzeRawData = mongoDBUtil.getMongoCollection(AnalyzeRawData.class).find(query).first();
        System.out.println(analyzeRawData);
    }

}
