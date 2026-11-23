package jobtrackerapp.servicecandidatures.Dto;

import jobtrackerapp.servicecandidatures.enums.ApplicationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CandidatureResponse(
        Long id,
        Long userId,
        String companyName,
        String jobTitle,
        String jobUrl,
        String location,
        String employmentType,
        ApplicationStatus status,
        LocalDate appliedAt,
        LocalDate nextFollowUpDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
