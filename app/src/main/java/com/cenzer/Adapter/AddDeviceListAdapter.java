package com.cenzer.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.CustomProgress.CustomDialog.AnimatedProgress;
import com.niftymodaldialogeffects.Effectstype;
import com.niftymodaldialogeffects.NiftyDialogBuilder;
import com.cenzer.Activities.AppHelper;
import com.cenzer.Constant.Constants;
import com.cenzer.CustomView.HeaderText;
import com.cenzer.CustomView.NormalText;
import com.cenzer.CustomView.WhiteUnderLineEdit;
import com.cenzer.DatabaseModule.DatabaseConstant;
import com.cenzer.EncodeDecodeModule.ByteQueue;
import com.cenzer.InterfaceModule.AdvertiseResultInterface;
import com.cenzer.InterfaceModule.ReceiverResultInterface;
import com.cenzer.PogoClasses.BeconDeviceClass;
import com.cenzer.PogoClasses.GroupDetailsClass;
import com.cenzer.R;
import com.cenzer.ServiceModule.ScannerTask;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cenzer.Activities.AppHelper.sqlHelper;
import static com.cenzer.DatabaseModule.DatabaseConstant.COLUMN_DEVICE_MASTER_STATUS;
import static com.cenzer.EncodeDecodeModule.TxMethodType.SELECT_MASTER_RESPONSE;


public class AddDeviceListAdapter extends BaseAdapter implements AdvertiseResultInterface, ReceiverResultInterface {
    Activity activity;
    ArrayList<BeconDeviceClass> arrayList;
    ArrayAdapter<GroupDetailsClass> adapter;
    ArrayList<GroupDetailsClass> groupDetailsClasses;
    ScannerTask scannerTask;
    AnimatedProgress animatedProgress;
    String TAG=this.getClass().getSimpleName();
    int selectedPosition=-1;

    public AddDeviceListAdapter(@NonNull Activity context) {
        activity = context;
        arrayList = new ArrayList<>();
        groupDetailsClasses=new ArrayList<>();
        scannerTask=new ScannerTask(activity,this);
        animatedProgress=new AnimatedProgress(activity);
        animatedProgress.setCancelable(false);

        adapter=new ArrayAdapter<GroupDetailsClass>(activity,R.layout.spinerlayout,groupDetailsClasses){
            public View getView(int position, View convertView, ViewGroup parent) {
                // Cast the spinner collapsed item (non-popup item) as a text view
                TextView tv = (TextView) super.getView(position, convertView, parent);

                // Set the text color of spinner item
                tv.setTextColor(Color.WHITE);
                tv.setText(groupDetailsClasses.get(position).getGroupName());
                // Return the view
                return tv;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent){
                // Cast the drop down items (popup items) as text view
                TextView tv = (TextView) super.getDropDownView(position,convertView,parent);

                // Set the text color of drop down items
                tv.setTextColor(Color.BLACK);
                tv.setText(groupDetailsClasses.get(position).getGroupName());

                /*// If this item is selected item
                if(position == mSelectedIndex){
                    // Set spinner selected popup item's text color
                    tv.setTextColor(Color.BLUE);
                }*/

                // Return the modified view
                return tv;
            }
        };


        /*adapter = ArrayAdapter.createFromResource(activity,
                R.array.add_spinner, R.layout.addspinerlayout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addDeviceSpinerAdapter=new AddDeviceSpinerAdapter(activity);*/

//        setArrayList();
        getAllGroups();
        if(AppHelper.IS_TESTING) {
            setArrayList();
        }
    }
    public void clearList()
    {
        if(this.arrayList==null)
            this.arrayList=new ArrayList<>();
        this.arrayList.clear();
        notifyDataSetChanged();

    }
    public void setArrayList(ArrayList<BeconDeviceClass> arrayList) {
//        if(this.arrayList==null)
//            this.arrayList=new ArrayList<>();
//        this.arrayList.clear();
        this.arrayList = arrayList;
        notifyDataSetChanged();

    }

    public void setArrayList()
    {
        for (int i=0;i<=20;i++)
        {
            BeconDeviceClass beconDeviceClass=new BeconDeviceClass();
            beconDeviceClass.setBeaconUID(i+10);
            beconDeviceClass.setDeviceUid((i+10)+"");
            beconDeviceClass.setDeriveType(0x01);
            arrayList.add(beconDeviceClass);
        }
        notifyDataSetChanged();
    }


