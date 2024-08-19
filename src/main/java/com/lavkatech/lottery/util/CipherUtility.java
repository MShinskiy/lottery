package com.lavkatech.lottery.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lavkatech.lottery.entity.dto.DecodedUserQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class CipherUtility {
    private final static Logger log = LogManager.getLogger();

    public static DecodedUserQuery encodedStringToPojo(String query, String initVector, String key)  {
        String queryJson = unwrapEncodedString(query, initVector, key);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(queryJson, DecodedUserQuery.class);
        } catch (JsonProcessingException e) {
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
}
