package com.cenzer.Fragments;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.CustomProgress.CustomDialog.AnimatedProgress;
import com.cenzer.Activities.AppHelper;
import com.cenzer.Adapter.AddDeviceListAdapter;
import com.cenzer.EncodeDecodeModule.ByteQueue;
import com.cenzer.InterfaceModule.AdvertiseResultInterface;
import com.cenzer.InterfaceModule.MyBeaconScanner;
import com.cenzer.PogoClasses.BeconDeviceClass;
import com.cenzer.R;
import com.cenzer.ServiceModule.AdvertiseTask;
import com.cenzer.ServiceModule.ScanningBeacon;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.cenzer.EncodeDecodeModule.RxMethodType.LIGHT_INFO;

public class AddDeviceFragment extends Fragment implements MyBeaconScanner, AdvertiseResultInterface {


    @BindView(R.id.device_list)
    ListView deviceList;
    Unbinder unbinder;
    AddDeviceListAdapter addDeviceListAdapter;
    Activity activity;
    ArrayAdapter<CharSequence> adapter;
    int movement=150;
    ScanningBeacon scanningBeacon;
    boolean isAdvertisingFinished=false;
    AdvertiseTask advertiseTask;
    AnimatedProgress animatedProgress;
    String TAG=this.getClass().getSimpleName();

    Handler handler ;
    private Runnable runnable= () -> {
        if(animatedProgress!=null)
        {
            animatedProgress.hideProgress();
        }

    };
    public AddDeviceFragment() {
        // Required empty public constructor
    }

    private void handlerProgressar()
    {
        animatedProgress.showProgress();
        handler = new Handler();
        handler.postDelayed(runnable,30*1000);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_device, container, false);
        unbinder = ButterKnife.bind(this, view);
        activity=getActivity();
        addDeviceListAdapter=new AddDeviceListAdapter(activity);
        deviceList.setAdapter(addDeviceListAdapter);
        scanningBeacon=new ScanningBeacon(activity);
        scanningBeacon.setMyBeaconScanner(this);

        animatedProgress=new AnimatedProgress(activity);
        animatedProgress.setCancelable(false);


        ByteQueue byteQueue=new ByteQueue();
        byteQueue.push(LIGHT_INFO);   //// Light Level Command method type
        byteQueue.pushU4B(0x00);   ////deviceDetail.getGroupId()   node id;
        byteQueue.push(0x00);    ////0x00-0x64
//            scannerTask.setRequestCode(TxMethodType.LIGHT_LEVEL_COMMAND_RESPONSE);
        advertiseTask=new AdvertiseTask(this,activity,10*1000);
        animatedProgress.setText("Advertising");
        advertiseTask.setByteQueue(byteQueue);
//        advertiseTask.setAdvertiseTimeout(10*1000);
        advertiseTask.setSearchRequestCode(LIGHT_INFO);
        advertiseTask.startAdvertising();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isAdvertisingFinished)
        {
            animatedProgress.setText("Scanning beacon");
            scanningBeacon.start();
        }
    }

    public void reStartScanning()
    {
        if(isAdvertisingFinished)
        {
            animatedProgress.setText("Scanning beacon");
            scanningBeacon.start();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        scanningBeacon.stop();
        if(handler!=null)
            handler.removeCallbacks(runnable);
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
//        if(isAdvertisingFinished)
//            scanningBeacon.start();
    }

    @Override
    public void onStop() {
        scanningBeacon.stop();
        super.onStop();
    }

    @Override
    public void onBeaconFound(ArrayList<BeconDeviceClass> beaconList) {
        if(addDeviceListAdapter==null)
            addDeviceListAdapter=new AddDeviceListAdapter(activity);
        addDeviceListAdapter.setArrayList(beaconList);
    }

    @Override
    public void noBeaconFound()
    {
        Log.w("AddDeviceFragment","No Beacon founded");
        if(!AppHelper.IS_TESTING)
            addDeviceListAdapter.clearList();
//        Toast.makeText(activity, "no beacon found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(String message) {
        handlerProgressar();
        Log.w(TAG,"Advertising start");
//        isAdvertisingFinished=false;
//         scanningBeacon.stop();
    }

    @Override
    public void onFailed(String errorMessage) {
//        if(animatedProgress!=null)
//            animatedProgress.hideProgress();
//        Toast.makeText(activity, "Advertising failed.", Toast.LENGTH_SHORT).show();

        isAdvertisingFinished=true;
        scanningBeacon.start();
        animatedProgress.setText("Scanning beacon");
    }

    @Override
    public void onStop(String stopMessage, int resultCode) {
//        if(animatedProgress!=null)
//            animatedProgress.hideProgress();
        isAdvertisingFinished=true;
        animatedProgress.setText("Scanning beacon");
        scanningBeacon.start();
    }
}
