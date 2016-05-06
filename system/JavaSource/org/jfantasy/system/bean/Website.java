package org.jfantasy.system.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jfantasy.filestore.bean.FileManagerConfig;
import org.jfantasy.framework.dao.BaseBusEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 站点配置表，一个应用维护多套网站时，需要对每个网站的基本信息进行一些配置
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2014-2-17 上午11:07:30
 */
@Entity
@Table(name = "SYS_WEBSITE")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "defaultFileManager", "defaultUploadFileManager", "settings", "users", "rootMenu"})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Website extends BaseBusEntity {

    private static final long serialVersionUID = 3763626581086219087L;

    /**
     * 网站key（唯一）
     */
    @Id
    @Column(name = "CODE", length = 20, unique = true)
    private String key;
    /**
     * 网站名称
     */
    @Column(name = "NAME", length = 50)
    private String name;
    /**
     * 网址
     */
    @Column(name = "WEB", length = 100)
    private String web;
    /**
     * 网站对应的默认文件管理器(发布文章及日志)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEFAULT_FILEMANAGER", nullable = false, foreignKey = @ForeignKey(name = "FK_WEBSITE_DEF_FMID"))
    private FileManagerConfig defaultFileManager;
    /**
     * 网站对应的默认上传文件管理器(发布文章及日志)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEFAULT_UPLOAD_FILEMANAGER", nullable = false, foreignKey = @ForeignKey(name = "FK_WEBSITE_DEF_UPLOAD_FMID"))
    private FileManagerConfig defaultUploadFileManager;
    /**
     * 菜单根
     */
    @Column(name = "MENU_ID")
    private Long rootMenuId;
    /**
     * 网站设置
     */
    @OneToMany(mappedBy = "website", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Setting> settings = new ArrayList<Setting>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public FileManagerConfig getDefaultFileManager() {
        return defaultFileManager;
    }

    public void setDefaultFileManager(FileManagerConfig defaultFileManager) {
        this.defaultFileManager = defaultFileManager;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public void setSettings(List<Setting> settings) {
        this.settings = settings;
    }

    public FileManagerConfig getDefaultUploadFileManager() {
        return defaultUploadFileManager;
    }

    public void setDefaultUploadFileManager(FileManagerConfig defaultUploadFileManager) {
        this.defaultUploadFileManager = defaultUploadFileManager;
    }

    public Long getRootMenuId() {
        return rootMenuId;
    }

    public void setRootMenuId(Long rootMenuId) {
        this.rootMenuId = rootMenuId;
    }
}