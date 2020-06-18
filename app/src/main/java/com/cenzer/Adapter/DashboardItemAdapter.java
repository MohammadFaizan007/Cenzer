package com.cenzer.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.CustomProgress.CustomDialog.AnimatedProgress;
import com.niftymodaldialogeffects.Effectstype;
import com.niftymodaldialogeffects.NiftyDialogBuilder;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;
import com.cenzer.Activities.AppHelper;
import com.cenzer.Constant.Constants;
import com.cenzer.CustomView.HeaderText;
import com.cenzer.CustomView.NormalText;
import com.cenzer.DatabaseModule.DatabaseConstant;
import com.cenzer.EncodeDecodeModule.ByteQueue;
import com.cenzer.HelperActivity;
import com.cenzer.InterfaceModule.AdvertiseResultInterface;
import com.cenzer.InterfaceModule.ReceiverResultInterface;
import com.cenzer.PogoClasses.DeviceClass;
import com.cenzer.PogoClasses.GroupDetailsClass;
import com.cenzer.R;
import com.cenzer.ServiceModule.AdvertiseTask;
import com.cenzer.ServiceModule.ScannerTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.Gravity.CENTER;
import static com.cenzer.Activities.AppHelper.sqlHelper;
import static com.cenzer.DatabaseModule.DatabaseConstant.COLUMN_DEVICE_PROGRESS;
import static com.cenzer.DatabaseModule.DatabaseConstant.COLUMN_DEVICE_STATUS;
import static com.cenzer.DatabaseModule.DatabaseConstant.COLUMN_GROUP_PROGRESS;
import static com.cenzer.DatabaseModule.DatabaseConstant.COLUMN_GROUP_STATUS;
import static com.cenzer.EncodeDecodeModule.RxMethodType.GROUP_STATE_COMMAND;
import static com.cenzer.EncodeDecodeModule.RxMethodType.LIGHT_LEVEL_GROUP_COMMAND;
import static com.cenzer.EncodeDecodeModule.TxMethodType.GROUP_STATE_COMMAND_RESPONSE;
import static com.cenzer.EncodeDecodeModule.TxMethodType.LIGHT_LEVEL_GROUP_COMMAND_RESPONSE;


public class DashboardItemAdapter extends BaseAdapter implements AdvertiseResultInterface,ReceiverResultInterface {
    Activity activity;
    ArrayList<GroupDetailsClass> arrayList;
    int requestCode;
    int requestCode2=0;
    ScannerTask scannerTask;
    AnimatedProgress animatedProgress;
    int seekBarProgress=0;
    int selectedPosition=0;

    String TAG=this.getClass().getSimpleName();
    public DashboardItemAdapter(@NonNull Activity context) {
        activity = context;
        arrayList = new ArrayList<>();
        scannerTask=new ScannerTask(context,this);
        animatedProgress=new AnimatedProgress(activity);

    }

