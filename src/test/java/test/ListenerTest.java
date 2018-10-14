package test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Rimantas Jacikevičius on 18.10.14.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@ActiveProfiles(profiles = "local")
@DirtiesContext
@Profile("local")
public class ListenerTest {


    @Autowired
    private AmqpTemplate template;

    @Autowired
    private AmqpAdmin admin;

    @Autowired
    private RabbitListenerEndpointRegistry registry;

    @Test
    // This one passes
    public void test() {
        String req = "FOO";
        String result = template.convertSendAndReceiveAsType(Listener.TEST_QUEUE, req, ParameterizedTypeReference.forType(String.class));
        assertThat(result).isEqualTo(req + " result");
    }

    @Test
    public void testRuntimeQueueDeclaration() {

        Queue declaredQueue = new Queue("new.queue", false);

        admin.declareQueue(declaredQueue);

        SimpleMessageListenerContainer listener = (SimpleMessageListenerContainer) registry.getListenerContainer(Listener.TEST_2_ID);
        listener.addQueues(declaredQueue);
        listener.start();

        String req = "FOO";
        String result = template.convertSendAndReceiveAsType(declaredQueue.getName(), req, ParameterizedTypeReference.forType(String.class));
        assertThat(result).isEqualTo(req + " result");
    }

}
