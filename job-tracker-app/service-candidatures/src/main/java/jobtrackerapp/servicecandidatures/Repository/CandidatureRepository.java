package jobtrackerapp.servicecandidatures.Repository;

import jobtrackerapp.servicecandidatures.enums.ApplicationStatus;
import jobtrackerapp.servicecandidatures.Entity.Candidature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Long> {

    List<Candidature> findByUserId(Long userId);

    List<Candidature> findByUserIdAndStatus(Long userId, ApplicationStatus status);
}
