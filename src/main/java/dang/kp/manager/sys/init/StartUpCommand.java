package dang.kp.manager.sys.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class StartUpCommand implements CommandLineRunner {

    @Autowired
    private Map<String, StartUpService> startUpServiceMap;

    @Override
    public void run(String... args) throws Exception {
        for (StartUpService startUpService : startUpServiceMap.values()) {
            startUpService.run();
        }
    }
}
