package dang.kp.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ManagerApplication {
    public static void main(String[] args) {
        log.info("App starting...");
        SpringApplication.run(ManagerApplication.class, args);
        log.info("App started!!!");
    }
}
