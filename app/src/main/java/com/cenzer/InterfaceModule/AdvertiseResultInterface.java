package com.cenzer.InterfaceModule;

public interface AdvertiseResultInterface
{

    void onSuccess(String message);
    void onFailed(String errorMessage);
    void onStop(String stopMessage,int resultCode);

}
