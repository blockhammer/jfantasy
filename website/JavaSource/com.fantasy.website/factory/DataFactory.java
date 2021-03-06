package org.jfantasy.website.factory;

import org.jfantasy.website.TemplateData;

import java.math.BigDecimal;

/**
 * 数据工厂
 */
public interface DataFactory {

    public TemplateData getStringData(String value);

    public TemplateData getTemplateData(Integer value);

    public TemplateData getTemplateData(BigDecimal value);

    public TemplateData getJSONData(String json);

    public TemplateData getHqlData(String hql);

}
