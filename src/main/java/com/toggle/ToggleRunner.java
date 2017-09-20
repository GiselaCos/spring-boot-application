package com.toggle;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ToggleRunner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final ToggleReceiver toggleReceiver;
    private final ConfigurableApplicationContext context;

    public ToggleRunner(ToggleReceiver toggleReceiver, RabbitTemplate rabbitTemplate,
                        ConfigurableApplicationContext context) {
        this.toggleReceiver = toggleReceiver;
        this.rabbitTemplate = rabbitTemplate;
        this.context = context;
    }

    @Override
    public void run(String... args) throws Exception {
        /*System.out.println("Sending message to observer...");
        rabbitTemplate.convertAndSend(ToggleApplication.queueName, "Toggle status changed!");
        toggleReceiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
        context.close();*/
    }

}
