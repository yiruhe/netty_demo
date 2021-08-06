package com.netty.chat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author tiankong
 * @date 2019/11/19 19:59
 */
public class ChatGroupDo implements Serializable {
    private Integer id;

    private String groupName;

    private String icon;

    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Short getMaxNumberOfPeople() {
        return maxNumberOfPeople;
    }

    public void setMaxNumberOfPeople(Short maxNumberOfPeople) {
        this.maxNumberOfPeople = maxNumberOfPeople;
    }

    public Boolean getSaveDb() {
        return saveDb;
    }

    public void setSaveDb(Boolean saveDb) {
        this.saveDb = saveDb;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * 群类型
     */
    private Byte type;

    /**
     * 加群人数最大值
     */
    private Short maxNumberOfPeople;

    /**
     * 是否保存数据到数据库
     */
    private Boolean saveDb;

    private Date createTime;

    private Date modifiedTime;

    private static final long serialVersionUID = 1L;
}