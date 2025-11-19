package cn.icheer.portal.utils;

import cn.icheer.portal.details.JWTUserDetails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author fangweihao
 * @since 2025/7/4 17:14
 */
@Slf4j
@Component
public class JWTUtils {

    private static RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplateInstance;

    // 关键：在实例初始化后，将实例的 redisTemplate 赋值给静态字段
    @PostConstruct
    public void init() {
        JWTUtils.redisTemplate = this.redisTemplateInstance;
    }

    /**
     * 密钥
     */
    private static final String SIGN = "icheer";
    private static final String REDIS_KEY_PREFIX = "token:";
    private static final long EXPIRATION_TIME = 30; // token过期时间（30天），单位：天

    /**
     * 生成token，并存储到Redis
     *
     * @param map Map<String, String>
     * @return String token
     */
    public static String getToken(Map<String, String> map) {
        Calendar instance = Calendar.getInstance();
        // 默认7天过期
        instance.add(Calendar.DATE, (int) EXPIRATION_TIME);
        // 创建JWT builder
        JWTCreator.Builder builder = JWT.create();
        // payload
        map.forEach(builder::withClaim);
        // signature
        String token = builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(SIGN));

        // 存储到Redis(key为 token:userId， value为token)
        String userId = map.get("id");
        redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + userId, token, EXPIRATION_TIME, TimeUnit.DAYS);
        return token;
    }

    /**
     * 校验token合法性
     *
     * @param token String
     * @return DecodedJWT
     */
    public static Boolean verifyToken(String token) {
        if (token == null) {
            return false;
        }
        try {
            // 验证JWT签名(允许有10秒钟误差，包含判断过期时间)
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SIGN)).acceptLeeway(10).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            // 从JWT中获取用户ID
            String userId = decodedJWT.getClaim("id").asString();
            // 从Redis中获取token
            String redisToken = (String) redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + userId);
            // 比较Redis中的token和传入的token是否一致
            return token.equals(redisToken);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("token校验失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 解析token
     *
     * @param token String
     * @return Map<String, String>
     */
    public static Map<String, String> parseToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        Map<String, String> map = new HashMap<>();
        decodedJWT.getClaims().forEach((key, value) -> {
            map.put(key, value.asString());
        });
        return map;
    }

    /**
     * 获取用户信息
     *
     * @param token String
     * @return String
     */
    public static JWTUserDetails getUserInfo(String token) {
        Map<String, String> map = parseToken(token);
        // 从token中获取用户信息并封装成JWTUserDetails对象
        JWTUserDetails jwtUserDetails = new JWTUserDetails();
        jwtUserDetails.setId(Long.parseLong(map.get("id")));
        jwtUserDetails.setPhoneNumber(map.get("phoneNumber"));
        jwtUserDetails.setUserName(map.get("userName"));
        jwtUserDetails.setCreateTime(LocalDateTime.parse(map.get("createTime")));
        return jwtUserDetails;
    }

    /**
     * 使token失效(用于登出)
     * @param token String
     */
    public static void invalidateToken(String token) {
        // 从token中获取用户ID
        Map<String, String> map = parseToken(token);
        String userId = map.get("id");
        redisTemplate.delete(REDIS_KEY_PREFIX + userId);
    }
}
