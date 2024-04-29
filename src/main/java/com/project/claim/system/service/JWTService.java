package com.project.claim.system.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.modelmapper.internal.bytebuddy.asm.Advice;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    private static final String SECRET_KEY = "58fU3EMYMCAMQZJNF8bDSpnnspQgLwpPrRSPhJmxxMMfbZSqxG5HDLRGZn7aEYQS";


    //Fetch one Claim
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims  = extractAllClaims(token); //extract all claims
        return   claimResolver.apply(claims);
    }

    //All Claims
    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey()) // need to decode during validity
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //Fetch One Username's claim
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // email or username of a claim
    }

    public String generateToken(Map<String, Object> extractClaims, UserDetails userDetails) {
        return Jwts
                .builder() // return builder object to construct jwt
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername()) // should be the username/email so that we use the UserDetails to get the email and username
                .setIssuedAt(new Date (System.currentTimeMillis())) // in date format that checks if the token is valid or not
                .setExpiration(new Date(System.currentTimeMillis() + 600000) ) // you can set how long the token should be valid (10min)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) //decide which key we want to sign the token, we already created the getSignInKey()
                .compact(); // this will generate & return the token
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        //return username of the valid userDetails and non expired token
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}