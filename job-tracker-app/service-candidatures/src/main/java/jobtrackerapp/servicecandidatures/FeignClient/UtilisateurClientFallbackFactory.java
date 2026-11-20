package jobtrackerapp.servicecandidatures.FeignClient;

import jobtrackerapp.servicecandidatures.Dto.UserResponseDto;
import jobtrackerapp.servicecandidatures.Exception.ResourceNotFoundException;
import jobtrackerapp.servicecandidatures.Exception.UtilisateurServiceUnavailableException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurClientFallbackFactory implements FallbackFactory<UtilisateurClient> {

    @Override
    public UtilisateurClient create(Throwable cause) {
        return (id, authorization) -> {
            if (cause instanceof ResourceNotFoundException rnf) throw rnf;
            throw new UtilisateurServiceUnavailableException(
                    "service-utilisateurs indisponible : " + cause.getMessage());
        };
    }
}
