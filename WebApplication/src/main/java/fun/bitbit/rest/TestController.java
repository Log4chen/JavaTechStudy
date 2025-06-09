package fun.bitbit.rest;

import fun.bitbit.service.TestService;
import fun.bitbit.model.resp.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController extends BaseController {
    @Autowired
    TestService testService;

    @PostMapping("/echo")
    public Result echo(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Map<String, Object> res = body == null ? new HashMap<>() : body;
        request.getParameterMap().forEach((k, v) -> res.put(k, v[0]));
        testService.printRequestMap(res);
        return success(res);
    }

    @GetMapping("/createBytes")
    public Result createBytes(int mb) throws InterruptedException {
        byte[] bytes = new byte[mb * 1024 * 1024];
        Thread.sleep(1000 * 60);
        return success();
    }
}