    public void setList(List<GroupDetailsClass> arrayList1) {
        arrayList.clear();

        arrayList.addAll(arrayList1);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public GroupDetailsClass getItem(int position) {
        if (arrayList.size() <= position)
            return null;
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    void showDialog( int index)
    {
        final Dialog dialog = new Dialog(activity);
        GroupDetailsClass deviceDetail=arrayList.get(index);

        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.90);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setGravity(CENTER);
        dialog.setContentView(R.layout.customize_group);
        NormalText deviceName=dialog.findViewById(R.id.customize_group_name);
        HeaderText levelPercentage=dialog.findViewById(R.id.level_percentage);
        SeekBar seekBar=dialog.findViewById(R.id.customizeGroupSeekBar);
        Button button=dialog.findViewById(R.id.customiseGroupSave);
        seekBarProgress=deviceDetail.getGroupDimming();
        seekBar.setProgress(deviceDetail.getGroupDimming());
        levelPercentage.setText(seekBarProgress+" %");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                seekBarProgress =i;
                levelPercentage.setText(seekBarProgress+" %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                selectedPosition=index;
                String hex = Integer.toHexString(seekBarProgress);
                Log.e(TAG,hex+" "+String.format("%02X", seekBarProgress));
                AdvertiseTask advertiseTask;
                requestCode=LIGHT_LEVEL_GROUP_COMMAND;
                ByteQueue byteQueue=new ByteQueue();
                byteQueue.push(requestCode);   //// Light Level Command method type
                byteQueue.push(arrayList.get(index).getGroupId());   ////deviceDetail.getGroupId()   node id;
                byteQueue.push(seekBarProgress);    ////0x00-0x64
                byteQueue.pushU3B(0x00);
                advertiseTask=new AdvertiseTask(DashboardItemAdapter.this,activity);
                advertiseTask.setByteQueue(byteQueue);
                advertiseTask.setSearchRequestCode(LIGHT_LEVEL_GROUP_COMMAND_RESPONSE);
                advertiseTask.startAdvertising();

//                dialog.dismiss();

            }
        });

//        button.setOnClickListener(view ->
//        {
//            Log.w(TAG,seekBarProgress+"");
//            selectedPosition=index;
////            arrayList.get(index).setGroupDimming(seekBarProgress);
//
//            String hex = Integer.toHexString(seekBarProgress);
//            Log.w(TAG,hex+" "+String.format("%02X", seekBarProgress));
//            AdvertiseTask advertiseTask;
//            requestCode=LIGHT_LEVEL_GROUP_COMMAND;
//            ByteQueue byteQueue=new ByteQueue();
//            byteQueue.push(requestCode);   //// Light Level Command method type
//            byteQueue.push(arrayList.get(index).getGroupId());   ////deviceDetail.getGroupId()   node id;
//            byteQueue.push(seekBarProgress);    ////0x00-0x64
//            byteQueue.pushU3B(0x00);
//            advertiseTask=new AdvertiseTask(this,activity);
//            advertiseTask.setByteQueue(byteQueue);
//            advertiseTask.setSearchRequestCode(LIGHT_LEVEL_GROUP_COMMAND_RESPONSE);
//            advertiseTask.startAdvertising();
//
//            dialog.dismiss();
//        });

        deviceName.setText(deviceDetail.getGroupName());

        dialog.show();

    }

//    /home/faizan/Android/Sdk

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(activity).
                inflate(R.layout.dashboard_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(convertView);
        GroupDetailsClass deviceClass=arrayList.get(position);
        viewHolder.dashboardDeviceName.setText(deviceClass.getGroupName());
        viewHolder.dashboardCustomize.setOnClickListener(view -> showDialog(position));
        viewHolder.lightDetails.setOnClickListener(v -> {
            Log.w("GroupDimming",deviceClass.getGroupDimming()+"");
            Intent intent = new Intent(activity, HelperActivity.class);
            intent.putExtra(Constants.MAIN_KEY, Constants.EDIT_GROUP);
            intent.putExtra(Constants.GROUP_DETAIL_KEY,arrayList.get(position));
            activity.startActivity(intent);

        });

        seekBarProgress=deviceClass.getGroupDimming();
        viewHolder.statusSwitch.setChecked(deviceClass.getGroupStatus());
        viewHolder.statusSwitch.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb)
            {
                boolean switchStatus= state != State.LEFT;
                if(deviceClass.getGroupStatus()==switchStatus)
                {
//                    Log.w("Advertise","state is same");
                    return;
                }

                AdvertiseTask advertiseTask;
                selectedPosition=position;
                ByteQueue byteQueue=new ByteQueue();
                byteQueue.push(GROUP_STATE_COMMAND);       ////State Group Command method type
                byteQueue.push(deviceClass.getGroupId( ));
                Log.w("DashboardItemAdapter",state+"");
                switch (state)
                {
                    case LEFT:
                        //// remove group method type
//                        byteQueue.pushS4B(12);

                        byteQueue.push(0x00);   //0x00 – OFF    0x01 – ON
//                        arrayList.get(position).setGroupStatus(false);
                        Log.w("DashboardItem","LEFT");

                        break;
                    case RIGHT:
                        Log.w("DashboardItem","RIGHT");
                        byteQueue.push(0x01 );   //0x00 – OFF    0x01 – ON
//                        arrayList.get(position).setGroupStatus(true);
                        break;
                    case LEFT_TO_RIGHT:

//                        Log.w("DashboardItem","LEFT_TO_RIGHT");
                        return;

                    case RIGHT_TO_LEFT:
//                        Log.w("DashboardItem","RIGHT_TO_LEFT");
                        return;

                }
                byteQueue.pushU3B(0x00);
                advertiseTask=new AdvertiseTask(DashboardItemAdapter.this,activity);
                advertiseTask.setByteQueue(byteQueue);
                advertiseTask.setSearchRequestCode(GROUP_STATE_COMMAND_RESPONSE);
                advertiseTask.startAdvertising();

            }
        });
