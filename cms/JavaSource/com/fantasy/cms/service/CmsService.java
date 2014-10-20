package com.fantasy.cms.service;

import com.fantasy.cms.bean.Article;
import com.fantasy.cms.bean.ArticleCategory;
import com.fantasy.cms.dao.ArticleCategoryDao;
import com.fantasy.cms.dao.ArticleDao;
import com.fantasy.framework.dao.Pager;
import com.fantasy.framework.dao.hibernate.PropertyFilter;
import com.fantasy.framework.lucene.BuguSearcher;
import com.fantasy.framework.spring.SpringContextUtil;
import com.fantasy.framework.util.common.DateUtil;
import com.fantasy.framework.util.common.ObjectUtil;
import com.fantasy.framework.util.common.StringUtil;
import com.fantasy.framework.util.htmlcleaner.HtmlCleanerUtil;
import com.fantasy.framework.util.jackson.JSON;
import com.fantasy.framework.util.regexp.RegexpUtil;
import freemarker.template.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.htmlcleaner.TagNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * CMS service
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-5-28 下午03:05:30
 */
@Service("fantasy.cms.CmsService")
@Transactional
public class CmsService extends BuguSearcher<Article> {

    private static final Log logger = LogFactory.getLog(CmsService.class);

    @Resource
    private ArticleCategoryDao articleCategoryDao;
    @Resource
    private ArticleDao articleDao;

    /**
     * 获取全部栏目
     *
     * @return List<ArticleCategory>
     */
    public List<ArticleCategory> getCategorys() {
        return articleCategoryDao.find(new Criterion[0], "layer,sort", "asc,asc");
    }

    public List<ArticleCategory> getCategorys(String code) {
        ArticleCategory category = this.get(code);
        return articleCategoryDao.find(new Criterion[]{Restrictions.like("path", category.getPath(), MatchMode.START), Restrictions.ne("code", code)}, "layer,sort", "asc,asc");
    }

    /**
     * 文章查询方法
     *
     * @param pager    翻页对象
     * @param filters 筛选条件
     * @return string
     */
    public Pager<Article> findPager(Pager<Article> pager, List<PropertyFilter> filters) {
        return articleDao.findPager(pager, filters);
    }

    /**
     * 保存栏目
     *
     * @param category 分类对象
     * @return string
     */
    public ArticleCategory save(ArticleCategory category) {
        if (logger.isDebugEnabled()) {
            logger.debug("保存栏目 > " + JSON.serialize(category));
        }
        List<ArticleCategory> categories;
        boolean root = false;
        if (category.getParent() == null || StringUtil.isBlank(category.getParent().getCode())) {
            category.setLayer(0);
            category.setPath(category.getCode() + ArticleCategory.PATH_SEPARATOR);
            root = true;
            categories = ObjectUtil.sort(articleCategoryDao.find(Restrictions.isNull("parent")), "sort", "asc");
        } else {
            ArticleCategory parentCategory = this.articleCategoryDao.get(category.getParent().getCode());
            category.setLayer(parentCategory.getLayer() + 1);
            category.setPath(parentCategory.getPath() + category.getCode() + ArticleCategory.PATH_SEPARATOR);// 设置path
            categories = ObjectUtil.sort(articleCategoryDao.findBy("parent.code", parentCategory.getCode()), "sort", "asc");
        }
        ArticleCategory old = category.getCode() != null ? this.articleCategoryDao.get(category.getCode()) : null;
        if (old != null) {// 更新数据
            if (category.getSort() != null && (ObjectUtil.find(categories, "code", old.getCode()) == null || !old.getSort().equals(category.getSort()))) {
                if (ObjectUtil.find(categories, "code", old.getCode()) == null) {// 移动了节点的层级
                    int i = 0;
                    for (ArticleCategory m : ObjectUtil.sort((old.getParent() == null || StringUtil.isBlank(old.getParent().getCode())) ? articleCategoryDao.find(Restrictions.isNull("parent")) : articleCategoryDao.findBy("parent.id", old.getParent().getCode()), "sort", "asc")) {
                        m.setSort(i++);
                        this.articleCategoryDao.save(m);
                    }
                    categories.add(category.getSort() - 1, category);
                } else {
                    ArticleCategory t = ObjectUtil.remove(categories, "code", old.getCode());
                    if (categories.size() >= category.getSort()) {
                        categories.add(category.getSort() - 1, t);
                    } else {
                        categories.add(t);
                    }
                }
                // 重新排序后更新新的位置
                for (int i = 0; i < categories.size(); i++) {
                    ArticleCategory m = categories.get(i);
                    if (m.getCode().equals(category.getCode())) {
                        continue;
                    }
                    m.setSort(i + 1);
                    this.articleCategoryDao.save(m);
                }
            }
        } else {// 新增数据
            category.setSort(categories.size() + 1);
        }
        this.articleCategoryDao.save(category);
        if (root) {
            category.setParent(null);
            this.articleCategoryDao.update(category);
        }
        return category;
    }

