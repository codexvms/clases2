package cl.gob.binexus.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final String HMAC_ALGO = "HmacSHA256";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jwt.secret:}")
    private String secret;

    public String generateToken(String subject, Map<String, Object> claims, long expirationMinutes) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret no configurado");
        }
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new HashMap<>(claims);
        payload.put("sub", subject);
        payload.put("iat", Instant.now().getEpochSecond());
        payload.put("exp", Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES).getEpochSecond());

        String encodedHeader = base64UrlEncode(writeJson(header));
        String encodedPayload = base64UrlEncode(writeJson(payload));
        String signature = sign(encodedHeader + "." + encodedPayload);
        return encodedHeader + "." + encodedPayload + "." + signature;
    }

    public boolean validateToken(String token) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret no configurado");
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return false;
        }
        String headerPayload = parts[0] + "." + parts[1];
        String expectedSignature = sign(headerPayload);
        if (!expectedSignature.equals(parts[2])) {
            return false;
        }
        try {
            Map<?, ?> payload = objectMapper.readValue(base64UrlDecode(parts[1]), Map.class);
            Number exp = (Number) payload.get("exp");
            return exp != null && Instant.now().getEpochSecond() < exp.longValue();
        } catch (Exception e) {
            return false;
        }
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGO));
            byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(signature);
        } catch (Exception e) {
            throw new IllegalStateException("Error al firmar token", e);
        }
    }

    private byte[] writeJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsBytes(map);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No se pudo serializar el token", e);
        }
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private byte[] base64UrlDecode(String value) {
        return Base64.getUrlDecoder().decode(value);
    }
}
