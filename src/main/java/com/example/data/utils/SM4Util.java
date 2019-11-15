package com.example.data.utils;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

/**
 * SM4生成密钥
 */
public class SM4Util {
    private SM4Util(){}

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String ENCODING = "UTF-8";
    private static final String ALGORITHM_NAME = "SM4";
    //设置算法     加密算法/分组加密模式/分组填充方式
    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";
    public static final int DEFAULT_KEY_SIZE = 128;

    /**
     * 生成ECB暗号
     * @param algorithmName
     * @param mode
     * @param key
     * @return
     * @throws Exception
     */
    public static Cipher generateEcbCipher(String algorithmName,int mode,byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName,BouncyCastleProvider.PROVIDER_NAME);
        Key secretKeySpec = new SecretKeySpec(key, ALGORITHM_NAME);
        cipher.init(mode,secretKeySpec);
        return cipher;
    }
    /**
     * 加密 base64编码
     */
    public static byte[] encodeData(byte[] key,byte[] json) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(json);
    }
    /**
     * 解密
     */
    public static String decodeData(byte[] key,byte[] json) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.DECRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(json);
        return new String(bytes);
    }

    /**
     * 生成16位随机key
     */
    public static byte[] getKey(){
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes =new byte[16];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    public static void main(String[] args) throws Exception {
        String json = "{\"test:\":\"吴\"}";
        byte[] key = getKey();
        byte[] bytes = encodeData(key, json.getBytes("utf-8"));

        //System.out.println(new String(bytes,"utf-8"));
        System.out.println(decodeData(key,bytes));

    }


}
