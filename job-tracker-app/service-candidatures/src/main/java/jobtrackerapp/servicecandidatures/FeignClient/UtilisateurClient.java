package jobtrackerapp.servicecandidatures.FeignClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jobtrackerapp.servicecandidatures.Dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "service-utilisateurs",
        fallbackFactory = UtilisateurClientFallbackFactory.class
)
public interface UtilisateurClient {

    @CircuitBreaker(name = "utilisateur-circuit")
    @GetMapping("/api/users/{id}")
    UserResponseDto getUserById(@PathVariable("id") Long id,
                                @RequestHeader(value = "Authorization", required = false) String authorization);
}