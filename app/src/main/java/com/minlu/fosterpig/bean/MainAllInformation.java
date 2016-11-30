package com.minlu.fosterpig.bean;

/**
 * Created by user on 2016/11/30.
 */
public class MainAllInformation {

    private String pigHome;
    private String siteNumber;
    private int collectionPoint;
    private int collectionNumber;
    private boolean isWarn;

    public MainAllInformation(String pigHome, String siteNumber, int collectionPoint, int collectionNumber, boolean isWarn) {
        this.pigHome = pigHome;
        this.siteNumber = siteNumber;
        this.collectionPoint = collectionPoint;
        this.collectionNumber = collectionNumber;
        this.isWarn = isWarn;
    }
    public String getPigHome() {
        return pigHome;
    }

    public void setPigHome(String pigHome) {
        this.pigHome = pigHome;
    }

    public String getSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(String siteNumber) {
        this.siteNumber = siteNumber;
    }

    public int getCollectionPoint() {
        return collectionPoint;
    }

    public void setCollectionPoint(int collectionPoint) {
        this.collectionPoint = collectionPoint;
    }

    public int getCollectionNumber() {
        return collectionNumber;
    }

    public void setCollectionNumber(int collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    public boolean isWarn() {
        return isWarn;
    }

    public void setWarn(boolean warn) {
        isWarn = warn;
    }
}