    public void showDialog(int position) {
        selectedPosition=position;
        final Dialog dialog = new Dialog(activity);
        BeconDeviceClass beconDeviceClass=arrayList.get(position);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_device_dialog_layout);
        Spinner spinner=dialog.findViewById(R.id.add_device_group_list);
        HeaderText deviceUid=dialog.findViewById(R.id.add_device_uid);
        Button addDevice=dialog.findViewById(R.id.add_device_button);
        WhiteUnderLineEdit deviceName=dialog.findViewById(R.id.add_device_name);
        deviceUid.setText(beconDeviceClass.getDeviceUid());
        spinner.setAdapter(adapter);
//        GroupDetailsClass selected=(GroupDetailsClass) spinner.getSelectedItem();
        dialog.show();

        addDevice.setText(beconDeviceClass.isAdded()?"Added":"Add");

        addDevice.setOnClickListener(view -> {

            if (deviceName.getText().toString().length()<1)
            {
                deviceName.setError("Enter device name");
                return;
            }

            ContentValues contentValues=new ContentValues();
            contentValues.put(DatabaseConstant.COLUMN_DEVICE_UID,beconDeviceClass.getBeaconUID());
            contentValues.put(DatabaseConstant.COLUMN_DEVICE_NAME,deviceName.getText().toString());
            contentValues.put(DatabaseConstant.COLUMN_DERIVE_TYPE,beconDeviceClass.getDeriveType()==0? Constants.PWM :Constants.VCC);
            contentValues.put(DatabaseConstant.COLUMN_DEVICE_STATUS,beconDeviceClass.getDeriveType()==0? 1 :0);
            contentValues.put(DatabaseConstant.COLUMN_GROUP_ID,((GroupDetailsClass) spinner.getSelectedItem()).getGroupId());
//            Log.w("Selected group",((GroupDetailsClass) spinner.getSelectedItem()).getGroupId()+"");
            if(sqlHelper.insertData(DatabaseConstant.ADD_DEVICE_TABLE,contentValues)<0)
            {

                arrayList.get(position).setAdded(true);
                Toast.makeText(activity, "Device Already added.", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }else
            {
                arrayList.get(position).setAdded(true);
                arrayList.get(position).setDeviceName(deviceName.getText().toString());
                Toast.makeText(activity, "Device  added successfully.", Toast.LENGTH_SHORT).show();
                dialog.cancel();
//                NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(activity);
//                dialogBuilder
//                        .withTitle("Master Light")
//                        .withEffect(Effectstype.Shake)
//                        .withMessage("Set light '"+deviceName.getText().toString()+"' as master light")
//                        .withButton1Text("OK")
//                        .setButton1Click(v -> {
//
////                            Toast.makeText(activity, "This will be soon.", Toast.LENGTH_SHORT).show();
//                            selectedPosition=position;
//                            AdvertiseTask advertiseTask;
//                            ByteQueue byteQueue;
//                            byteQueue = new ByteQueue();
//                            byteQueue.push(RxMethodType.SELECT_MASTER);
//                            byteQueue.pushU4B(beconDeviceClass.getBeaconUID());
//                            byteQueue.push(0x00);
//                            advertiseTask = new AdvertiseTask(this, activity);
//                            advertiseTask.setByteQueue(byteQueue);
//                            advertiseTask.setSearchRequestCode(SELECT_MASTER_RESPONSE);
//                            advertiseTask.startAdvertising();
//                            dialogBuilder.dismiss();
//                        }) .withButton2Text("Cancel")
//                        .setButton2Click(v -> {
//                            dialogBuilder.dismiss();
//                        })
//                        .show();
//
            }
            notifyDataSetChanged();

        });
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public BeconDeviceClass getItem(int position) {
        if (arrayList.size() <= position)
            return null;
        return arrayList.get(position);
    }

    public void getAllGroups()
    {
        groupDetailsClasses.clear();
        GroupDetailsClass noGroupData = new GroupDetailsClass();
        noGroupData.setGroupId(0);
        noGroupData.setGroupName("No Group");
        noGroupData.setGroupStatus(true);
        groupDetailsClasses.add(noGroupData);
        Cursor cursor = sqlHelper.getAllGroup();
        if (cursor.moveToFirst())
        {
            do {
                GroupDetailsClass groupData = new GroupDetailsClass();
                groupData.setGroupId(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_GROUP_ID)));
                groupData.setGroupDimming(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_GROUP_PROGRESS)));
                groupData.setGroupName(cursor.getString(cursor.getColumnIndex(DatabaseConstant.COLUMN_GROUP_NAME)));
                groupData.setGroupStatus(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_GROUP_STATUS)) == 1);
                groupDetailsClasses.add(groupData);
                // do what ever you want here
            }
            while (cursor.moveToNext());
        }

