package com.it.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component

public class JwtUtil {
    @Value("${shiro.jwt.secret}")
    private String secret;
    @Value("${shiro.jwt.expire}")
    private Long expire;
    @Value("${shiro.jwt.header.alg}")
    private String headerAlg;
    @Value("${shiro.jwt.header.typ}")
    private String headerTyp;

    /**
     * 生成token
     */
    public String getToken(String account, long currentTimeMillis) {
        // 设置秘钥
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(account).append(secret);

        // 设置jwt头header
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("alg", headerAlg); // 签名算法
        headerClaims.put("typ", headerTyp); // token 类型
        // 设置jwt的header，负载paload以及加密算法
        String token = JWT
                .create()
                .withHeader(headerClaims)
                .withClaim("account", account)
                .withClaim("expire", currentTimeMillis + expire)
                .sign(Algorithm.HMAC256(stringBuilder.toString()));
        return token;
    }

    /**
     * 无需秘钥就能获取其中的信息
     * 解析token.
     * {
     * "account": "account",
     * "timeStamp": "134143214"
     * }
     */
    public Map<String, String> parseToken(String token) {
        HashMap<String, String> map = new HashMap<String, String>();
        // 解码 JWT
        DecodedJWT decodedJwt = JWT.decode(token);
        Claim account = decodedJwt.getClaim("account");
        Claim expire = decodedJwt.getClaim("expire");
        map.put("account", account.asString());
        map.put("expire", expire.asLong().toString());
        return map;
    }

    /**
     * 解析token获取账号.
     */
    public String getAccount(String token) {
        HashMap<String, String> map = new HashMap<String, String>();
        DecodedJWT decodedJwt = JWT.decode(token);
        Claim account = decodedJwt.getClaim("account");
        return account.asString();
    }

    /**
     * 校验token是否正确
     *
     * @param token Token
     * @return boolean 是否正确
     */
    public boolean verify(String token) {
        DecodedJWT decodedJwt = JWT.decode(token);
        Claim account = decodedJwt.getClaim("account");
        Claim expire = decodedJwt.getClaim("expire");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(account.asString()).append(secret);
        // 验证JWT的签名和有效性
        Algorithm algorithm = Algorithm.HMAC256(stringBuilder.toString());
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            verifier.verify(token);
            return true; // 验证通过
        } catch (JWTVerificationException e) {
            return false; // 验证失败
        }
    }

    /**
     * 校验token是否过期
     *
     * @param token Token
     * @return boolean 是否正确
     */
    public boolean isExpired(String token) {
        DecodedJWT decodedJwt = JWT.decode(token);
        Claim expire = decodedJwt.getClaim("expire");
        // 验证过期时间
        Long expireTime = expire.asLong();
        if (System.currentTimeMillis() > expireTime) {
            return true;
        }
        return false;
    }


    /**
     * 获取token过期时间
     *
     * @param token Token
     * @return boolean 是否正确
     */
    public long getExpiredTime(String token) {
        DecodedJWT decodedJwt = JWT.decode(token);
        Claim expire = decodedJwt.getClaim("expire");
        return expire.asLong();
    }
}