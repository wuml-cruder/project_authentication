package com.example.data.utils;

import sun.misc.BASE64Encoder;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 加解密工具类
 */
public class DataUtil {

    public static final String KEY = "RSA";
    public static final int KEY_SIZE = 2048;
    /**
     * 生成密钥
     */
    public static Map getKeys(){
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY);
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            Map keyMap = new HashMap(2);
            keyMap.put("publicKey",new BASE64Encoder().encode(keyPair.getPublic().getEncoded()));
            keyMap.put("privateKey",new BASE64Encoder().encode(keyPair.getPrivate().getEncoded()));
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取公钥Key
     * @param publicKey
     * @param key
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String publicKey,String key) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        KeyFactory instance = KeyFactory.getInstance(key);
        PublicKey publicKey1 = instance.generatePublic(x509EncodedKeySpec);
        return publicKey1;
    }

    /**
     * 获取私钥Key
     * @param privateKey
     * @param key
     * @return
     * @throws Exception
     */

    private static PrivateKey getPrivateKey(String privateKey,String key) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
        KeyFactory instance = KeyFactory.getInstance(key);
        PrivateKey privateKey1 = instance.generatePrivate(pkcs8EncodedKeySpec);
        return privateKey1;
    }



    private DataUtil(){}

}
