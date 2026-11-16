package jobtrackerapp.servicecandidatures.Service;

import jobtrackerapp.servicecandidatures.Dto.CandidatureRequest;
import jobtrackerapp.servicecandidatures.Dto.CandidatureResponse;
import jobtrackerapp.servicecandidatures.Entity.ApplicationStatus;
import jobtrackerapp.servicecandidatures.Entity.Candidature;
import jobtrackerapp.servicecandidatures.Exception.ResourceNotFoundException;
import jobtrackerapp.servicecandidatures.Repository.CandidatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class  CandidatureService {

    private final CandidatureRepository repository;


    public CandidatureResponse create(CandidatureRequest request) {
        Candidature c = new Candidature();
        applyRequest(c, request);
        return toResponse(repository.save(c));
    }


    @Transactional(readOnly = true)
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