//        Picasso.with(activity).load(IMAGE_URL + friendsDetails.getUserImage()).placeholder(R.drawable.ic_user_male_icon_2).error(R.drawable.ic_user_male_icon_2).into(viewHolder.friendsProfile);

        return convertView;
    }

//    public void showDialog(int index)
//    {
//        final Dialog dialog = new Dialog(activity);
//        GroupDetailsClass groupDetails =arrayList.get(index);
//        final int[] seekBarProgress = {groupDetails.getGroupDimming()};
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.customize_group);
//        LinearLayout linearLayout=dialog.findViewById(R.id.group_device_list);
//        NormalText groupName=dialog.findViewById(R.id.customize_group_name);
//        SeekBar seekBar=dialog.findViewById(R.id.customizeGroupSeekBar);
//        Button button=dialog.findViewById(R.id.customiseGroupSave);
//
//        seekBar.setProgress(groupDetails.getGroupDimming());
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
//            {
//
//                seekBarProgress[0] =i;
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//        button.setOnClickListener(view -> {
//            Log.w("GroupAdapter",seekBarProgress[0]+"");
//            ContentValues contentValues=new ContentValues();
//            arrayList.get(index).setGroupDimming(seekBarProgress[0]);
//            contentValues.put(COLUMN_GROUP_PROGRESS,seekBarProgress[0]);
//            String hex = Integer.toHexString(seekBarProgress[0]);
//            Log.w("GroupHex",hex+" "+String.format("%02X", seekBarProgress[0]));
//            AdvertiseTask advertiseTask;
//            ByteQueue byteQueue=new ByteQueue();
//            byteQueue.push(0x0b);   //// Light Level Group Command method type
//            byteQueue.push(groupDetails.getGroupId());
//            byteQueue.push(hex);    ////0x00-0x64
//            advertiseTask=new AdvertiseTask(activity);
//            advertiseTask.setByteQueue(byteQueue);
//            advertiseTask.startAdvertising();
//
//            Log.w("GroupAdapter",AppHelper.sqlHelper.updateGroup(groupDetails.getGroupId(),contentValues)+"");
//            dialog.dismiss();
//        });
//        groupName.setText(groupDetails.getGroupName());
//        for(DeviceClass deviceClass:getGroupLight(groupDetails.getGroupId())) {
//            View layout2 = LayoutInflater.from(activity).inflate(R.layout.group_device_text, null, false);
//
//            NormalText textView =  layout2.findViewById(R.id.custom_device_text);
//            textView.setText(deviceClass.getDeviceName());
//            linearLayout.addView(layout2);
//        }
//        dialog.show();
//
//    }

    public ArrayList<DeviceClass> getGroupLight(int groupId)
    {
        ArrayList<DeviceClass> deviceClasses=new ArrayList<>();
        Cursor cursor = sqlHelper.getLightInGroup(groupId);
        if (cursor.moveToFirst()) {
            do {
                DeviceClass deviceClass = new DeviceClass();
                deviceClass.setDeviceName(cursor.getString(cursor.getColumnIndex(DatabaseConstant.COLUMN_DEVICE_NAME)));
                deviceClass.setDeviceUID(cursor.getLong(cursor.getColumnIndex(DatabaseConstant.COLUMN_DEVICE_UID)));
                deviceClass.setDeriveType(cursor.getString(cursor.getColumnIndex(DatabaseConstant.COLUMN_DERIVE_TYPE)));
                deviceClass.setDeviceDimming(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_DEVICE_PROGRESS)));
                deviceClass.setGroupId(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_GROUP_ID)));
                deviceClass.setStatus(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_DEVICE_STATUS))==1);
                // do what ever you want here
            } while (cursor.moveToNext());
        }

        cursor.close();