//
        cursor.close();
        adapter.notifyDataSetChanged();
    }



    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        if (convertView == null) {
        convertView = LayoutInflater.from(activity).
                inflate(R.layout.device_list_item, parent, false);

//        }
        BeconDeviceClass beconDeviceClass=arrayList.get(position);
        ViewHolder viewHolder=new ViewHolder(convertView);
        viewHolder.addDevice.setText(beconDeviceClass.isAdded()?"Added":"Add");
        if (beconDeviceClass.isAdded()) {
            viewHolder.addDevice.setVisibility(View.GONE);
        } else {
            viewHolder.addDevice.setVisibility(View.VISIBLE);
        }
        viewHolder.review1.setBackground(activity.getResources().getDrawable(beconDeviceClass.getMasterStatus()==0?R.drawable.white_circle_border:R.drawable.yellow_circle));
        viewHolder.addDevice.setOnClickListener(view -> {
            if(beconDeviceClass.isAdded())
            {
                NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(activity);
                dialogBuilder
                        .withTitle("ADD DEVICE")
                        .withEffect(Effectstype.Slit)
                        .withMessage("Device is already added")
                        .withButton1Text("OK")
                        .setButton1Click(v -> {
                            dialogBuilder.dismiss();
                        })
                        .show();
                return;
            }
            showDialog(position);

        });
        viewHolder.addDeviceUid.setText(beconDeviceClass.isAdded()?beconDeviceClass.getDeviceName():beconDeviceClass.getDeviceUid());

        return convertView;
    }

    void showAlert(int position, String message, String title) {
        if (title.length() < 1)
            title = "Alert";
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setTitle(title);
        builder.setPositiveButton("Block", (dialog1, id) -> {
            // User clicked OK button
//            acceptRequest(2,position);
            dialog1.dismiss();

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onSuccess(String message) {
        animatedProgress.showProgress();
        Log.w(TAG,"Advertising start");
    }

    @Override
    public void onFailed(String errorMessage) {
        if(animatedProgress==null)
            return;
        Toast.makeText(activity, "Advertising failed.", Toast.LENGTH_SHORT).show();
        animatedProgress.hideProgress();
    }

    @Override
    public void onStop(String stopMessage, int resultCode) {
        scannerTask=new ScannerTask(activity,this);
        scannerTask.setRequestCode(resultCode);
        scannerTask.start();
        Log.w(TAG,"Advertising stop"+resultCode);
    }

    @Override
    public void onScanSuccess(int successCode, ByteQueue byteQueue) {
        if(animatedProgress==null)
            return;
        animatedProgress.hideProgress();
        ContentValues contentValues=new ContentValues();

        Log.w("BYTEQUESIZE",byteQueue.size()+",");
        Log.w("MethodType",(int)byteQueue.pop()+"");

        byte bytes1;
        String nodeUid;
        long deviceUid;
        int lightStatus;
        NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(activity);
        switch (successCode)
        {
            case SELECT_MASTER_RESPONSE:




                BeconDeviceClass beconDeviceClass=arrayList.get(selectedPosition);
                arrayList.get(selectedPosition).setMasterStatus(1);
                lightStatus=byteQueue.pop();

                contentValues.put(COLUMN_DEVICE_MASTER_STATUS, lightStatus==0?1:0);



                dialogBuilder
                        .withTitle("Master Status")
                        .withEffect(Effectstype.RotateBottom)
                        .withMessage("Light is set as master")
                        .withButton1Text("OK")
                        .setButton1Click(v -> {
                            dialogBuilder.dismiss();
                        })
                        .show();
                Log.w("DashGroup", AppHelper.sqlHelper.updateDevice(beconDeviceClass.getBeaconUID(), contentValues) +"");

                break;


        }

    }

    @Override
    public void onScanFailed(int errorCode) {
        if(animatedProgress==null)
            return;
        animatedProgress.hideProgress();
        NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(activity);
        dialogBuilder
                .withTitle("Timeout")
                .withEffect(Effectstype.Slit)
                .withMessage("Timeout,Please check your beacon is in range")
                .withButton1Text("OK")
                .setButton1Click(v -> {
                    dialogBuilder.dismiss();
                })
                .show();
    }


    static class ViewHolder {
        @BindView(R.id.review_1)
        ImageButton review1;
        @BindView(R.id.add_device)
        Button addDevice;
        @BindView(R.id.add_device_layout)
        RelativeLayout addDeviceLayout;
        @BindView(R.id.add_device_uid)
        NormalText addDeviceUid;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }}
