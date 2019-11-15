package com.example.data.service;

import com.example.data.model.Message;

public interface DataService {
    /**
     * 生成请求数据
     */
    Message getMessage(String data) throws Exception;

    /**
     * 解析数据
     * @param message
     */
    void analysisData(Message message) throws Exception;
}
