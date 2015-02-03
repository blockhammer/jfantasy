package com.fantasy.system.service;

import com.fantasy.framework.util.jackson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
public class WebsiteServiceTest {

    private static Log LOG = LogFactory.getLog(WebsiteServiceTest.class);

    @Autowired
    private WebsiteService websiteService;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    @Transactional
    public void testGetAll() throws Exception {
        LOG.debug(JSON.serialize( websiteService.getAll()));
    }
}