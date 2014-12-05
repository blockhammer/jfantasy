package com.fantasy.cms.service;

import com.fantasy.cms.bean.Topic;
import com.fantasy.cms.dao.TopicDao;
import com.fantasy.framework.dao.Pager;
import com.fantasy.framework.dao.hibernate.PropertyFilter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

/**
 * 专题服务
 */
@Service
@Transactional
public class TopicService {

    @Resource
    private TopicDao topicDao;

    public Pager<Topic> findPager(Pager<Topic> pager, List<PropertyFilter> filters) {
        return topicDao.findPager(pager,filters);
    }

    public void delete(String... codes) {
    }

    public void save(Topic topic) {
    }

    public Topic get(String code) {
        return this.topicDao.get(code);
    }
}
