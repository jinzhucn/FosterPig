package com.minlu.fosterpig.bean;

/**
 * Created by user on 2016/11/30.
 */
public class FacilityDetail {

    private int dataValue;
    private int facilityType;
    private int isWarn;
    private String siteName;
    private String areaName;

    public FacilityDetail(int dataValue, int facilityType, String siteName, String areaName, int isWarn) {
        this.dataValue = dataValue;
        this.facilityType = facilityType;
        this.siteName = siteName;
        this.areaName = areaName;
        this.isWarn = isWarn;
    }

    public int getDataValue() {
        return dataValue;
    }

    public void setDataValue(int dataValue) {
        this.dataValue = dataValue;
    }

    public int getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(int facilityType) {
        this.facilityType = facilityType;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public int getIsWarn() {
        return isWarn;
    }

    public void setIsWarn(int isWarn) {
        this.isWarn = isWarn;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
