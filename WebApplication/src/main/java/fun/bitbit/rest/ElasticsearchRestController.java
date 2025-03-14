package fun.bitbit.rest;

import fun.bitbit.model.Order;
import fun.bitbit.model.User;
import fun.bitbit.model.resp.Result;
import fun.bitbit.service.OrderService;
import fun.bitbit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description:
 * Date: 2024/4/27 16:16
 */
@RestController
@RequestMapping("/elastic")
public class ElasticsearchRestController extends BaseController {

    @Autowired
    OrderService orderService;

    @Autowired
    UserService userService;

    @GetMapping("/testCurd")
    public Result testCurd() {
        orderService.testCurd();
        return success();
    }

    /**
     * 通过订单行商品名称，搜索订单
     */
    @GetMapping("/queryByProductName")
    public Result<List<Order>> queryByProductName(@RequestParam(required = false, defaultValue = "iphone 512G") String text) {
        return success(orderService.matchByItemProductName(text));
    }

    @GetMapping("/queryUser")
    public Result<Page<User>> queryUser() {
        return success(userService.matchQuery());
    }
}
