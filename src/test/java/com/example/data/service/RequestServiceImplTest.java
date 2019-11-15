package com.example.data.service;

import cn.hutool.json.JSONUtil;
import com.example.data.model.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RequestServiceImplTest {

    @Autowired
    DataService requestService;
    @Test
    void getMessage() throws Exception {
        String data = "{\"test:\":\"吴\"}";
        Message message = requestService.getMessage(data);
        System.out.println(JSONUtil.toJsonStr(message));
    }
}