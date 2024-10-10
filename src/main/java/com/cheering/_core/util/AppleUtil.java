package com.cheering._core.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


@Component
public class AppleUtil {
    private static final String APPLE_PUBLIC_KEY_URL = "https://appleid.apple.com/auth/keys";

    public PublicKey getPublicKey(String kid, String alg) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> httpEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<String> res = restTemplate.exchange(APPLE_PUBLIC_KEY_URL, HttpMethod.GET, httpEntity, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jwkNode = null;
        try {
            jwkNode = objectMapper.readTree(res.getBody()).get("keys");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        for (JsonNode key : jwkNode) {
            if(key.get("kid").asText().equals(kid) && key.get("alg").asText().equals(alg)) {
                String kty = key.get("kty").asText();
                String n = key.get("n").asText();
                String e = key.get("e").asText();

                byte[] decodedN = Base64.getUrlDecoder().decode(n);
                byte[] decodedE = Base64.getUrlDecoder().decode(e);

                RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(1, decodedN), new BigInteger(1, decodedE));

                try {
                    KeyFactory keyFactory = KeyFactory.getInstance(kty);
                    return keyFactory.generatePublic(publicKeySpec);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return null;
    }

    public DecodedJWT validateIdentityToken(String identityToken) {
        DecodedJWT decodedJWT = JWT.decode(identityToken);
        String kid = decodedJWT.getKeyId();
        String alg = decodedJWT.getAlgorithm();

        PublicKey publicKey = getPublicKey(kid, alg);

        DecodedJWT verifiedJWT = JWT.require(Algorithm.RSA256((RSAPublicKey) publicKey, null)).build().verify(identityToken);

        return verifiedJWT;
    }

}
