package com.fantasy.common.order;

import com.fantasy.framework.spring.mvc.error.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OrderServiceFactory {

    private Map<String, OrderService> orderDetailsServices;

    public OrderServiceFactory() {
        orderDetailsServices = new HashMap<String, OrderService>();
        orderDetailsServices.put("TEST", new TestOrderDetailsService());
    }

    public OrderServiceFactory(Map<String, OrderService> orderDetailsServices) {
        this.orderDetailsServices = orderDetailsServices;
    }

    public void register(String type, OrderService orderDetailsService) {
        orderDetailsServices.put(type, orderDetailsService);
    }

    public OrderService getOrderService(String type) {
        if (!this.orderDetailsServices.containsKey(type)) {
            throw new NotFoundException("orderType[" + type + "] 对应的 PaymentOrderService 未配置！");
        }
        return orderDetailsServices.get(type);
    }

}
