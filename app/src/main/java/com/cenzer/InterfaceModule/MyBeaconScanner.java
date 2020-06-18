package com.cenzer.InterfaceModule;

import com.cenzer.PogoClasses.BeconDeviceClass;

import java.util.ArrayList;

public interface MyBeaconScanner
{
    void onBeaconFound(ArrayList<BeconDeviceClass> byteQueue);
    void noBeaconFound();
}
