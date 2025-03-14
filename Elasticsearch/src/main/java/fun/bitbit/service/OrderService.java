package fun.bitbit.service;

import com.alibaba.fastjson2.JSON;
import fun.bitbit.model.Order;
import fun.bitbit.model.OrderItem;
import fun.bitbit.model.OrderItemExtend;
import fun.bitbit.model.OrderItemPay;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 * Date: 2024/4/27 17:15
 */
@Slf4j
@Service
public class OrderService {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    public List<Order> matchByItemProductName(String text) {
        SearchHits<Order> searchHits;

        Query fuzzyQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery().must(QueryBuilders.nestedQuery("orderItems", QueryBuilders.matchQuery("orderItems.productName", text), ScoreMode.None)))
                .build();
        searchHits = restTemplate.search(fuzzyQuery, Order.class);
        searchHits.stream().iterator().forEachRemaining(e -> log.info("match query order: {}", JSON.toJSONString(e.getContent().getOrderItemList().stream().map(OrderItem::getProductName).collect(Collectors.toList()))));
        return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    public void testCurd() {
        String orderId = "1001";
        Order order = new Order();
        order.setOrderId(orderId);
        order.setChanel("store");
        order.setOrderTime(new Date());
        order.setMemInCardNo("no0001");

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId("10010001");
        orderItem.setProductName("iphone max 512G red");

        OrderItemExtend orderItemExtend = new OrderItemExtend();
        orderItemExtend.setShopAddCode("101");
        orderItemExtend.setShopType("store");
        orderItemExtend.setDeliveryType("express");
        orderItem.setOrderItemExtend(orderItemExtend);

        OrderItemPay orderItemPay = new OrderItemPay();
        orderItemPay.setCouponRuleId("rule500");
        orderItemPay.setCouponType("ticket");
        orderItem.setOrderItemPayList(Collections.singletonList(orderItemPay));

        order.setOrderItemList(Collections.singletonList(orderItem));
        Order saveResult = restTemplate.save(order);
        log.info("save result:{}", JSON.toJSONString(saveResult));

        Order getOrder = restTemplate.get(orderId, Order.class);
        log.info("id get order:{}", JSON.toJSONString(getOrder));

        Query query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("memInCardNo", "mem00001"))
                .build();
        SearchHits<Order> searchHits = restTemplate.search(query, Order.class);
        searchHits.stream().iterator().forEachRemaining(e -> log.info("term query order: {}", JSON.toJSONString(e.getContent())));

        String deleteResult = restTemplate.delete(orderId, Order.class);
        log.info("delete result: {}", deleteResult);
    }
}
