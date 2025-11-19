package cn.icheer.portal.utils;

/**
 * @author fangweihao
 * @since 2025/7/3 13:15
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Utils {
    /**
     * MD5加密
     * @param pwd String
     * @return String
     */
    public static String getPwd(String pwd) {
        if (pwd == null) {
            return "";
        }
        try {
            // 创建加密对象
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] bs = digest.digest(pwd.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : bs) {
                int temp = b & 255;
                if (temp < 16 && temp >= 0) {
                    hexString.append("0").append(Integer.toHexString(temp));
                } else {
                    hexString.append(Integer.toHexString(temp));
                }
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
