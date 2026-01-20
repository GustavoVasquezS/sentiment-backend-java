package com.project.sentimentapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // Clave secreta (en producción debe estar en application.properties)
    private static final String SECRET_KEY = "mySecretKeyForJWTTokenGenerationAndValidation2024";
    private static final long EXPIRATION_TIME = 86400000; // 24 horas en milisegundos

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Generar token JWT
    public String generateToken(String correo, Integer usuarioId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("usuarioId", usuarioId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(correo)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extraer correo del token
    public String extractCorreo(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extraer usuarioId del token
    public Integer extractUsuarioId(String token) {
        return extractAllClaims(token).get("usuarioId", Integer.class);
    }

    // Extraer todos los claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Validar token
    public Boolean validateToken(String token, String correo) {
        final String extractedCorreo = extractCorreo(token);
        return (extractedCorreo.equals(correo) && !isTokenExpired(token));
    }

    // Verificar si el token expiró
    private Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}