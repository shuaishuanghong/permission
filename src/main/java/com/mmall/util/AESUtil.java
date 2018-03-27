package com.mmall.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AESUtil {

    private  static final  String secreKey="1234567890123456";
    private  static final  String algorithm="AES";


    public  static  String encrypt(String originalSource,String secreKey, String algorithm, boolean isEncrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
          byte[] originalBytes=originalSource.getBytes();

        // secretKey加密的颜值，随便定义一串字符串16位
          byte[] secretKeyBytes=secreKey.getBytes();
        //algorithm:固定值：AES，代表这个加密算法   secretKeySpec ：要new一个这个出来,参数为key加密颜值的二//进制数据，和固定在AES ，下面cipher这个初始化//的时候要用//到
          SecretKeySpec secretKeySpec=new SecretKeySpec(secretKeyBytes,algorithm);
        //得到cipher实例
        Cipher cipher=Cipher.getInstance(algorithm);
        if(isEncrypt){
                //cipher这个初始化(加密模式的)
                cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
        }else {
                //cipher这个初始化(解密模式的)
                cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
        }
        //开始调用实例进行加密（解密）放回加密后的内容
        byte[] encrypBtyes= cipher.doFinal(originalBytes);
        byte[] encrypBtyesBase64= Base64.getEncoder().encode(encrypBtyes);

        return new String(encrypBtyesBase64);
    }

    public  static  String decoder(String encryptString,String secreKey, String algorithm, boolean isEncrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] encryBytes=Base64.getDecoder().decode(encryptString);
        byte[] secretKeyBytes =secreKey.getBytes();
        SecretKeySpec secretKeySpec=new SecretKeySpec(secretKeyBytes,algorithm);

        Cipher cipher=Cipher.getInstance(algorithm);
        if(isEncrypt){
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
        }else {
            cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
        }
       byte[] originalSource= cipher.doFinal(encryBytes);

        return  new String(originalSource);
    }

    private  static  byte[] cipher(boolean isEncrypt,byte[] source,String secreKey,String algorithm) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] secretKeyBytes = secreKey.getBytes();
        SecretKeySpec secretKeySpec=new SecretKeySpec(secretKeyBytes,algorithm);
        Cipher cipher=Cipher.getInstance(algorithm);
        if(isEncrypt){
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
        }else {
            cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
        }
        return  cipher.doFinal(source);
    }

    public static  void main(String[] aa) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        String ss=encrypt("我是李攀哦",secreKey,algorithm,true);
        System.out.println(ss);

        String sss=decoder(ss,secreKey,algorithm,false);
        System.out.println(sss);
    }
}