    /**
     * 得到栏目
     *
     * @param code categoryCode
     * @return ArticleCategory
     */
    public ArticleCategory get(String code) {
        return this.articleCategoryDao.get(code);
    }

    /**
     * 移除栏目
     *
     * @param code 栏目 Code
     * @return ArticleCategory
     */
    public ArticleCategory remove(String code) {
        ArticleCategory category = articleCategoryDao.get(code);
        articleCategoryDao.delete(category);
        return category;
    }

    /**
     * 保存文章
     *
     * @param article 文章对象
     * @return Article
     */
    public Article save(Article article) {
        if (logger.isDebugEnabled()) {
            logger.debug("保存文章 > " + JSON.serialize(article));
        }
        if ("".equals(article.getSummary())) {
            TagNode node = HtmlCleanerUtil.htmlCleaner(article.getContent().getContent());
            String content = node.getText().toString().trim().replace("\n", "");
            String summary = content.substring(0, 10);
            article.setSummary(summary);
        }
        this.articleDao.save(article);
        return article;
    }

    /**
     * 获取文章
     *
     * @param id 文章id
     * @return Article
     */
    public Article get(Long id) {
        return this.articleDao.get(id);
    }

    /**
     * 发布文章
     *
     * @param ids 文章 ids
     */
    public void issue(Long[] ids) {
        for (Long id : ids) {
            Article art = this.get(id);
            art.setIssue(true);
            art.setReleaseDate(DateUtil.format("yyyy-MM-dd hh:mm"));
            this.save(art);
        }
    }

    /**
     * 关闭文章
     *
     * @param ids 文章 ids
     */
    public void colseissue(Long[] ids) {
        for (Long id : ids) {
            Article art = this.get(id);
            art.setIssue(false);
            art.setReleaseDate(DateUtil.format("yyyy-MM-dd hh:mm"));
            this.save(art);
        }
    }

    /**
     * 删除文章
     *
     * @param ids 文章 ids
     */
    public void delete(Long[] ids) {
        for (Long id : ids) {
            this.articleDao.delete(id);
        }
    }

    /**
     * 文章分类编码唯一
     *
     * @param code 分类 code
     * @return boolean
     */
    public boolean articleCategoryUnique(String code) {
        return this.articleCategoryDao.findUniqueBy("code", code) == null;
    }

    public List<Article> find(List<PropertyFilter> filters, String orderBy, String order, int size) {
        if (StringUtil.isBlank(orderBy)) {
            return this.articleDao.find(filters, 0, size);
        } else {
            return this.articleDao.find(filters, orderBy, StringUtil.defaultValue(order, Pager.Order.asc.name()), 0, size);
        }
    }

    /**
     * 移动文章
     *
     * @param ids          文章 ids
     * @param categoryCode 分类 categoryCode
     */
    public void moveArticle(Long[] ids, String categoryCode) {
        ArticleCategory articleCategory = this.articleCategoryDao.get(categoryCode);
        for (Long id : ids) {
            Article article = this.articleDao.get(id);
            article.setCategory(articleCategory);
            this.articleDao.save(article);
        }
    }

    public List<ArticleCategory> listArticleCategory() {
        return this.articleCategoryDao.find(Restrictions.isNull("parent"));
    }

    public static List<ArticleCategory> articleCategoryList() {
        CmsService cmsService = SpringContextUtil.getBeanByType(CmsService.class);
        return cmsService.listArticleCategory();
    }

    /**
     * 根据分类code跳转不同界面
     *
     * @param templateUrl 模板 url
     * @param object      object
     * @return String
     */
    public static String getTemplatePath(String templateUrl, final Object object) {
        if (object == null || (!(object instanceof ArticleCategory) && !(object instanceof Article))) {
            return RegexpUtil.replace(templateUrl, "\\{code\\}", "");
        }
        Configuration configuration = SpringContextUtil.getBean("freemarkerService", Configuration.class);
        ArticleCategory category = object instanceof ArticleCategory ? (ArticleCategory) object : ((Article) object).getCategory();

        do {
            String newTemplateUrl = RegexpUtil.replace(templateUrl, "\\{code\\}", "_" + category.getCode());
            try {
                configuration.getTemplate(newTemplateUrl);
                return newTemplateUrl;
            } catch (IOException e) {
                if (category.getParent() == null) {
                    break;
                }
                category = category.getParent();
            }
        } while (true);
        return RegexpUtil.replace(templateUrl, "\\{code\\}", "");
    }

}