package com.spriv.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class CriptoUtil {

    private String charsetName = "UTF8";
    private String algorithm = "AES";
    private int base64Mode = Base64.DEFAULT;

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public int getBase64Mode() {
        return base64Mode;
    }

    public void setBase64Mode(int base64Mode) {
        this.base64Mode = base64Mode;
    }
    
    public  String encrypt(String key, String data) throws Exception
    {
	    String dataBytes = Base64.encodeToString(data.getBytes(),base64Mode);
	    SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm);
	    Cipher c = Cipher.getInstance(algorithm);
	    c.init(Cipher.ENCRYPT_MODE, secretKey);
	    byte[] encVal = c.doFinal(dataBytes.getBytes());
	    String encrypted=Base64.encodeToString(encVal, base64Mode);
	    return encrypted;

    }

    //Decryption

    public String decrypt(String key, String data) throws Exception
    {
	    Cipher c = Cipher.getInstance("AES");
	    SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm);
	    c.init(Cipher.DECRYPT_MODE, secretKey);
	    byte[] decordedValue = Base64.decode(data.getBytes(), base64Mode);
	    byte[] decValue = c.doFinal(decordedValue);
	    String decryptedValue = new String(decValue);
	    String decoded=new String(Base64.decode(decryptedValue, base64Mode));
	    return decoded;
    } 

    /*public String encrypt(String key, String data) {
        if (key == null || data == null)
            return null;
        try {
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(charsetName));
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
            SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);
            byte[] dataBytes = data.getBytes(charsetName);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.encodeToString(cipher.doFinal(dataBytes), base64Mode);
        } catch (Exception e) {
            return null;
        }
    }

    public String decrypt(String key, String data) {
        if (key == null || data == null)
            return null;
        try {
            byte[] dataBytes = Base64.decode(data, base64Mode);
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(charsetName));
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
            SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] dataBytesDecrypted = (cipher.doFinal(dataBytes));
            return new String(dataBytesDecrypted);
        } catch (Exception e) {
            return null;
        }
    }*/
}
