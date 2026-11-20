package jobtrackerapp.servicecandidatures.FeignClient;

import feign.Response;
import feign.codec.ErrorDecoder;
import jobtrackerapp.servicecandidatures.Exception.ResourceNotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 404) {
                return new ResourceNotFoundException("Utilisateur introuvable");
            }
            if (response.status() == 401 || response.status() == 403) {
                return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalide ou absent");
            }
            return new ErrorDecoder.Default().decode(methodKey, response);
        };
    }
}