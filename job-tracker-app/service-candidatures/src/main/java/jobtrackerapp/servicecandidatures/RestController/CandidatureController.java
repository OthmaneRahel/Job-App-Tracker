package jobtrackerapp.servicecandidatures.RestController;


import jobtrackerapp.servicecandidatures.Dto.CandidatureRequest;
import jobtrackerapp.servicecandidatures.Dto.CandidatureResponse;
import jobtrackerapp.servicecandidatures.Entity.ApplicationStatus;
import jobtrackerapp.servicecandidatures.Service.CandidatureService;
import jobtrackerapp.servicecandidatures.Service.IdempotencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidatures")
@RequiredArgsConstructor
public class CandidatureController {

    private final CandidatureService service;
    private final IdempotencyService idempotencyService;




//    @PostMapping
//    public ResponseEntity<CandidatureResponse> create(@Valid @RequestBody CandidatureRequest request) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
//    }

    @PostMapping
    public ResponseEntity<CandidatureResponse> create(
            @RequestHeader(value = "Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CandidatureRequest request) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new NullPointerException("idempotencyKey is null or empty");
        }

        CandidatureResponse response = idempotencyService.execute(
                idempotencyKey,
                "candidature:create:" + request.userId(),
                request,
                () -> service.create(request),
                CandidatureResponse.class
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public CandidatureResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<CandidatureResponse> getAll(@RequestParam(required = false) Long userId,
                                            @RequestParam(required = false) ApplicationStatus status) {
        return service.getAll(userId, status);
    }

    @PutMapping("/{id}")
    public CandidatureResponse update(@PathVariable Long id,
                                      @Valid @RequestBody CandidatureRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}