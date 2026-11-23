package jobtrackerapp.serviceutilisateurs.service;

import jobtrackerapp.servicecandidatures.Exception.ResourceNotFoundException;
import jobtrackerapp.serviceutilisateurs.dto.LoginRequest;
import jobtrackerapp.serviceutilisateurs.dto.RegisterRequest;
import jobtrackerapp.serviceutilisateurs.dto.UserResponseDto;
import jobtrackerapp.serviceutilisateurs.dto.UserSession;
import jobtrackerapp.serviceutilisateurs.entity.User;
import jobtrackerapp.serviceutilisateurs.exception.EmailAlreadyExistsException;
import jobtrackerapp.serviceutilisateurs.exception.UserNotFoundException;
import jobtrackerapp.serviceutilisateurs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final SessionService sessionService;
    private final JwtDecoder jwtDecoder;

    public UserResponseDto register(RegisterRequest dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        // 1. Création du compte dans Keycloak
        String keycloakId = keycloakService.createUser(dto);

        // 2. Sauvegarde du profil en base (sans mot de passe)
        try {
            User saved = userRepository.save(User.builder()
                    .keycloakId(keycloakId)
                    .nom(dto.nom())
                    .prenom(dto.prenom())
                    .email(dto.email())
                    .build());
            return toDto(saved);
        } catch (RuntimeException e) {
            // Si la base échoue, on supprime le compte Keycloak pour rester cohérent
            keycloakService.deleteUser(keycloakId);
            throw e;
        }
    }

    public UserResponseDto findByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .map(this::toDto)
                .orElseThrow(() -> new UserNotFoundException("Profil introuvable"));
    }

    // UserService
    public UserResponseDto findById(Long id) {
        return userRepository.findById(id)
                .map(this::toDto) // ton mapper existant
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + id));
    }

    private UserResponseDto toDto(User u) {
        return new UserResponseDto(u.getId(), u.getNom(), u.getPrenom(),
                u.getEmail(), u.getCreatedAt(), u.getUpdatedAt());
    }

    public Map<String, Object> login(LoginRequest req) {
        // 1. Keycloak vérifie email + mot de passe et renvoie les tokens
        Map<String, Object> tokens = keycloakService.login(req);

        // 2. On lit le JWT pour récupérer le sub et les rôles
        Jwt jwt = jwtDecoder.decode((String) tokens.get("access_token"));
        String keycloakId = jwt.getSubject();

        // 3. Profil depuis la base
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException("Profil introuvable"));

        // 4. Rôles Keycloak
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        String roles = realmAccess == null ? "" :
                String.join(",", (Collection<String>) realmAccess.get("roles"));

        // 5. TTL = durée de la session Keycloak (refresh token)
        long seconds = ((Number) tokens.get("refresh_expires_in")).longValue();

        sessionService.save(new UserSession(
                        user.getId(), keycloakId, user.getNom(), user.getPrenom(),
                        user.getEmail(), roles, Instant.now().toString()),
                Duration.ofSeconds(seconds));

        return tokens;
    }

    public UserSession getSession(String keycloakId) {
        return sessionService.find(keycloakId).orElseGet(() -> {
            // Pas dans Redis (expiré, ou Redis vidé) : on recharge depuis la base
            User u = userRepository.findByKeycloakId(keycloakId)
                    .orElseThrow(() -> new UserNotFoundException("Profil introuvable"));
            UserSession s = new UserSession(u.getId(), keycloakId, u.getNom(),
                    u.getPrenom(), u.getEmail(), "", Instant.now().toString());
            sessionService.save(s, Duration.ofMinutes(30));
            return s;
        });
    }
}