package dang.kp.manager.sys.init;

import dang.kp.manager.sys.init.detail.StartUpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Component
public class StartUpCommand implements CommandLineRunner {

    @Resource
    private Map<String, StartUpService> startUpServiceMap;

    @Override
    public void run(String... args) throws Exception {
        for (StartUpService startUpService : startUpServiceMap.values()) {
            startUpService.run();
        }
    }
}
