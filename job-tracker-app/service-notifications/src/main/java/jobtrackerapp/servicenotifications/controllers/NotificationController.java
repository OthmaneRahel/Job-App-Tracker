package jobtrackerapp.servicenotifications.controllers;


import jakarta.validation.Valid;
import jobtrackerapp.servicenotifications.dto.NotificationPayload;
import jobtrackerapp.servicenotifications.services.NotificationService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController @AllArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    public final NotificationService notificationService;

    @PostMapping("/new-notification")
    public void envoyerNotification(@RequestBody @Valid NotificationPayload notificationPayload){
        notificationService.envoyerMail(notificationPayload);
    }




}
