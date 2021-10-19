package demo.test.security.jwt;

import demo.test.model.request.LoginBody;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {
    private static final String secret = "nguyenthanhdat19022001@gmail.com";
    private static final long EXPIRE_TIME = 60*60*1000;
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class.getName());

    // Generate token
    public String generateToken(LoginBody loginBody) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, loginBody.getUsername());
    }

    // Get exp date of token
    public Date getExpirationDateFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }
    // Get username login
    public String getUserNameFromJwtToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }
    // Bool Token expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    // Bool Validate token
    public boolean validateJwtToken(String authToken) {
        try {
            getAllClaimsFromToken(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature -> Message: {} ", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token -> Message: {}", e);
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token -> Message: {}", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token -> Message: {}", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty -> Message: {}", e);
        }
        return false;
    }
    // Helper function
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }
    private Key getSigningKey() {
        byte[] keyBytes = this.secret.getBytes(StandardCharsets. UTF_8 );
        return Keys.hmacShaKeyFor(keyBytes);
    }
}