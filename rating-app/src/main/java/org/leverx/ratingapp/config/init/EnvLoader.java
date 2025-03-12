package org.leverx.ratingapp.config.init;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EnvLoader {

    @EventListener(ApplicationReadyEvent.class)
    public void loadEnv() {
        Dotenv dotenv = Dotenv.load(); // Loads .env file
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }
}
