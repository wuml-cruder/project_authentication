package com.example.data.model;

import lombok.Data;

/**
 * 请求报文
 */
@Data
public class Message {
    /**
     * 用于标识公钥的版本，支持公钥更新升级
     */
    private int publicKeyVersion;
    /**
     *客户端ID
     */
    private String clientId;
    /**
     * 报文加密后的密文
     */
    private String data;
    /**
     * 加密报文密钥的密文
     */

    private String keyData;
    /**
     * 参数签名
     */
    private String sign;
    /**
     * 请求编号
     */
    private String requestNo;
}
