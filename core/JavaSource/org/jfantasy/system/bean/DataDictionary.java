package org.jfantasy.system.bean;

import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.jackson.JSON;
import org.jfantasy.system.bean.databind.DataDictionaryKeyDeserializer;
import org.jfantasy.system.bean.databind.DataDictionaryKeySerializer;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.List;

/**
 * 数据字典类
 * <br/>
 * 该类为了取代Config.java
 */
@ApiModel(value = "数据字典")
@Entity
@Table(name = "SYS_DD")
@IdClass(DataDictionaryKey.class)
@JsonFilter(JSON.CUSTOM_FILTER)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "parent", "children"})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DataDictionary extends BaseBusEntity {

    /**
     * 代码
     */
    @ApiModelProperty("代码")
    @Id
    private String code;
    /**
     * 配置类别
     */
    @ApiModelProperty("配置类别")
    @Id
    private String type;
    /**
     * 名称
     */
    @ApiModelProperty("名称")
    @Column(name = "NAME", length = 50)
    private String name;
    /**
     * 排序字段
     */
    @ApiModelProperty(hidden = true)
    @Column(name = "SORT")
    private Integer sort;
    /**
     * 描述
     */
    @ApiModelProperty("描述")
    @Column(name = "DESCRIPTION", length = 200)
    private String description;
    /**
     * 上级数据字典
     */
    @ApiModelProperty("上级数据字典")
    @ManyToOne(fetch = FetchType.LAZY, cascade = {javax.persistence.CascadeType.REFRESH})
    @JoinColumns(value = {@JoinColumn(name = "PCODE", referencedColumnName = "CODE"), @JoinColumn(name = "PTYPE", referencedColumnName = "TYPE")}, foreignKey = @ForeignKey(name = "FK_SYS_DD_PARENT"))
    private DataDictionary parent;
    /**
     * 下级数据字典
     */
    @ApiModelProperty(hidden = true)
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    @OrderBy("sort ASC")
    private List<DataDictionary> children;
    @Transient
    @ApiModelProperty("KEY")
    private DataDictionaryKey key;
    @Transient
    @ApiModelProperty("上级数据KEY")
    private DataDictionaryKey parentKey;

    @JsonSerialize(using = DataDictionaryKeySerializer.class)
    public DataDictionaryKey getKey() {
        return ObjectUtil.defaultValue(key, key = DataDictionaryKey.newInstance(this.code, this.type));
    }

    @JsonDeserialize(using = DataDictionaryKeyDeserializer.class)
    public void setKey(DataDictionaryKey key) {
        this.key = key;
        this.setCode(key.getCode());
        this.setType(key.getType());
    }

    @JsonSerialize(using = DataDictionaryKeySerializer.class)
    public DataDictionaryKey getParentKey() {
        if (this.getParent() == null) {
            return null;
        }
        return ObjectUtil.defaultValue(parentKey, parentKey = DataDictionaryKey.newInstance(this.parent.code, this.parent.type));
    }

    @JsonDeserialize(using = DataDictionaryKeyDeserializer.class)
    public void setParentKey(DataDictionaryKey key) {
        if(key == null){
            return;
        }
        this.parentKey = key;
        this.parent = new DataDictionary();
        this.parent.setCode(key.getCode());
        this.parent.setType(key.getType());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DataDictionary getParent() {
        return parent;
    }

    public void setParent(DataDictionary parent) {
        this.parent = parent;
    }

    public List<DataDictionary> getChildren() {
        return children;
    }

    public void setChildren(List<DataDictionary> children) {
        this.children = children;
    }

}
