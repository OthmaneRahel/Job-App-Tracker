package jobtrackerapp.serviceutilisateurs.service;

import jobtrackerapp.servicecandidatures.Exception.ResourceNotFoundException;
import jobtrackerapp.serviceutilisateurs.dto.RegisterRequest;
import jobtrackerapp.serviceutilisateurs.dto.UserResponseDto;
import jobtrackerapp.serviceutilisateurs.entity.User;
import jobtrackerapp.serviceutilisateurs.exception.EmailAlreadyExistsException;
import jobtrackerapp.serviceutilisateurs.exception.UserNotFoundException;
import jobtrackerapp.serviceutilisateurs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

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
}