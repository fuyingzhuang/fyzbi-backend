package com.ambition.bi.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ambition
 * @date 2023/11/16 13:51
 */
@SpringBootTest
class AiManagerTest {
    @Resource
    private AiManager aiManager;

    @Test
    void doChat() {
        String result = aiManager.doChat(1654785040361893889L,"你好");
        System.out.println(result);
    }

}
