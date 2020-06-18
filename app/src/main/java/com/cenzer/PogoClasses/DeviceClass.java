package com.cenzer.PogoClasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.cenzer.Constant.Constants;

public class DeviceClass implements Parcelable {

    long DeviceUID=0;
    String deriveType= Constants.PWM;
    String deviceName="";
    int groupId=0;
    int masterStatus=0;
    int deviceDimming=0;
    boolean status=true;

    public String getDeriveType() {
        return deriveType;
    }

    public void setDeriveType(String deriveType) {
        this.deriveType = deriveType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setMasterStatus(int masterStatus) {
        this.masterStatus = masterStatus;
    }

    public int getMasterStatus() {
        return masterStatus;
    }


    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getDeviceDimming() {
        return deviceDimming;
    }

    public void setDeviceDimming(int deviceDimming) {
        this.deviceDimming = deviceDimming;
    }



    public long getDeviceUID() {
        return DeviceUID;
    }

    public void setDeviceUID(long deviceUID) {
        DeviceUID = deviceUID;
    }


    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(this.DeviceUID);
        dest.writeString(this.deviceName);
        dest.writeInt(this.groupId);
        dest.writeInt(this.deviceDimming);
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
    }

    public DeviceClass() {
    }

    protected DeviceClass(Parcel in) {

        this.DeviceUID = in.readLong();
        this.deviceName = in.readString();
        this.groupId = in.readInt();
        this.deviceDimming = in.readInt();
        this.status = in.readByte() != 0;
    }

    public static final Parcelable.Creator<DeviceClass> CREATOR = new Parcelable.Creator<DeviceClass>() {
        @Override
        public DeviceClass createFromParcel(Parcel source) {
            return new DeviceClass(source);
        }

        @Override
        public DeviceClass[] newArray(int size) {
            return new DeviceClass[size];
        }
    };
}
