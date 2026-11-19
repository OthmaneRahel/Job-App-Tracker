package jobtrackerapp.servicenotifications.services;


import jobtrackerapp.servicenotifications.dto.NotificationPayload;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service @AllArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;


    public void envoyerMail(NotificationPayload notificationPayload){
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom("noreply@com.com");
        mailMessage.setTo(notificationPayload.destinataire());
        mailMessage.setSubject(notificationPayload.title());
        mailMessage.setText(notificationPayload.message());


        mailSender.send(mailMessage);
    }



}
