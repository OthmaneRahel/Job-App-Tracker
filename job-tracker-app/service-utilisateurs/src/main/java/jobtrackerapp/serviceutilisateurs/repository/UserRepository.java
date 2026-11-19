package jobtrackerapp.serviceutilisateurs.repository;

import jobtrackerapp.serviceutilisateurs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakId(String keycloakId);
    boolean existsByEmail(String email);
}