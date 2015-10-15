package com.fantasy.website.dao;

import org.springframework.stereotype.Repository;

import com.fantasy.framework.dao.hibernate.HibernateDao;
import com.fantasy.website.bean.Template;

/**
 *@Author lsz
 *@Date 2014-1-2 下午4:48:25
 *
 */
@Repository
public class TemplateDao extends HibernateDao<Template, Long> {

}
