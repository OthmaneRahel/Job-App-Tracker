package jobtrackerapp.servicecandidatures.Repository;

import jobtrackerapp.servicecandidatures.Entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    Optional<IdempotencyKey> findByKeyAndScope(String key, String scope);
}