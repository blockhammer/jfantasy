package org.jfantasy.website.runtime;

import freemarker.template.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.file.FileManager;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.freemarker.FreeMarkerTemplateUtils;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.ognl.OgnlUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.jfantasy.website.PageInstance;
import org.jfantasy.website.bean.*;
import org.jfantasy.website.bean.enums.PageType;
import org.jfantasy.website.service.PageItemDataService;
import org.jfantasy.website.service.PageItemService;
import org.jfantasy.website.util.GeneratePageUtil;
import org.jfantasy.website.util.TemplateModelUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * 生成静态页面
 * Created by wml on 2015/1/26.
 */
public class GeneratePage implements PageInstance {

    private static Log logger = LogFactory.getLog(GeneratePage.class);

    private transient Configuration configuration;

    private Page page;
    private FileManager fileManager;

    public GeneratePage(Page page,FileManager fileManager) {
        this.page = page;
        this.fileManager = fileManager;
        configuration = SpringContextUtil.getBeanByType(Configuration.class);
    }

    @Override
    public void execute(){
        try {
            // 模版
            final org.jfantasy.website.bean.Template template = page.getTemplate();
            // 数据定义
            List<DataInferface> dataInferfaces = template.getDataInferfaces();
            // 所有数据
            final Map<String, Object> dm = new HashMap<String, Object>();
            for(DataInferface dataInferface : dataInferfaces){
                Data data = ObjectUtil.find(page.getDatas(), "dataInferface.id", dataInferface.getId());
                if(dataInferface.getKey().equals(template.getDataKey())){
                    continue;
                }
                dm.put(dataInferface.getKey(), GeneratePageUtil.getValue(data));
            }

            if(template.getPageType()==PageType.pagination){    // 分页
                List<Object> list = (List<Object>) dm.get(template.getDataKey());
                final Pager pager = new Pager(page.getPageSize());
                pager.setTotalCount(list.size());
                pager.setCurrentPage(1);
                for (int i = 1; i <= pager.getTotalPage(); pager.setCurrentPage(pager.getCurrentPage() + 1),i++) {
                    int start = pager.getPageSize() * (pager.getCurrentPage()-1);
                    int end = Math.min(pager.getPageSize() * pager.getCurrentPage(), pager.getTotalCount());
                    pager.setPageItems(list.subList(start, end));
                    final Map<String, Object> pageDataMap = new HashMap<String, Object>();
                    pageDataMap.put(template.getDataKey(),pager);
                    pageDataMap.putAll(dm);
                    // /aaaa/xxxx/bbbb/p_${art.id}.html
                    String path = RegexpUtil.replace(page.getPath(), "\\$\\{([a-zA-Z.]+)\\}", new RegexpUtil.AbstractReplaceCallBack() {
                        @Override
                        public String doReplace(String text, int index, Matcher matcher) {
                        return StringUtil.nullValue(OgnlUtil.getInstance().getValue($(1),pageDataMap));
                        }
                    });
                    FreeMarkerTemplateUtils.writer(TemplateModelUtils.createScopesHashModel(pageDataMap), configuration.getTemplate(template.getPath()), fileManager.writeFile(path));
                    // 页面生成成功，记录pageItem
//                    this.savePageItem("");
                }
            }else if(template.getPageType()==PageType.multi){   // 多页面
                Data data = ObjectUtil.find(page.getDatas(), "dataInferface.key", template.getDataKey());
                List<Object> list = (List<Object>) GeneratePageUtil.getValue(data);
                for(Object o : list){
                    final Map<String, Object> pageDataMap = new HashMap<String, Object>();
                    pageDataMap.put(template.getDataKey(), o);
                    pageDataMap.putAll(dm);
                    String path = RegexpUtil.replace(page.getPath(), "\\$\\{([a-zA-Z.]+)\\}", new RegexpUtil.AbstractReplaceCallBack() {
                        @Override
                        public String doReplace(String text, int index, Matcher matcher) {
                        return StringUtil.nullValue(OgnlUtil.getInstance().getValue($(1),pageDataMap));
                        }
                    });
                    FreeMarkerTemplateUtils.writer(TemplateModelUtils.createScopesHashModel(pageDataMap), configuration.getTemplate(template.getPath()), fileManager.writeFile(path));
                    this.savePageItem(pageDataMap,path,page);
                }
            }else{   // 单页面
                String fileName = page.getPath();
                FreeMarkerTemplateUtils.writer(TemplateModelUtils.createScopesHashModel(dm), configuration.getTemplate(template.getPath()), fileManager.writeFile(fileName));
                this.savePageItem(dm,fileName,page);
            }
        } catch (IOException e) {
            logger.error("IOException...writeFile exception..."+e.getMessage());
        }

    }

    private void savePageItem(Map<String,Object> dm,String fileName,Page page){
        PageItemService pageItemService = SpringContextUtil.getBeanByType(PageItemService.class);
        PageItemDataService pageItemDataService = SpringContextUtil.getBeanByType(PageItemDataService.class);
        PageItem pageItem = new PageItem();
        pageItem.setPage(page);
        pageItem.setContent("");
        pageItem.setFile(fileName);
        List<PageItemData> pageItemDatas = new ArrayList<PageItemData>();
        pageItem.setPageItemDatas(pageItemDatas);

        for (Map.Entry<String, Object> entry : dm.entrySet()) {
            Object hibernateEntityObject = null;
            if(entry.getValue() instanceof List){
                if(entry.getValue()==null || ((List) entry.getValue()).size()==0){
                    continue;
                }
                hibernateEntityObject = ((List) entry.getValue()).get(0);
            }else if(entry.getValue() instanceof Array){
                if(entry.getValue()==null || ((Object[]) entry.getValue()).length==0){
                    continue;
                }
                hibernateEntityObject = ((Object[]) entry.getValue())[0];
            }else{
                hibernateEntityObject = entry.getValue();
            }
            Class clazz = ClassUtil.getRealClass(hibernateEntityObject.getClass());
            Entity entity = (Entity) clazz.getAnnotation(Entity.class);
            if(entity!=null){
                Field[] field = ClassUtil.getDeclaredFields(clazz,Id.class);
                Object id = null;
                if(field.length == 1){
                    id = ClassUtil.getValue(hibernateEntityObject,field[0].getName());
                }
                PageItemData pageItemData = new PageItemData();
                pageItemData.setPageItem(pageItem);
                pageItemData.setClassName(clazz.getName());
                pageItemData.setBeanId(id.toString());
                pageItemDatas.add(pageItemData);
                if(entry.getKey().equals(page.getTemplate().getDataKey())){
                    // 多页面或分页
                    pageItem.setCode(id.toString());
                }
            }
        }
        // 保存pageItem
        pageItemService.save(pageItem);
        // 保存pageItemData
        for(PageItemData pageItemData : pageItemDatas){
            pageItemDataService.save(pageItemData);
        }
    }

    public static void main(String[] args){
        System.out.println(RegexpUtil.replace("/template/art_test_list_${articles.currentPage}.html", "\\$\\{([a-zA-Z.]+)\\}", new RegexpUtil.AbstractReplaceCallBack() {
            @Override
            public String doReplace(String text, int index, Matcher matcher) {
                return "xxxx";
            }
        }));
    }

}
