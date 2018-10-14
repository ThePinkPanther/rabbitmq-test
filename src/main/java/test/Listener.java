package test;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created by Rimantas Jacikevičius on 18.10.14.
 */
@Component
public class Listener {


    public static final String TEST_QUEUE = "TEST";
    public static final String TEST_2_ID = "TEST_2";

    @RabbitListener(queues = TEST_QUEUE)
    public String test(String req) {
        return req + " result";
    }


    @RabbitListener(id = TEST_2_ID)
    public String test2(String req) {
        return req + " result";
    }

}
