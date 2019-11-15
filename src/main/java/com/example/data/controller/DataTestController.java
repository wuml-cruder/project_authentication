package com.example.data.controller;

import com.example.data.model.Message;
import com.example.data.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *数据请求
 * @author wuml
 */
@RestController
public class DataTestController {

    @Autowired
    DataService dataService;

    /**
     * 接受反馈
     * @param message
     * @return
     */
    @PostMapping(value = "acceptData")
    public String acceptData(@RequestBody Message message) throws Exception {
        dataService.analysisData(message);
        System.out.println(message.toString());
        return "";
    }



}
