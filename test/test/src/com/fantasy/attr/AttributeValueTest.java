package org.jfantasy.attr;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.attr.storage.bean.Attribute;
import org.jfantasy.attr.storage.bean.AttributeValue;
import org.jfantasy.framework.util.jackson.JSON;
import org.junit.Test;

public class AttributeValueTest {

    private final static Log LOG = LogFactory.getLog(AttributeValueTest.class);

    @Test
    public void testGetJSON() throws Exception {
        AttributeValue value = new AttributeValue();
        value.setId(1l);
        value.setTargetId(1l);
        value.setAttribute(new Attribute());
        value.getAttribute().setCode("test");
        value.setValue("0001");

        String json = JSON.serialize(value);

        LOG.debug(json);

        AttributeValue _value = JSON.deserialize(json,AttributeValue.class);

        Assert.assertEquals("test", _value.getAttribute().getCode());

    }

}