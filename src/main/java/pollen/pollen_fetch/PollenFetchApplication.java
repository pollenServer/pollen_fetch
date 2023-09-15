package pollen.pollen_fetch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PollenFetchApplication {

    public static void main(String[] args) {
        SpringApplication.run(PollenFetchApplication.class, args);
    }

}
