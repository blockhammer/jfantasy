package org.jfantasy.attr.typeConverter;

import org.jfantasy.attr.framework.converter.UserTypeConverter;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.util.ognl.OgnlUtil;
import org.jfantasy.security.bean.User;
import junit.framework.Assert;
import ognl.TypeConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
public class UserTypeConverterTest {

    private final static Log logger = LogFactory.getLog(UserTypeConverterTest.class);

    @Test
    public void testConvertValue() throws Exception {
        TypeConverter typeConverter = SpringContextUtil.createBean(UserTypeConverter.class,SpringContextUtil.AutoType.AUTOWIRE_BY_TYPE);

        Object user = typeConverter.convertValue(new HashMap(),null,null,null,"admin",User.class);

        logger.debug(user);

        Assert.assertEquals(user.getClass(), User.class);

        Object username = typeConverter.convertValue(new HashMap(),null,null,null,user,String.class);

        logger.debug(username);

        Assert.assertEquals(username, "admin");

        OgnlUtil ognlUtil = OgnlUtil.getInstance("attr");
        ognlUtil.addTypeConverter(User.class, typeConverter);

        TypeBean typeBean = new TypeBean();
        ognlUtil.setValue("key",typeBean,"test");
        ognlUtil.setValue("user",typeBean,"admin");

        logger.debug(typeBean);

        Assert.assertNotNull(typeBean.getUser());
        Assert.assertEquals(typeBean.getUser().getUsername(),"admin");

        user = ognlUtil.getValue("user",typeBean);

        logger.debug(user);

        username = ognlUtil.getValue("user",typeBean,String.class);

        logger.debug(username);
        Assert.assertEquals(username,"admin");

    }
}