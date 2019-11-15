package com.example.data.utils;

import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;

public class AESUtil {
    private AESUtil(){}

    /**
     * 获取密钥
     * @return
     * @throws Exception
     */
    public static Key getKey() throws Exception{
        //生成KEY
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(new SecureRandom());
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] byteKey = secretKey.getEncoded();

        //转换KEY
        Key key = new SecretKeySpec(byteKey,"AES");
        return key;
    }
    /**
     * 通过密钥对数据加密
     */
    public static String encodeData(Key key,String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] result = cipher.doFinal(data.getBytes());
        System.out.println("加密后：" + new BASE64Encoder().encode(result));
        return new BASE64Encoder().encode(result);
    }
    /**
     * 通过密钥解密数据
     */
    public static String decodeData(Key key,String encodeData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] result = cipher.doFinal(encodeData.getBytes());
        result = cipher.doFinal(result);
        System.out.println("解密后：" + new String(result));
        return new String(result);
    }

    public static void main(String[] args) throws Exception {
        String json = "{\"test:\":\"吴\"}";
        Key key= AESUtil.getKey();
        AESUtil.decodeData(key,AESUtil.encodeData(key,json));
    }

}
