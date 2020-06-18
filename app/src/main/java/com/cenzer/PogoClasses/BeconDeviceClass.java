package com.cenzer.PogoClasses;

public class BeconDeviceClass {
    String deviceUid="";
    String deviceName="";

    long beaconUID=0;
    int deriveType=0;
    int masterStatus=0;
    boolean isAdded=false;

    public void setDeriveType(int deriveType) {
        this.deriveType = deriveType;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public int getDeriveType() {
        return deriveType;
    }

    public void setMasterStatus(int masterStatus) {
        this.masterStatus = masterStatus;
    }

    public int getMasterStatus() {
        return masterStatus;
    }

    public void setBeaconUID(long beaconUID) {
        this.beaconUID = beaconUID;
    }

    public long getBeaconUID() {
        return beaconUID;
    }

    public String getDeviceUid() {
        return deviceUid;
    }

    public void setDeviceUid(String deviceUid) {
        this.deviceUid = deviceUid;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

}
