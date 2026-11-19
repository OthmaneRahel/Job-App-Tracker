package jobtrackerapp.servicecandidatures.Dto;



import jobtrackerapp.servicecandidatures.enums.NotificationTypes;

import java.sql.Timestamp;


public record NotificationPayload(
    String destinataire,
    Long applicationId,
    NotificationTypes notificationType,
    String title,
    String message,
    boolean read,
    Timestamp createdAt
)
{}
