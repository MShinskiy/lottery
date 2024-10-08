package com.lavkatech.lottery.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lavkatech.lottery.entity.dto.DecodedUserQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CipherUtility {
    private final static Logger log = LogManager.getLogger();

    public static DecodedUserQuery encodedStringToPojo(String query, String initVector, String key)  {
        String queryJson = unwrapEncodedString(query, initVector, key);
        if(queryJson != null) {
            queryJson = queryJson.replaceAll("(?i)other", "OTHER");
            queryJson = queryJson.replaceAll("(?i)partner", "PARTNER");
            queryJson = queryJson.replaceAll("(?i)low", "LOW");
            queryJson = queryJson.replaceAll("(?i)high", "HIGH");
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(queryJson, DecodedUserQuery.class);
        } catch (JsonProcessingException e) {
            log.error("Invalid JSON for encoded query: {}", queryJson, e);
            return null;
        } catch (IllegalArgumentException e) {
            log.error("Received query: {}", query);
            log.error("Invalid JSON for encoded query: {}", queryJson, e);
            return null;
        }
    }

    // Развернуть зашифрованную строку в Json
    public static String unwrapEncodedString(String query, String initVector, String key) {
        if(query.isEmpty()) {
            log.error("Empty query");
            return null;
        }
        //Decipher query parameter
        if(query.startsWith("query="))
            query = query.replace("query=", "");
        if(query.contains(" "))
            query = query.replace(" ", "+");
        String res;
        String param = URLDecoder.decode(query.replace("query=", ""), StandardCharsets.UTF_8);
        res = decrypt(param, initVector, key);
        if(res == null)
            res = decrypt(query, initVector, key);
        if(res == null) {
            log.error("Could not decrypt query {} with vector {} and key {}", param, initVector, key);
            return null;
        }
        return res;
    }

    //AES256CBC Message decryption
    public static String decrypt(String encrypted, String initVector, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec sKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception e) {
            log.debug("Error decrypting query {} with vector {} and key {}", encrypted, initVector, key);
            log.debug("Error message: {}", e.getMessage());
            //Null on error
            return null;
        }
    }

    //AES256CBC Message encryption
    public static String encrypt(String input, String initVector,
                                 String key) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec sKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());

        return Base64.encodeBase64String(cipherText);
    }

}
