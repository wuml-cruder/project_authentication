package com.example.data.utils;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import org.apache.tomcat.util.codec.binary.Base64;

/**
 * @author wuml
 * @time 2019/11/12
 */
public class RSAUtil {

    /**
     * RSA最大加密明文大小
     * 2048 / 8 - 11 (需要加密的明文长度 = 密钥长度 - 11)
     * -11的原因，RSA采用padding,当内容不足234字节时，自动填充，填充的部分占用字节，参与加密
     */
    private static final int MAX_ENCRYPT_BLOCK = 234;

    /**
     * RSA最大解密密文大小
     * 2048 / 8
     */
    private static final int MAX_DECRYPT_BLOCK = 256;
    /**
     *算法名
     */
    public static final String ALGORITHM = "RSA";
    /**
     *加密算法/分组加密模式/分组填充方式 RSA/ECB/PKCS1Padding
     */
    private static final String RSA_ECB_PKCS1PADDING = "RSA/ECB/PKCS1Padding";
    /**
     * 密钥长度(蚂蚁金服指定)
     */
    public static final int KEY_SIZE = 2048;
    /**
     * 签名算法名（蚂蚁金服指定）
     */
    private static final String SIGN_ALGORITHM = "SHA256withRSA";

    /**
     * 获取密钥对
     *
     * @return 密钥对
     */
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
        generator.initialize(KEY_SIZE);
        return generator.generateKeyPair();
    }

    /**
     * 获取私钥
     *
     * @param privateKey 私钥字符串
     * @return
     */
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        byte[] decodedKey = Base64.decodeBase64(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 获取公钥
     *
     * @param publicKey 公钥字符串
     * @return
     */
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        byte[] decodedKey = Base64.decodeBase64(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * RSA加密
     *
     * @param data 待加密数据
     * @param publicKey 公钥
     * @return
     */
    public static String encrypt(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = setting(data, cipher,MAX_ENCRYPT_BLOCK);
        // 获取加密内容使用base64进行编码,并以UTF-8为标准转化成字符串
        // 加密后的字符串
        return new String(Base64.encodeBase64String(encryptedData));
    }

    /**
     * RSA解密
     *
     * @param data 待解密数据
     * @param privateKey 私钥
     * @return
     */
    public static byte[] decrypt(String data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] dataBytes = Base64.decodeBase64(data);
        byte[] decryptedData = setting(dataBytes,cipher,MAX_DECRYPT_BLOCK);
        // 解密后的内容 
        return decryptedData;
    }

    private static byte[] setting(byte[] data,Cipher cipher,int size) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int inputLen = data.length;
            int offset = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offset > 0) {
                if (inputLen - offset > size) {
                    cache = cipher.doFinal(data, offset, size);
                } else {
                    cache = cipher.doFinal(data, offset, inputLen - offset);
                }
                out.write(cache, 0, cache.length);
                i++;
                offset = i * size;
            }
            return out.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            out.close();
        }
    }
    /**
     * 签名
     *
     * @param data 待签名数据
     * @param privateKey 私钥
     * @return 签名
     */
    public static String sign(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        return new String(Base64.encodeBase64(signature.sign()));
    }

    /**
     * 验签
     *
     * @param srcData 原始字符串
     * @param publicKey 公钥
     * @param sign 签名
     * @return 是否验签通过
     */
    public static boolean verify(String srcData, PublicKey publicKey, String sign) throws Exception {
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(srcData.getBytes());
        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }

    public static void main(String[] args) {
        try {
            // 生成密钥对
            KeyPair keyPair = getKeyPair();
            String privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
            String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
            System.out.println("私钥:" + privateKey);
            System.out.println("公钥:" + publicKey);
            System.out.println("私钥长度:" + privateKey.length());
            System.out.println("公钥长度:" + publicKey.length());
            // RSA加密
            String data = "{\"test:\":\"吴\"}";
            String encryptData = encrypt(data.getBytes(), getPublicKey(publicKey));
            System.out.println("加密后内容:" + encryptData);
            // RSA解密
            byte[] decrypt = decrypt(encryptData, getPrivateKey(privateKey));
            System.out.println("解密后内容:" + new String(decrypt));

            // RSA签名
            String sign = sign(data, getPrivateKey(privateKey));
            // RSA验签
            boolean result = verify(data, getPublicKey(publicKey), sign);
            System.out.print("验签结果:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("加解密异常");
        }
    }
}