package com.ambition.bi;

import com.ambition.bi.config.WxOpenConfig;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 主类测试
 *
 * @author ambition
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    private WxOpenConfig wxOpenConfig;

    @Test
    void contextLoads() {
        System.out.println(wxOpenConfig);
    }

}
