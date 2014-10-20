package com.fantasy.common.service;

import com.fantasy.common.bean.FtpConfig;
import com.fantasy.common.dao.FtpConfigDao;
import com.fantasy.framework.dao.Pager;
import com.fantasy.framework.dao.hibernate.PropertyFilter;
import com.fantasy.framework.spring.SpringContextUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class FtpConfigService {

	@Resource
	private FtpConfigDao ftpConfigDao;

	public Pager<FtpConfig> findPager(Pager<FtpConfig> pager, List<PropertyFilter> filters) {
		return this.ftpConfigDao.findPager(pager, filters);
	}

	public List<FtpConfig> find(List<PropertyFilter> filters, String orderBy, String order) {
		return this.ftpConfigDao.find(filters, orderBy, order);
	}

    public List<FtpConfig> find(List<PropertyFilter> filters){
        return this.ftpConfigDao.find(filters);
    }

	public FtpConfig get(Long id) {
		return this.ftpConfigDao.get(id);
	}

	public void save(FtpConfig config) {
		this.ftpConfigDao.save(config);
	}

	public void delete(Long... ids) {
		for (Long id : ids) {
			this.ftpConfigDao.delete(id);
		}
	}

	public void listFile() {
		
	}

	public List<FtpConfig> getAll() {
		return this.ftpConfigDao.getAll();
	}
	
	public static List<FtpConfig> findftps(){
		FtpConfigService ftpConfigService = SpringContextUtil.getBeanByType(FtpConfigService.class);
		return ftpConfigService.getAll();
	}
	
	public static FtpConfig getFtp(Long id){
		FtpConfigService ftpConfigService = SpringContextUtil.getBeanByType(FtpConfigService.class);
		return ftpConfigService.get(id);
	}

}