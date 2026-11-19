package jobtrackerapp.servicecandidatures.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {
    public static final String QUEUE_NAME_FOLLOW_UP = "notifications_follow_up.queue";
    public static final String QUEUE_NAME_STATUS_CHANGED = "notifications_status_changed.queue";
    public static final String EXCHANGE_NAME = "notifications.exchange";

    @Bean
    public Queue queue_follow_up() {
        return new Queue(QUEUE_NAME_FOLLOW_UP, true);
    }

    @Bean
    public Queue queue_status_changed() {
        return new Queue(QUEUE_NAME_STATUS_CHANGED, true);
    }

    @Bean
    public Queue queue_candidature_cree() {
        return new Queue("candidature-valide", true);
    }

    @Bean
    public DirectExchange monExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding mainBinding(Queue queue_status_changed, DirectExchange exchange) {
        return BindingBuilder.bind(queue_status_changed).to(exchange).with("routing.status_changed");
    }

    @Bean
    public Binding followUpBinding(Queue queue_follow_up, DirectExchange exchange) {
        return BindingBuilder.bind(queue_follow_up).to(exchange).with("routing.follow-up");
    }

    @Bean
    public Binding CandidatureCreeBinding(Queue queue_candidature_cree, DirectExchange exchange) {
        return BindingBuilder.bind(queue_candidature_cree()).to(exchange).with("routing.candidature");
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new JacksonJsonMessageConverter();
    }


}
