package com.cenzer.Fragments;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.CustomProgress.CustomDialog.AnimatedProgress;
import com.niftymodaldialogeffects.Effectstype;
import com.niftymodaldialogeffects.NiftyDialogBuilder;
import com.cenzer.Adapter.CreateGroupAdapter;
import com.cenzer.DatabaseModule.DatabaseConstant;
import com.cenzer.EncodeDecodeModule.ByteQueue;
import com.cenzer.EncodeDecodeModule.RxMethodType;
import com.cenzer.EncodeDecodeModule.TxMethodType;
import com.cenzer.InterfaceModule.AdvertiseResultInterface;
import com.cenzer.InterfaceModule.ReceiverResultInterface;
import com.cenzer.PogoClasses.DeviceClass;
import com.cenzer.R;
import com.cenzer.ServiceModule.AdvertiseTask;
import com.cenzer.ServiceModule.ScannerTask;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.cenzer.Activities.AppHelper.sqlHelper;
import static com.cenzer.EncodeDecodeModule.TxMethodType.ADD_GROUP_RESPONSE;
import static com.cenzer.EncodeDecodeModule.TxMethodType.LIGHT_LEVEL_COMMAND_RESPONSE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateGroup extends Fragment implements  AdvertiseResultInterface, ReceiverResultInterface {


    @BindView(R.id.create_group_device_list)
    ListView createGroupDeviceList;
    Unbinder unbinder;
    Activity activity;
    long groupId=0;
    long deviceUid=0;
    ArrayList<DeviceClass> deviceList;
    CreateGroupAdapter createGroupAdapter;
    @BindView(R.id.group_save)
    Button groupSave;
    @BindView(R.id.create_group_name)
    EditText groupName;

    String TAG=this.getClass().getSimpleName();
    ScannerTask scannerTask;
    AnimatedProgress animatedProgress;

    public CreateGroup() {
        deviceList = new ArrayList<>();
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();
        deviceList = new ArrayList<>();
        animatedProgress=new AnimatedProgress(activity);
        createGroupAdapter = new CreateGroupAdapter(activity);
        createGroupDeviceList.setAdapter(createGroupAdapter);
        scannerTask=new ScannerTask(activity,this);
//        getDevice();
        return view;
    }

    public void getDevice()
    {
        Cursor cursor = sqlHelper.getNonGroupDevice(DatabaseConstant.ADD_DEVICE_TABLE);
        if (cursor.moveToFirst())
        {
            do {
                DeviceClass deviceClass = new DeviceClass();
                deviceClass.setDeviceName(cursor.getString(cursor.getColumnIndex(DatabaseConstant.COLUMN_DEVICE_NAME)));
                deviceClass.setDeviceUID(cursor.getLong(cursor.getColumnIndex(DatabaseConstant.COLUMN_DEVICE_UID)));
                deviceClass.setDeviceDimming(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_DEVICE_PROGRESS)));
                deviceClass.setGroupId(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_GROUP_ID)));
                deviceClass.setStatus(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_DEVICE_STATUS)) == 1);
                deviceList.add(deviceClass);
                // do what ever you want here
            } while (cursor.moveToNext());
        }
        createGroupAdapter.setList(deviceList);
        cursor.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.group_save)
    public void onViewClicked()
    {
        if (groupName.getText().toString().trim().length()<1)
        {
            groupName.setError("Group name is required.");
            return;
        }

//        if (createGroupAdapter.getSelectedLight().size()<1)
//        {
//            if (activity instanceof HelperActivity)
//            {
//                ((HelperActivity)activity).showAlert("Create Group","Minimum one selection is required.");
//
//            }
//            return;
//        }
        ContentValues contentValues=new ContentValues();
        contentValues.put(DatabaseConstant.COLUMN_GROUP_NAME,groupName.getText().toString()+"");
        long groupId=sqlHelper.insertData(DatabaseConstant.GROUP_TABLE_NAME,contentValues);
        this.groupId=groupId;
        Log.w("CreateGroup",groupId+"");
        if(groupId>0)
        {
            activity.onBackPressed();

//          setGroupDevice(groupId);
        }
        else
        {
            Toast.makeText(activity, "cannot create group.", Toast.LENGTH_SHORT).show();
        }
    }
    //    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setGroupDevice(long groupId)
    {
        ArrayList<String> selectedLights=createGroupAdapter.getSelectedLight();
        if (selectedLights.size()<1)
        {
            activity.onBackPressed();
            return;
        }
        try {
            int i=0;
            for (String s:selectedLights)
            {
                DeviceClass deviceClass=deviceList.get(Integer.parseInt(s));

                AdvertiseTask advertiseTask;
                ByteQueue byteQueue=new ByteQueue();
                byteQueue.push(RxMethodType.ADD_GROUP);
                Log.w("GroupUid",deviceClass.getDeviceUID()+"");
                byteQueue.pushU4B(deviceClass.getDeviceUID());
                byteQueue.push(groupId);

                deviceUid=deviceClass.getDeviceUID();
                advertiseTask=new AdvertiseTask(this,activity);
                advertiseTask.setByteQueue(byteQueue);
                advertiseTask.setSearchRequestCode(TxMethodType.ADD_GROUP_RESPONSE);
                advertiseTask.startAdvertising();


//            i++;
//            if (i==selectedLights.size())
//            {
////                animatedProgress.showProgress();
//
//
////                scannerTask.setRequestCode(TxMethodType.ADD_GROUP_RESPONSE);
//
//
//                Toast.makeText(activity, "Created Group successfully.", Toast.LENGTH_SHORT).show();
//                activity.onBackPressed();
//            }
            }

        }catch (Exception e)
        {
            Log.w("Create Group",e.getMessage());
        }
    }

    @Override
    public void onScanSuccess(int successCode, ByteQueue byteQueue)
    {

        animatedProgress.hideProgress();
        Log.w("MethodType12",(int)byteQueue.pop()+","+successCode);


//        ArrayUtilities.reverse(bytes1);
//        String nodeUid=bytesToHex(bytes1);
        int status=byteQueue.pop();
//                                String s = "4d0d08ada45f9dde1e99cad9";
        Log.w("Status",status+"");
        switch (successCode)
        {
            case ADD_GROUP_RESPONSE:
                if(status==0x00)
                {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DatabaseConstant.COLUMN_GROUP_ID, (int) groupId);
                    Log.w("Group123",sqlHelper.updateDevice(this.deviceUid, contentValues)+"");
                    activity.onBackPressed();
                }
                else
                    Log.w("Group","FAiled");
                break;

            case LIGHT_LEVEL_COMMAND_RESPONSE:
                break;
        }
    }

    @Override
    public void onScanFailed(int errorCode) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(DatabaseConstant.COLUMN_GROUP_ID, (int) groupId);
//        Log.w("Group123",sqlHelper.updateDevice(this.deviceUid, contentValues)+"");

        NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(activity);
        dialogBuilder
                .withTitle("Timeout")
                .withEffect(Effectstype.Newspager)
                .withMessage("Timeout,Please check your beacon is in range")
                .withButton1Text("OK")
                .setButton1Click(v -> {
                    dialogBuilder.dismiss();
                })
                .show();
        animatedProgress.hideProgress();
        Log.w(TAG,"onScanFailed "+errorCode);

    }

    @Override
    public void onSuccess(String message) {
        animatedProgress.showProgress();
    }

    @Override
    public void onFailed(String errorMessage) {

    }

    @Override
    public void onStop(String stopMessage, int resultCode) {

        scannerTask.setRequestCode(resultCode);
        scannerTask.start();
    }
}
