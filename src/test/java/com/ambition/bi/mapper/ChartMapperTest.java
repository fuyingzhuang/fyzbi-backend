package com.ambition.bi.mapper;

import cn.hutool.json.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ambition
 * @date 2023/11/22 14:07
 * @desc 单元测试 测试查询sql
 */
@SpringBootTest
class ChartMapperTest {
    @Resource
    private ChartMapper chartMapper;

    @Test
    void testQuery() {
        List<Map<String, Object>> maps = chartMapper.queryChartData("select * from chart_1");
        for (Map<String, Object> map : maps) {
            System.out.println("map = " + map);
        }
        // 将 maps 转成 JSON 数据
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(maps);
            System.out.println("JSON: " + json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


}