//        setList(arrayList);
        return deviceClasses;
    }

    @Override
    public void onScanSuccess(int successCode, ByteQueue byteQueue)
    {
//        animatedProgress.hideProgress();
        ContentValues contentValues=new ContentValues();
        ContentValues deviceContentValue = new ContentValues();
        Log.w("MethodType",(int)byteQueue.pop()+"");


        switch (successCode)
        {
            case GROUP_STATE_COMMAND_RESPONSE:
                int groupId=byteQueue.pop();
                Log.w("ScanningBeacon",groupId+"");
                int status=byteQueue.pop();
//                                String s = "4d0d08ada45f9dde1e99cad9";
                Log.w("Scann",","+status);
                if(status==0) {
                    boolean groupStatus = !arrayList.get(selectedPosition).getGroupStatus();

                    contentValues.put(COLUMN_GROUP_STATUS, groupStatus);
                    contentValues.put(COLUMN_GROUP_PROGRESS, groupStatus ? 100 : 0);

                    deviceContentValue.put(COLUMN_DEVICE_STATUS, groupStatus);
                    deviceContentValue.put(COLUMN_DEVICE_PROGRESS, groupStatus ? 100 : 0);

                    arrayList.get(selectedPosition).setGroupStatus(groupStatus);
                    arrayList.get(selectedPosition).setGroupDimming(groupStatus ? 100 : 0);
                    Log.w("DashGroup", AppHelper.sqlHelper.updateGroup(arrayList.get(selectedPosition).getGroupId(), contentValues) + "");
                    Log.w("DashGroup12", AppHelper.sqlHelper.updateGroupDevice(arrayList.get(selectedPosition).getGroupId(), deviceContentValue) + "");
                }
                else {
//                    this.groupStatus.setChecked(groupDetailsClass.getGroupStatus());
                    notifyDataSetChanged();
                }
                break;

            case LIGHT_LEVEL_GROUP_COMMAND_RESPONSE:
                int lightStatus = byteQueue.pop();
                if(lightStatus==0) {
                    contentValues.put(COLUMN_GROUP_PROGRESS, seekBarProgress);
                    contentValues.put(COLUMN_GROUP_STATUS, 1);

                    deviceContentValue.put(COLUMN_DEVICE_STATUS, 1);
                    deviceContentValue.put(COLUMN_DEVICE_PROGRESS, seekBarProgress);
                    arrayList.get(selectedPosition).setGroupDimming(seekBarProgress);
                    arrayList.get(selectedPosition).setGroupStatus(true);
                    Log.w("DashGroup", AppHelper.sqlHelper.updateGroup(arrayList.get(selectedPosition).getGroupId(), contentValues) + "");
                    Log.w("DashGroup12", AppHelper.sqlHelper.updateGroupDevice(arrayList.get(selectedPosition).getGroupId(), deviceContentValue) + "");
                }
                notifyDataSetChanged();
                break;
        }
//        animatedProgress.hideProgress();
        Log.w(TAG,"onScanSuccess "+successCode);
    }

    @Override
    public void onScanFailed(int errorCode) {
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
        notifyDataSetChanged();
        if(AppHelper.IS_TESTING)
        {
            ContentValues contentValues = new ContentValues();
            ContentValues deviceContentValue = new ContentValues();
            switch (requestCode2) {
                case GROUP_STATE_COMMAND_RESPONSE:
                    boolean groupStatus = !arrayList.get(selectedPosition).getGroupStatus();

                    contentValues.put(COLUMN_GROUP_STATUS, groupStatus);
                    contentValues.put(COLUMN_GROUP_PROGRESS, groupStatus ? 100 : 0);

                    deviceContentValue.put(COLUMN_DEVICE_STATUS,groupStatus);
                    deviceContentValue.put(COLUMN_DEVICE_PROGRESS, groupStatus ? 100 : 0);

                    arrayList.get(selectedPosition).setGroupStatus(groupStatus);
                    arrayList.get(selectedPosition).setGroupDimming(groupStatus ? 100 : 0);

                    Log.w("DashGroup", AppHelper.sqlHelper.updateGroup(arrayList.get(selectedPosition).getGroupId(), contentValues) + "");
                    Log.w("DashGroup12", AppHelper.sqlHelper.updateGroupDevice(arrayList.get(selectedPosition).getGroupId(), deviceContentValue) + "");
                    break;

                case LIGHT_LEVEL_GROUP_COMMAND_RESPONSE:

                    contentValues.put(COLUMN_GROUP_STATUS,true);
                    contentValues.put(COLUMN_GROUP_PROGRESS, seekBarProgress);

                    deviceContentValue.put(COLUMN_DEVICE_STATUS,true);
                    deviceContentValue.put(COLUMN_DEVICE_PROGRESS, seekBarProgress);

                    arrayList.get(selectedPosition).setGroupDimming(seekBarProgress);
                    arrayList.get(selectedPosition).setGroupStatus(true);

                    Log.w("DashGroup", AppHelper.sqlHelper.updateGroup(arrayList.get(selectedPosition).getGroupId(), contentValues) + "");
                    Log.w("DashGroup12", AppHelper.sqlHelper.updateGroupDevice(arrayList.get(selectedPosition).getGroupId(), deviceContentValue) + "");
                    notifyDataSetChanged();
                    break;
            }
        }
        Log.w(TAG,"onScanFailed "+errorCode);
    }

    @Override
    public void onSuccess(String message) {
//        animatedProgress.showProgress();
        Log.w(TAG,"onSuccess "+message);
    }

    @Override
    public void onFailed(String errorMessage)

    {
        Log.w(TAG,"onFailed "+errorMessage);
        animatedProgress.hideProgress();
    }

    @Override
    public void onStop(String stopMessage, int resultCode)
    {
//        Log.w(TAG,"onStop "+stopMessage);
//        requestCode2=resultCode;
//        scannerTask.setRequestCode(resultCode);
//        scannerTask.start();
        //        Log.w(TAG,"onStop "+stopMessage);
//        requestCode2=resultCode;
//        scannerTask.setRequestCode(resultCode);
//        scannerTask.start();

        animatedProgress.hideProgress();
        ContentValues contentValues=new ContentValues();
        ContentValues deviceContentValue = new ContentValues();



        switch (resultCode)
        {
            case GROUP_STATE_COMMAND_RESPONSE:

                boolean groupStatus = !arrayList.get(selectedPosition).getGroupStatus();

                contentValues.put(COLUMN_GROUP_STATUS, groupStatus);
                contentValues.put(COLUMN_GROUP_PROGRESS, groupStatus ? 100 : 0);

                deviceContentValue.put(COLUMN_DEVICE_STATUS, groupStatus);
                deviceContentValue.put(COLUMN_DEVICE_PROGRESS, groupStatus ? 100 : 0);

                arrayList.get(selectedPosition).setGroupStatus(groupStatus);
                arrayList.get(selectedPosition).setGroupDimming(groupStatus ? 100 : 0);
                Log.w("DashGroup", AppHelper.sqlHelper.updateGroup(arrayList.get(selectedPosition).getGroupId(), contentValues) + "");
                Log.w("DashGroup12", AppHelper.sqlHelper.updateGroupDevice(arrayList.get(selectedPosition).getGroupId(), deviceContentValue) + "");

                notifyDataSetChanged();
                Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();
                break;

            case LIGHT_LEVEL_GROUP_COMMAND_RESPONSE:



                contentValues.put(COLUMN_GROUP_PROGRESS, seekBarProgress);
                contentValues.put(COLUMN_GROUP_STATUS, 1);

                deviceContentValue.put(COLUMN_DEVICE_STATUS, 1);
                deviceContentValue.put(COLUMN_DEVICE_PROGRESS, seekBarProgress);
                arrayList.get(selectedPosition).setGroupDimming(seekBarProgress);
                arrayList.get(selectedPosition).setGroupStatus(true);
                Log.w("DashGroup", AppHelper.sqlHelper.updateGroup(arrayList.get(selectedPosition).getGroupId(), contentValues) + "");
                Log.w("DashGroup12", AppHelper.sqlHelper.updateGroupDevice(arrayList.get(selectedPosition).getGroupId(), deviceContentValue) + "");

                notifyDataSetChanged();
                Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();
                break;
        }


    }

    static class ViewHolder {
        @BindView(R.id.dashboard_deviceName)
        NormalText dashboardDeviceName;

        @BindView(R.id.individual_customize)
        Button dashboardCustomize;

        @BindView(R.id.light_details)
        ImageButton lightDetails;

        @BindView(R.id.status_switch)
        JellyToggleButton statusSwitch;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
