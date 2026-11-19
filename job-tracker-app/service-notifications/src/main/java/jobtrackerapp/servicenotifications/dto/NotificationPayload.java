package jobtrackerapp.servicenotifications.dto;


import jobtrackerapp.servicenotifications.enums.NotificationTypes;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;


public record NotificationPayload (
    String destinataire,
    Long applicationId,
    NotificationTypes notificationType,
    String title,
    String message,
    boolean read,
    String createdAt
)
{}
