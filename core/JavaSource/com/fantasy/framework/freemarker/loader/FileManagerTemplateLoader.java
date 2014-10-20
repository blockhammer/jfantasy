package com.fantasy.framework.freemarker.loader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fantasy.file.FileItem;
import com.fantasy.file.FileManager;
import com.fantasy.file.service.FileManagerFactory;
import com.fantasy.framework.util.common.StringUtil;

import freemarker.cache.TemplateLoader;

public class FileManagerTemplateLoader implements TemplateLoader {

	private final static Log logger = LogFactory.getLog(FileManagerTemplateLoader.class);
	
	private FileManager fileManager;
	private String fileManagerBeanName;

	@SuppressWarnings("static-access")
	private FileManager getFileManager() {
		if (fileManager != null) {
			return fileManager;
		} else if (StringUtil.isNotBlank(fileManagerBeanName)) {
			while(FileManagerFactory.getInstance() == null){
				try {
					Thread.currentThread().sleep(TimeUnit.MILLISECONDS.toNanos(1000));
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
			return fileManager = FileManagerFactory.getInstance().getFileManager(fileManagerBeanName);
		}
		throw new RuntimeException("fileManager 与  fileManagerBeanName 不能同时为空!");
	}

	public void closeTemplateSource(Object templateSource) throws IOException {
	}

	public Object findTemplateSource(String name) throws IOException {
		name = name.startsWith("/") ? name : ("/" + name);
		return getFileManager().getFileItem(name) != null ? name : null;
	}

	public long getLastModified(Object templateSource) {
		String templateName = (String) templateSource;
		FileItem fileItem = getFileManager().getFileItem(templateName);
		return fileItem.lastModified().getTime();
	}

	public Reader getReader(Object templateSource, final String encoding) throws IOException {
		String templateName = (String) templateSource;
		return new InputStreamReader(getFileManager().readFile(templateName), encoding);
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setFileManagerBeanName(String beanName) {
		this.fileManagerBeanName = beanName;
	}

}