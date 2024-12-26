package fun.bitbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ProducerController {
    @Autowired
    private ProducerService producerService;

    @PostMapping("/send")
    public String send(@RequestBody User user) {
        producerService.send(user);
        return "test";
    }
}
