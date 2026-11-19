package jobtrackerapp.servicenotifications.messaging;


import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jobtrackerapp.servicenotifications.dto.NotificationPayload;
import jobtrackerapp.servicenotifications.services.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotficationConsumer {

    private final NotificationService notificationService;
    private final Validator validator;
    public NotficationConsumer(NotificationService notificationService, Validator
            validator) {
        this.notificationService = notificationService;
        this.validator = validator;
    }


    @RabbitListener(queues = {"candidature-valide","notifications_follow_up.queue"})
    public void recevoir(NotificationPayload notificationEmailDTO){
        System.out.println("hello hello");
        var erreurs = validator.validate(notificationEmailDTO);
        System.out.println("haya l resultat dyal erreurs "+erreurs.toString());
        if (!erreurs.isEmpty()){
            throw new ConstraintViolationException(erreurs);
        }
        notificationService.envoyerMail(notificationEmailDTO);
    }

}
