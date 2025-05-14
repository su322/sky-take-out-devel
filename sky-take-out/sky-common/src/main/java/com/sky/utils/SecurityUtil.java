package com.sky.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtil {

    /**
     * SHA256加密
     *
     * @param input 原始字符串
     * @return 加密后的字符串
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param hash 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 取身份证号后六位，目的是在Service层调用进行sha256加密
     *
     * @param idNumber 身份证号
     * @return 身份证号后六位
     */
    public static String getLastSix(String idNumber) {
        return idNumber.substring(idNumber.length() - 6);
    }

    /**
     * 身份证号校验
     *
     * @param idNumber 身份证号
     * @return 身份证号
     * TODO 后端在存入数据库之前校验最后一位大小写和身份证格式，虽然前端也应该确保是正确的，我就假设前端传过来的身份证号是正确的，不做正则表达式校验，先只做最后一位大小写的校验
     */
    public static String idNumberVerification(String idNumber) {
        char lastChar = idNumber.charAt(idNumber.length() - 1);
        if (lastChar == 'x') {
            return idNumber.substring(0, idNumber.length() - 1) + 'X';
        }
        return idNumber;
    }
}