package org.jfantasy.pay.dao.listener;

import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.pay.bean.OrderServer;
import org.jfantasy.pay.service.OrderServerService;

import java.util.Arrays;

public class OrderServerChangedListener implements PostInsertEventListener, PostUpdateEventListener {

    private OrderServerService orderServerService;

    public OrderServerService orderServerService(){
        if(orderServerService == null){
            return orderServerService = SpringContextUtil.getBeanByType(OrderServerService.class);
        }
        return orderServerService;
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        Class<?> aClass = ClassUtil.forName(persister.getRootEntityName());
        assert aClass != null;
        return OrderServer.class.isAssignableFrom(aClass);
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if(!event.getEntity().getClass().isAssignableFrom(OrderServer.class)){
            return;
        }
        OrderServer entity = (OrderServer) event.getEntity();
        if (entity.isEnabled()) {
            orderServerService().register(entity);
        }
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if(!event.getEntity().getClass().isAssignableFrom(OrderServer.class)){
            return;
        }
        if (modify(event, "enabled")) {
            OrderServer entity = (OrderServer) event.getEntity();
            if (entity.isEnabled()) {
                orderServerService().register(entity);
            } else {
                orderServerService().unregister(entity);
            }
        }
    }

    /**
     * 如果返回true代表没有改变值
     *
     * @param event    事件
     * @param property 字段名
     * @return bool
     */
    private boolean modify(PostUpdateEvent event, String property) {
        if(event.getOldState() == null){
            return true;
        }
        Arrays.binarySearch(event.getPersister().getPropertyNames(), property);
        int index = ObjectUtil.indexOf(event.getPersister().getPropertyNames(), property);
        return index != -1 && event.getState()[index].equals(event.getOldState()[index]);
    }


}