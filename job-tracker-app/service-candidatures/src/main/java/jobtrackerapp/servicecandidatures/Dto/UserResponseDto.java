package jobtrackerapp.servicecandidatures.Dto;


import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String nom,
        String prenom,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
