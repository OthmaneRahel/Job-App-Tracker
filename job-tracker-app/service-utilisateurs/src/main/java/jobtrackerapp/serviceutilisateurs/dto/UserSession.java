package jobtrackerapp.serviceutilisateurs.dto;

public record UserSession(
        Long id,
        String keycloakId,
        String nom,
        String prenom,
        String email,
        String roles,       // ex: "USER,ADMIN"
        String loginAt) {}