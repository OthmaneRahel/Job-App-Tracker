package jobtrackerapp.servicecandidatures.messaging;


import jobtrackerapp.servicecandidatures.Dto.NotificationPayload;
import jobtrackerapp.servicecandidatures.config.RabbitConfig;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service @AllArgsConstructor
public class NotificationPublisher {
    public final RabbitTemplate rabbitTemplate;

    public void envoyerNotification_follow_up(NotificationPayload notificationPayload) {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_NAME,
                    "routing.follow-up",
                    notificationPayload
            );
            System.out.println("Message envoyé : " + notificationPayload);
    }

    public void envoyerNotification_status_changed(NotificationPayload notificationPayload) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                "routing.status_changed",
                notificationPayload
        );
        System.out.println("Message envoyé : " + notificationPayload);
    }


    public void envoyerNotifcandidature(NotificationPayload notificationPayload) {
        rabbitTemplate.convertAndSend(
                "notifications.exchange",
                "routing.candidature",
                notificationPayload
        );
        System.out.println("Message envoyé : " + notificationPayload);
    }

}
