package jobtrackerapp.servicecandidatures.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import jobtrackerapp.servicecandidatures.Repository.IdempotencyKeyRepository;
import jobtrackerapp.servicecandidatures.Entity.IdempotencyKey;


import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class IdempotencyService {

    private final IdempotencyKeyRepository repository;
    private final ObjectMapper objectMapper;

    public IdempotencyService(IdempotencyKeyRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * Exécute l'action une seule fois par (clé, scope).
     * Si la clé existe déjà : retourne la réponse enregistrée.
     * Tout se fait dans une seule transaction : si l'action échoue, la clé est annulée.
     */
    @Transactional
    public <T> T execute(String key, String scope, Object request,
                         Supplier<T> action, Class<T> responseType) {

        String hash = hash(request);

        Optional<IdempotencyKey> existing = repository.findByKeyAndScope(key, scope);
        if (existing.isPresent()) {
            return replay(existing.get(), hash, responseType);
        }

        IdempotencyKey record = new IdempotencyKey();
        record.setKey(key);
        record.setScope(scope);
        record.setRequestHash(hash);

        try {
            repository.saveAndFlush(record); // réserve la clé (contrainte unique)
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Une requête avec cette clé d'idempotence est déjà en cours");
        }

        T result = action.get();

        record.setResponseStatus(HttpStatus.CREATED.value());
        record.setResponseBody(toJson(result));
        repository.save(record);

        return result;
    }

    private <T> T replay(IdempotencyKey record, String hash, Class<T> responseType) {
        if (!record.getRequestHash().equals(hash)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Cette clé d'idempotence a déjà été utilisée avec un contenu différent");
        }
        if (record.getResponseBody() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Une requête avec cette clé d'idempotence est déjà en cours");
        }
        try {
            return objectMapper.readValue(record.getResponseBody(), responseType);
        } catch (Exception e) {
            throw new IllegalStateException("Réponse enregistrée illisible", e);
        }
    }

    private String hash(Object request) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(request);
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(json));
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de calculer le hash de la requête", e);
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de sérialiser la réponse", e);
        }
    }
}