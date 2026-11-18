package jobtrackerapp.serviceutilisateurs.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequestDto {
    @NotBlank private String nom;
    @NotBlank private String prenom;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 8) private String password;
}