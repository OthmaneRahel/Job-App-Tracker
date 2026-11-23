package jobtrackerapp.servicecandidatures.FeignClient;

import jobtrackerapp.servicecandidatures.Dto.UserResponseDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RequestUser {

    private final UtilisateurClient utilisateurClient;

    public RequestUser(UtilisateurClient utilisateurClient) {
        this.utilisateurClient = utilisateurClient;
    }


    @Cacheable(value = "user-info", key = "#userId")
    public UserResponseDto getUserWithCache(Long userId, String authorization) {
        return utilisateurClient.getUserById(userId, authorization);
    }
}
