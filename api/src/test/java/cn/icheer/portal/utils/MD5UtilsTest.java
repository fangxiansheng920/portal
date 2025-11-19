package cn.icheer.portal.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author fangweihao
 * @since 2025/7/3 16:54
 */
class MD5UtilsTest {
   @Test
   public void MD5Test() {
       System.out.println(MD5Utils.getPwd("123456789"));
   }
}