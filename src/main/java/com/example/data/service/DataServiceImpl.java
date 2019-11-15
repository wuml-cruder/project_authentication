package com.example.data.service;

import com.example.data.model.Message;
import com.example.data.utils.ConstantsUtil;
import com.example.data.utils.RSAUtil;
import com.example.data.utils.SM4Util;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.PrivateKey;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@Service
public class DataServiceImpl implements DataService{

    @Override
    public Message getMessage(String data) throws Exception {

        Message message = new Message();
        message.setClientId(ConstantsUtil.CLIENTID);
        message.setPublicKeyVersion(ConstantsUtil.PUBLIC_KEY_VERSION);
        //态生成对称密钥
        byte[] key = SM4Util.getKey();
        //用密钥加密报文数据 aesEncrvptData为数据密文
        message.setData(Base64.getEncoder().encodeToString(SM4Util.encodeData(key,data.getBytes("GBK"))));
        //使用对方公钥加密密钥
        message.setKeyData(getEncrvptDataByPublicKey(key));
        //根据上面的数据生成签名
        String sign = getSign(message);
        if(!StringUtils.isEmpty(sign)){
            message.setSign(sign);
        }
        message.setRequestNo(UUID.randomUUID().toString());
        System.out.println("待发送至第三方机构的json数据："+message.toString());
        return message;
    }

    @Override
    public void analysisData(Message message) throws Exception {
        //第一步 验签
        if(Objects.nonNull(message)){
            String realSign = message.getPublicKeyVersion()+message.getClientId()+message.getData()+message.getKeyData();
            String sign = message.getSign();
            if(!StringUtils.isEmpty(sign)){
                boolean verify = RSAUtil.verify(realSign, RSAUtil.getPublicKey(ConstantsUtil.MAYI_PUBLIC_KEY), sign);
                if(verify){
                    //签名通过，解析密钥密文，通过自己的私钥进行解密数据
                    PrivateKey privateKey = RSAUtil.getPrivateKey(ConstantsUtil.MINDE_PRIVATE_KEY);
                    //获取密钥明文
                    byte[] decrypt = RSAUtil.decrypt(message.getKeyData(), privateKey);
                    //通过密钥解密报文
                    byte[] decode = Base64.getDecoder().decode(message.getData());
                    String jsonStr = SM4Util.decodeData(decrypt, decode);
                    System.out.println("获取的数据到底是啥:"+jsonStr);
                }
            }
        }
    }

    /**
     * 使用对方公钥加密密钥
     * @return 加密字符串
     */
    private String getEncrvptDataByPublicKey(byte[] byteKey) throws Exception {

        String encryptData = RSAUtil.encrypt(byteKey, RSAUtil.getPublicKey(ConstantsUtil.MAYI_PUBLIC_KEY));
        System.out.println("加密后内容:" + encryptData);
        return encryptData;
    }

    /**
     * 根据加密信息生成签名
     * @param message
     * @return
     */
    private String getSign(Message message) throws Exception {
        if(Objects.nonNull(message)){
            String signData = message.getPublicKeyVersion()+message.getClientId()+message.getData()+message.getKeyData();
            //采用个人的私钥进行签名
            String sign = RSAUtil.sign(signData, RSAUtil.getPrivateKey(ConstantsUtil.MINDE_PRIVATE_KEY));
            return sign;
        }
        return "";
    }
}
