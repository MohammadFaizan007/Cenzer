package com.cenzer;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.niftymodaldialogeffects.Effectstype;
import com.niftymodaldialogeffects.NiftyDialogBuilder;
import com.cenzer.Constant.Constants;
import com.cenzer.InterfaceModule.AdvertiseResultInterface;
import com.cenzer.ReceiverModule.BLEBroadcastReceiver;
import com.cenzer.ServiceModule.AdvertiseTask;
import com.cenzer.ServiceModule.ScannerTask;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements AdvertiseResultInterface, BeaconConsumer, MonitorNotifier {
    @BindView(R.id.main_toolbar)
    Toolbar mainToolbar;
    public  BeaconManager beaconManager;
    ScannerTask scannerTask;
    BLEBroadcastReceiver bleBroadcastReceiver;
    private BackgroundPowerSaver backgroundPowerSaver;
    AdvertiseTask advertiseTask;
    String TAG="MainActivity";
    protected LocationManager locationManager;
    NiftyDialogBuilder dialogBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mainToolbar);
        setTitle("");
         dialogBuilder = NiftyDialogBuilder.getInstance(this);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            Log.d(TAG, "SDK "+Build.VERSION.SDK_INT+" App Permissions:");
            if (info.requestedPermissions != null) {
                for (String p : info.requestedPermissions) {
                    int grantResult = this.checkPermission(p, android.os.Process.myPid(), android.os.Process.myUid());
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, p+" PERMISSION_GRANTED");
                    }
                    else {
                        Log.d(TAG, p+" PERMISSION_DENIED: "+grantResult);
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Cannot get permissions due to error", e);
        }

        enableBT();
        backgroundPowerSaver = new BackgroundPowerSaver(this);
//        initiateBeaconService();
        bleBroadcastReceiver=new BLEBroadcastReceiver();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bleBroadcastReceiver, filter);
    }
    @Override
    public void onBeaconServiceConnect() {
        Log.w("AppHelper","onBeaconServiceConnect");

    }


    @OnClick({R.id.my_network_layout, R.id.smart_device_layout, R.id.dashboard_layout, R.id.group_layout, R.id.demo_layout})
    public void onViewClicked(View view) {

        Intent intent = new Intent(this, HelperActivity.class);
        switch (view.getId()) {
            case R.id.my_network_layout:
//                scannerTask.stop();
                intent.putExtra(Constants.MAIN_KEY, Constants.MY_NETWORK_CODE);
                break;
            case R.id.smart_device_layout:
                intent.putExtra(Constants.MAIN_KEY, Constants.SMART_DEVICE_CODE);
                break;
            case R.id.dashboard_layout:
                intent.putExtra(Constants.MAIN_KEY, Constants.DASHBOARD_CODE);
                break;
            case R.id.group_layout:
                intent.putExtra(Constants.MAIN_KEY, Constants.GROUP_CODE);
                break;
            case R.id.demo_layout:
                intent.putExtra(Constants.MAIN_KEY, Constants.DEMO_CODE);
                break;
        }
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        enableBT();
        isLocationEnable();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (locationManager == null) {
            locationManager = (LocationManager)this .getSystemService(Context.LOCATION_SERVICE);
        }


//        loginUser();
    }
    public void isLocationEnable() {


//            Log.e(TAG, "getLocation");
            initializeLocationManager();


            // getting GPS status
           boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
        boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled)
            {
                Log.e(TAG, "cannot get current location");

                dialogBuilder
                        .withTitle("Enable Location")
                        .withEffect(Effectstype.Newspager)
                        .withMessage("Your location is disable.Please enable it to better scanning.")
                        .withButton1Text("OK")
                        .setButton1Click(v -> {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            dialogBuilder.dismiss();
                        })
                        .show();
                // no network provider is enabled
            }
                // First get location from Network Provider

    }

//    export ANDROID_HOME=/home/faizan/Android/Sdk/android-sdk

    @Override
    protected void onDestroy() {
        disableBT();
        if (bleBroadcastReceiver!=null)
        {
            unregisterReceiver(bleBroadcastReceiver);
        }
//        stopService();
//        beaconManager.unbind(this);
        super.onDestroy();
    }
    public void disableBT()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();
        }
    }
    public void enableBT(){

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
        }
    }

    @Override
    public void onSuccess(String message) {
        Log.d("MainActivity",message+"");
    }



    @Override
    public void onFailed(String errorMessage) {
        Log.d("MainActivity onScanF",errorMessage+"");
    }

    @Override
    public void onStop(String stopMessage, int resultCode) {
        Log.w("MainActivity onStop",stopMessage+"");
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.w("MainActivity ","didEnterRegion");

    }

    @Override
    public void didExitRegion(Region region) {
        Log.w("MainActivity ","didExitRegion");
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        Log.w("MainActivity ","didDetermineStateForRegion");
    }
}
