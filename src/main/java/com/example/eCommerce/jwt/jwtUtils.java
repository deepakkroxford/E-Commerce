package com.example.eCommerce.jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;
import com.example.eCommerce.jwt.service.UserDetailsImpl;
import jakarta.servlet.http.Cookie;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;



@Component
public class jwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(jwtUtils.class);

    @Value("${spring.app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    @Value("${spring.app.jwtSecret}")
    private  String jwtSecrete;

    @Value("${spring.app.jwtCookieName}")
    private String jwtCookieName;

    //Getting jwt from header
    // public String getJwtHeader(HttpServletRequest request) {
    //     String bearerToken = request.getHeader("Authorization");
    //     logger.debug("Bearer Token: {}", bearerToken);
    //     if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
    //         return bearerToken.substring(7);
    //     }
    //     return null;
    // }

    // Getting Jwt from the cookies
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "springBootEcommerce");
        if(cookie!= null) {
            return cookie.getValue();
        }else {
            return null;
        }
    }

    public String getJwtFromHeader(HttpServletRequest request) {
       String bearerToken = request.getHeader("Authorization");
        logger.debug("Bearer Token: {}", bearerToken);
             if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                 return bearerToken.substring(7);
             }
             return null;
    }


    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrinciple) {
        String jwt = generateTokenFromUsername(userPrinciple.getUsername());
        ResponseCookie cookie = ResponseCookie.from("springBootEcommerce", jwt).
        path("/api").
        maxAge(24*60*60)
        .httpOnly(false).
        build();
        return cookie;
    }

    public ResponseCookie getCleanJwtCookies() {
        ResponseCookie cookie = ResponseCookie.from("springBootEcommerce", null).
                path("/api").
                maxAge(24*60*60)
                .httpOnly(false).
                build();
        return cookie;
    }

    //Generating token from the username
    public String generateTokenFromUsername(String username) {
    
        return Jwts.builder().
                subject(username).
                issuedAt(new Date()).
                expiration(new Date(new Date().getTime() + jwtExpirationInMs)).
                signWith(key()).
                compact();
    }
    //Getting username from jwt token
    public String getUsernameFromToken(String token) {
        return Jwts.parser().
                verifyWith((SecretKey) key()).build().
                parseSignedClaims(token).
                getPayload().
                getSubject();
    }
    //Generate signing key
    public Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecrete)
        );
    }
    //Validate JWT token
    public boolean validateToken(String token) {
        try {
            System.out.println("Validating Token");
            Jwts.parser().
                    verifyWith((SecretKey) key()).
                    build().
                    parseClaimsJws(token);
            return true;
        }catch (MalformedJwtException e){
            return false;
        }
    }


}
