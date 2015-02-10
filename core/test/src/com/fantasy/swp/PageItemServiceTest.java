package com.fantasy.swp;

import com.fantasy.attr.bean.Article;
import com.fantasy.attr.bean.AttributeVersion;
import com.fantasy.attr.service.ArticleService;
import com.fantasy.attr.service.AttributeVersionService;
import com.fantasy.framework.dao.Pager;
import com.fantasy.framework.dao.hibernate.PropertyFilter;
import com.fantasy.swp.bean.Page;
import com.fantasy.swp.bean.PageItem;
import com.fantasy.swp.bean.PageItemData;
import com.fantasy.swp.service.PageItemDataService;
import com.fantasy.swp.service.PageItemService;
import com.fantasy.swp.service._PageService;
import org.apache.struts2.StrutsSpringJUnit4TestCase;
import org.apache.struts2.views.JspSupportServlet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
public class PageItemServiceTest extends StrutsSpringJUnit4TestCase {
    @Resource
    private PageItemService pageItemService;
    @Resource
    private PageItemDataService pageItemDataService;
    @Resource
    private _PageService pageService;
    @Resource
    private PageActionTest pageActionTest;
    @Resource
    private AttributeVersionService attributeVersionService;
    @Resource
    private ArticleService articleService;

    @Before
    public void setUp() throws Exception {
        JspSupportServlet jspSupportServlet = new JspSupportServlet();
        jspSupportServlet.init(new MockServletConfig());
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        this.testDelete();
    }

    @Test
    public void testSave() throws Exception {
        PageItem pageItem = new PageItem();
        pageItem.setFile("/file/test_file.html");
        List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
        filters.add(new PropertyFilter("EQS_name","PAGE_JUNIT_TEST"));
        List<Page> pages = pageService.find(filters);
        if(pages==null || pages.size()<=0){
            pageActionTest.setUp();
            pageActionTest.testSave();
            pages = pageService.find(filters);
        }
        pageItem.setPage(pages.get(pages.size()-1));
        pageItem.setContent("PAGEITEM_JUNIT_TEST");

        List<PageItemData> pageItemDatas = new ArrayList<PageItemData>();
        PageItemData pageItemData = new PageItemData();
        filters = new ArrayList<PropertyFilter>();
        filters.add(new PropertyFilter("EQS_title","ARTICLE_JUNIT_TEST"));

        Article article = new Article();
        List<Article> articles = articleService.find(filters);
        if(articles.size()<=0){
            article.setTitle("ARTICLE_JUNIT_TEST");
            article.setSummary("xxxx");
            Pager<AttributeVersion> pager = attributeVersionService.findPager(new Pager<AttributeVersion>(),new ArrayList<PropertyFilter>());
            if(pager.getPageItems()!=null && pager.getPageItems().size()>0){
                article.setVersion(pager.getPageItems().get(0));
            }
            this.articleService.save(article);
        }else{
            article = articles.get(0);
        }
        pageItemData.setBeanId(article.getId() + "");
        pageItemData.setClassName(Article.class.getName());
        pageItemData.setPageItem(pageItem);
        pageItemDatas.add(pageItemData);
        pageItem.setPageItemDatas(pageItemDatas);
        this.pageItemService.save(pageItem);
        this.pageItemDataService.save(pageItemData);
    }

    @Test
    public void testDelete(){
        List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
        filters.add(new PropertyFilter("EQS_content","PAGEITEM_JUNIT_TEST"));
        List<PageItem> pageItems = this.pageItemService.find(filters);
        if(pageItems.size()<=0){
            return;
        }
        Long[] ids = new Long[pageItems.size()];
        for(int i=0; i<pageItems.size(); i++){
            PageItem pageItem = pageItems.get(i);
            ids[i] = pageItem.getId();
        }
        this.pageItemService.delete(ids);
    }
}
