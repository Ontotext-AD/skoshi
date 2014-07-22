package com.ontotext.tools.skoseditor.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class IdEncodingUtil {

    private static Logger log = LoggerFactory.getLogger(IdEncodingUtil.class);

    private IdEncodingUtil() {}

    public static String encode(String value) {
        byte[] encodedBytes = Base64.encodeBase64(value.getBytes());
        return new String(encodedBytes);
    }

    public static String decode(String value) {
        byte[] encodedBytes = value.getBytes();
        byte[] decodedBytes = Base64.decodeBase64(encodedBytes);
        return new String(decodedBytes);
    }

//    public static String encode(String value) {
//        try {
//            value = URLEncoder.encode(value, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            log.error("Failed to encode value.", e);
//        }
//        value = value.replace('.', '_');
//        return value;
//    }
//
//    public static String decode(String value) {
//        value = value.replace('_', '.');
//        try {
//            value = URLDecoder.decode(value, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            log.error("Failed to decode value.", e);
//        }
//        return value;
//    }

}
