package jobtrackerapp.serviceutilisateurs.service;

import jobtrackerapp.serviceutilisateurs.dto.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionService {

    private static final String PREFIX = "session:";
    private final StringRedisTemplate redis;

    public void save(UserSession s, Duration ttl) {
        String key = PREFIX + s.keycloakId();
        Map<String, String> data = new HashMap<>();
        data.put("id", String.valueOf(s.id()));
        data.put("keycloakId", s.keycloakId());
        data.put("nom", s.nom());
        data.put("prenom", s.prenom());
        data.put("email", s.email());
        data.put("roles", s.roles());
        data.put("loginAt", s.loginAt());
        redis.opsForHash().putAll(key, data);
        redis.expire(key, ttl);          // la session disparaît seule
    }

    public Optional<UserSession> find(String keycloakId) {
        Map<Object, Object> d = redis.opsForHash().entries(PREFIX + keycloakId);
        if (d.isEmpty()) return Optional.empty();
        return Optional.of(new UserSession(
                Long.valueOf((String) d.get("id")),
                (String) d.get("keycloakId"),
                (String) d.get("nom"),
                (String) d.get("prenom"),
                (String) d.get("email"),
                (String) d.get("roles"),
                (String) d.get("loginAt")));
    }

    public void delete(String keycloakId) {
        redis.delete(PREFIX + keycloakId);
    }
}