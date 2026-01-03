package com.juangomez.userservice.service.impl;

import com.juangomez.userservice.model.enums.UserAccountStatus;
import com.juangomez.userservice.service.contract.JwtService;
import com.juangomez.userservice.util.TokenPayload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("classpath:certs/private.pem")
    private Resource privateKeyResource;

    @Value("classpath:certs/public.pem")
    private Resource publicKeyResource;

    // Hold the actual keys
    private PrivateKey privateKey;
    private PublicKey publicKey;

    // Convert Resource -> Key on startup
    @PostConstruct
    public void initKeys() throws Exception {
        this.privateKey = readPrivateKey(privateKeyResource);
        this.publicKey = readPublicKey(publicKeyResource);
    }

    private PrivateKey readPrivateKey(Resource resource) throws Exception {
        String key = new String(resource.getInputStream().readAllBytes())
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", ""); // Remove newlines/spaces

        byte[] encoded = Base64.getDecoder().decode(key);
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encoded));
    }

    private PublicKey readPublicKey(Resource resource) throws Exception {
        String key = new String(resource.getInputStream().readAllBytes())
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(key);
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encoded));
    }

    @Override
    public String generateToken(TokenPayload payload) {
        Map<String, Object> claims = new HashMap<>();

        // Add extra claims
        claims.put("userId", payload.getUserId());
        claims.put("status", payload.getStatus());

        long JWT_EXPIRATION = 1000 * 60 * 30;
        return Jwts.builder()
                .header().keyId("user-service-key-id").and()
                .claims(claims)
                .subject(payload.getUsername())
                .signWith(privateKey, Jwts.SIG.RS256)
                .issuedAt(new Date(System.currentTimeMillis()))
                // 30 minutes for token expiration
                .expiration( new Date(System.currentTimeMillis() + JWT_EXPIRATION) )
                .compact();
    }

    @Override
    public TokenPayload extractPayload(String token) {

        String username = extractClaim(token, Claims::getSubject);

        String userIdString = extractClaim(token,
                claims -> claims.get("userId", String.class)
        );

        String accountStatus = extractClaim(token,
                claims -> claims.get("status", String.class)
        );

        return new TokenPayload(
                UUID.fromString(userIdString),
                username,
                // Delete string ROLE_ from the claim
                UserAccountStatus.valueOf(accountStatus)
        );
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        boolean validUsername = extractPayload(token).getUsername()
                .equals(userDetails.getUsername());

        // If the token has expired, or it is in the blacklist
        boolean tokenExpired = isTokenExpired(token);

        return validUsername && !tokenExpired;
    }

    @Override
    public String extractAuthToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Override
    public Duration tokenTtl(String token) {
        return Duration.between(
                new Date(System.currentTimeMillis()).toInstant(),
                extractExpiration(token).toInstant()
        );
    }

    @Override
    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    /**
     * Extracts from the token the claim indicated in claimResolver
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }


    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey) //
                .build()
                .parseSignedClaims(token) // If signature is fake, this throws an Exception
                .getPayload();
    }

    // Verifies token expiration
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}