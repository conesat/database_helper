package com.hg.idea.plugins.model;

public class EntityItem {
    private String sqlName;
    private String sqlType;
    private String name;
    private String type;
    private boolean pkey;
    private String remarks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPkey() {
        return pkey;
    }

    public void setPkey(boolean pkey) {
        this.pkey = pkey;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSqlName() {
        return sqlName;
    }

    public void setSqlName(String sqlName) {
        this.sqlName = sqlName;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    @Override
    public String toString() {
        return "EntityItem{" +
                "sqlName='" + sqlName + '\'' +
                ", sqlType='" + sqlType + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", pkey=" + pkey +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
