package jobtrackerapp.serviceutilisateurs.controller;

import jakarta.validation.Valid;
import jobtrackerapp.serviceutilisateurs.dto.LoginRequest;
import jobtrackerapp.serviceutilisateurs.dto.RegisterRequest;
import jobtrackerapp.serviceutilisateurs.dto.UserResponseDto;
import jobtrackerapp.serviceutilisateurs.service.KeycloakService;
import jobtrackerapp.serviceutilisateurs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final KeycloakService keycloakService;

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/auth/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request) {
        return keycloakService.login(request);
    }

    // Route protégée : nécessite un JWT valide
    @GetMapping("/users/me")
    public UserResponseDto me(@AuthenticationPrincipal Jwt jwt) {
        return userService.findByKeycloakId(jwt.getSubject());
    }





}