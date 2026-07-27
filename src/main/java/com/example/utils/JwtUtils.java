package com.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtils {

    // 签名密钥（这个字符串可以随便写，但要足够复杂，生产环境会配置在配置文件中）
    private static final String SIGN_KEY = "example2026SecretKey@123!";
    // 令牌有效期（毫秒），这里设置为 12 小时
    private static final Long EXPIRE = 12 * 60 * 60 * 1000L;

    /**
     * 生成 JWT 令牌
     * @param claims 要存储的数据（比如用户id，用户名）
     * @return JWT 字符串
     */
    public static String generateJwt(Map<String, Object> claims) {
        String jwt = Jwts.builder()
                .addClaims(claims)                          // 设置自定义数据
                .signWith(SignatureAlgorithm.HS256, SIGN_KEY) // 设置签名算法和密钥
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE)) // 设置过期时间
                .compact();                                 // 生成字符串
        return jwt;
    }

    /**
     * 解析 JWT 令牌
     * @param jwt 令牌字符串
     * @return 存储的数据（Claims）
     * @throws Exception 如果令牌过期或被篡改，会抛出异常
     */
    public static Claims parseJwt(String jwt) throws Exception {
        Claims claims = Jwts.parser()
                .setSigningKey(SIGN_KEY)          // 设置密钥
                .parseClaimsJws(jwt)              // 解析
                .getBody();                       // 获取数据
        return claims;
    }
}