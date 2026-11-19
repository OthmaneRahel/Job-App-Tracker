package jobtrackerapp.serviceutilisateurs.service;

import jobtrackerapp.serviceutilisateurs.dto.LoginRequest;
import jobtrackerapp.serviceutilisateurs.dto.RegisterRequest;
import jobtrackerapp.serviceutilisateurs.exception.EmailAlreadyExistsException;
import jobtrackerapp.serviceutilisateurs.exception.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class KeycloakService {


    //RestClient permet à Spring Boot d'envoyer des requêtes HTTP à Keycloak.
    private final RestClient rest = RestClient.create();

    @Value("${keycloak.server-url}") private String serverUrl;
    @Value("${keycloak.realm}") private String realm;
    @Value("${keycloak.login-client-id}") private String loginClientId;
    @Value("${keycloak.admin-client-id}") private String adminClientId;
    @Value("${keycloak.admin-client-secret}") private String adminClientSecret;

    private String tokenUrl() {
        return serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    private Map<String, Object> requestToken(MultiValueMap<String, String> form) {
        return rest.post()
                .uri(tokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED) // format: application/x-www-form-urlencoded
                .body(form)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    /** Token d'administration : permet à NOTRE service de créer des comptes. */
    private String getAdminToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", adminClientId);
        form.add("client_secret", adminClientSecret);
        return (String) requestToken(form).get("access_token");
    }

    /** INSCRIPTION : crée le compte dans Keycloak et renvoie son identifiant. */
    public String createUser(RegisterRequest dto) {
        Map<String, Object> body = Map.of(
                "username", dto.email(),
                "email", dto.email(),
                "firstName", dto.prenom(),
                "lastName", dto.nom(),
                "enabled", true,
                "emailVerified", true,
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", dto.password(),
                        "temporary", false)));
        try {
            ResponseEntity<Void> response = rest.post()
                    .uri(serverUrl + "/admin/realms/" + realm + "/users")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAdminToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            // Keycloak renvoie l'URL du nouvel utilisateur : .../users/<id>
            String location = response.getHeaders().getLocation().toString();
            return location.substring(location.lastIndexOf('/') + 1);

        } catch (HttpClientErrorException.Conflict e) {
            throw new EmailAlreadyExistsException(dto.email());
        }
    }

    /** Sert à annuler l'inscription si la sauvegarde en base échoue. */
    public void deleteUser(String keycloakId) {
        rest.delete()
                .uri(serverUrl + "/admin/realms/" + realm + "/users/" + keycloakId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAdminToken())
                .retrieve()
                .toBodilessEntity();
    }

    /** CONNEXION : envoie email + mot de passe, reçoit le JWT. */
    public Map<String, Object> login(LoginRequest req) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", loginClientId);
        form.add("username", req.email());
        form.add("password", req.password());
        try {
            return requestToken(form);
        } catch (HttpClientErrorException e) {
            System.out.println("Erreur Keycloak login : " + e.getStatusCode()
                    + " -> " + e.getResponseBodyAsString());
            throw new InvalidCredentialsException();
        }
    }
}