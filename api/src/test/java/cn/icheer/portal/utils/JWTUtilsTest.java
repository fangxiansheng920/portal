package cn.icheer.portal.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author fangweihao
 * @since 2025/7/7 8:37
 */
class JWTUtilsTest {

    @Test
    void getToken() {
        Map<String, String> map = new HashMap<>();
        map.put("id", "1");
        map.put("creatTime", "2025-07-04 11:22:45");
        System.out.println(JWTUtils.getToken(map));
    }

    @Test
    void verifyToken() {
        String token = "eyJ0eXaiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjcmVhdFRpbWUiOiIyMDI1LTA3LTA0IDExOjIyOjQ1IiwiaWQiOiIxIn0.E_dXNKnDJVYiDSY0mJ3CVYfxcF_ifPjMUH49fxFA8c0";
        try {
            System.out.println(JWTUtils.verifyToken(token));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    void insertTokenInRedis() {

    }
}