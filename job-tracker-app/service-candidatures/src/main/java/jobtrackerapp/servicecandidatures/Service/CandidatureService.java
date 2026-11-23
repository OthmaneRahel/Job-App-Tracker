package jobtrackerapp.servicecandidatures.Service;

import jobtrackerapp.servicecandidatures.Dto.CandidatureRequest;
import jobtrackerapp.servicecandidatures.Dto.CandidatureResponse;
import jobtrackerapp.servicecandidatures.Dto.NotificationPayload;
import jobtrackerapp.servicecandidatures.Dto.UserResponseDto;
import jobtrackerapp.servicecandidatures.FeignClient.RequestUser;
import jobtrackerapp.servicecandidatures.enums.ApplicationStatus;
import jobtrackerapp.servicecandidatures.Entity.Candidature;
import jobtrackerapp.servicecandidatures.Exception.ResourceNotFoundException;
import jobtrackerapp.servicecandidatures.FeignClient.UtilisateurClient;
import jobtrackerapp.servicecandidatures.Repository.CandidatureRepository;
import jobtrackerapp.servicecandidatures.enums.NotificationTypes;
import jobtrackerapp.servicecandidatures.messaging.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class  CandidatureService {

    private final CandidatureRepository repository;
    private final NotificationPublisher notificationPublisher;
    private final RequestUser requestUser;



//    @Transactional
//    public CandidatureResponse create(CandidatureRequest request) {
//        Candidature c = new Candidature();
//        applyRequest(c, request);
//        Candidature saved = repository.save(c);
//        NotificationPayload notificationPayload = new NotificationPayload(
//                "rahelothmane@gmail.com",
//                c.getId(),
//                NotificationTypes.FOLLOW_UP,
//                "Candidature crée",
//                "Bonjour,votre candidature a été bien crée",
//                false,
//                Timestamp.from(Instant.now())
//        );
//
//        System.out.println(notificationPayload.applicationId());
//        afterCommit(()-> notificationPublisher.envoyerNotification_follow_up(notificationPayload));
//        return toResponse(saved);
//
//    }


//    @Transactional
//    public CandidatureResponse create(CandidatureRequest request) {
//        Candidature c = new Candidature();
//        applyRequest(c, request);
//        Candidature saved = repository.save(c);
//        afterCommit(() -> {
//            NotificationPayload notificationPayload = new NotificationPayload(
//                "rahelothmane@gmail.com",
//                c.getId(),
//                NotificationTypes.FOLLOW_UP,
//                "Candidature crée",
//                "Bonjour,votre candidature a été bien crée",
//                false,
//                Timestamp.from(Instant.now())
//        );
//            notificationPublisher.envoyerNotification_follow_up(
//                   notificationPayload
//            );
//        });
//
//        return toResponse(saved);
//    }


    @Transactional
    public CandidatureResponse create(CandidatureRequest request,  String authorization) {

        UserResponseDto user = requestUser.getUserWithCache(request.userId(), authorization);

        Candidature c = new Candidature();
        applyRequest(c, request);
        Candidature saved = repository.save(c);

        afterCommit(() -> {
            NotificationPayload notificationPayload = new NotificationPayload(
                    user.email(),
                    //"rahelothmane@gmail.com",
                    c.getId(),
                    NotificationTypes.FOLLOW_UP,
                    "Candidature crée",
                    "Bonjour " + user.prenom() + ", votre candidature dans le post " + c.getJobTitle() + " dans l\' entreprise " + c.getCompanyName() + " a été bien crée",
                    false,
                    Timestamp.from(Instant.now())
            );
            notificationPublisher.envoyerNotification_follow_up(notificationPayload);
        });

        return toResponse(saved);
    }


    private void afterCommit(Runnable action) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            action.run();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "candidature",key = "#id")
    public CandidatureResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }


    @Transactional(readOnly = true)
    public List<CandidatureResponse> getAll(Long userId, ApplicationStatus status) {
        List<Candidature> list;
        if (userId != null && status != null) {
            list = repository.findByUserIdAndStatus(userId, status);
        } else if (userId != null) {
            list = repository.findByUserId(userId);
        } else {
            list = repository.findAll();
        }
        return list.stream().map(this::toResponse).toList();
    }


    public CandidatureResponse update(Long id, CandidatureRequest request) {
        Candidature c = findOrThrow(id);
        applyRequest(c, request);
        return toResponse(repository.save(c));
    }


    public void delete(Long id) {
        repository.delete(findOrThrow(id));
    }

    private Candidature findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable avec l'id : " + id));
    }

    private void applyRequest(Candidature c, CandidatureRequest r) {
        c.setUserId(r.userId());
        c.setCompanyName(r.companyName());
        c.setJobTitle(r.jobTitle());
        c.setJobUrl(r.jobUrl());
        c.setLocation(r.location());
        c.setEmploymentType(r.employmentType());
        if (r.status() != null) c.setStatus(r.status());
        if (r.appliedAt() != null) c.setAppliedAt(r.appliedAt());
        c.setNextFollowUpDate(r.nextFollowUpDate());
    }

    private CandidatureResponse toResponse(Candidature c) {
        return new CandidatureResponse(
                c.getId(), c.getUserId(), c.getCompanyName(), c.getJobTitle(), c.getJobUrl(),
                c.getLocation(), c.getEmploymentType(), c.getStatus(), c.getAppliedAt(),
                c.getNextFollowUpDate(), c.getCreatedAt(), c.getUpdatedAt());
    }
}