package com.mryan_cachedome.test.utils;

import java.security.MessageDigest;

/**
 * name:Mr.Yan or Mr.TianChen
 * Data: 2017/4/14
 * 备注
 */

public class MD5Utils {
    /**
     * MD5加密
     * @param str
     * @param isUp
     * true是否大写
     * @return
     */
    public static String MD5(String str, boolean isUp) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = (md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        if (isUp) {
            return (hexValue.toString()).toUpperCase();
        } else {
            return hexValue.toString();
        }
    }
}
