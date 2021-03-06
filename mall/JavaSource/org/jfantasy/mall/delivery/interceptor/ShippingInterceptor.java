package org.jfantasy.mall.delivery.interceptor;

import org.jfantasy.mall.delivery.bean.DeliveryItem;
import org.jfantasy.mall.delivery.bean.Shipping;
import org.jfantasy.mall.goods.service.ProductService;
import org.jfantasy.mall.order.service.OrderService;
import org.jfantasy.mall.stock.service.StockService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 发货拦截器
 */
@Component
@Aspect
public class ShippingInterceptor {

	@Autowired
	private ProductService productService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private StockService stockService;

	/**
	 * 发货后，同步修改库存信息
	 * 
	 * @param point
	 */
	@Transactional
	@After("execution(public * org.jfantasy.mall.delivery.service.DeliveryTypeService.save(org.jfantasy.mall.delivery.bean.Shipping))")
	public void outStock(JoinPoint point) {
		Shipping shipping = (Shipping) point.getArgs()[0];
		stockService.outStock(shipping);
	}

	/**
	 * 发货后，更新商品占用数量
	 * 
	 * @param point
	 */
	@Transactional
	@After("execution(public * org.jfantasy.mall.delivery.service.DeliveryTypeService.save(org.jfantasy.mall.delivery.bean.Shipping))")
	public void freezeStore(JoinPoint point) {
		Shipping shipping = (Shipping) point.getArgs()[0];
		for (DeliveryItem item : shipping.getDeliveryItems()) {
//			productService.freezeStore(item.getProduct().getId(), -item.getQuantity());
		}
	}

	/**
	 * 发货后，判断订单是否发货完成
	 * 
	 * @param point
	 * @功能描述
	 */
	@Transactional
	@After("execution(public * org.jfantasy.mall.delivery.service.DeliveryTypeService.save(org.jfantasy.mall.delivery.bean.Shipping))")
	public void refreshOrder(JoinPoint point) {
		Shipping shipping = (Shipping) point.getArgs()[0];
//		orderService.shipping(shipping.getOrder().getId(), shipping);
	}

}
