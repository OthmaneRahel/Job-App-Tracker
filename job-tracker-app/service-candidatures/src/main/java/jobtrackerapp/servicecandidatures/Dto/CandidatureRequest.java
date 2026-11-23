package jobtrackerapp.servicecandidatures.Dto;

import jobtrackerapp.servicecandidatures.enums.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CandidatureRequest(

        @NotNull(message = "userId est obligatoire") Long userId,
        @NotBlank(message = "companyName est obligatoire") String companyName,
        @NotBlank(message = "jobTitle est obligatoire") String jobTitle,
        String jobUrl,
        String location,
        String employmentType,
        ApplicationStatus status,
        LocalDate appliedAt,
        LocalDate nextFollowUpDate

) {}
