package com.cenzer.Fragments;


import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cenzer.Adapter.DashboardItemAdapter;
import com.cenzer.Adapter.GroupedLightAdapter;
import com.cenzer.Adapter.IndividualLightAdapter;
import com.cenzer.CustomView.NonScrollListView;
import com.cenzer.DatabaseModule.DatabaseConstant;
import com.cenzer.PogoClasses.DeviceClass;
import com.cenzer.PogoClasses.GroupDetailsClass;
import com.cenzer.PogoClasses.GroupedLight;
import com.cenzer.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.cenzer.Activities.AppHelper.sqlHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragmentBackUp extends Fragment {


    @BindView(R.id.dashboard_home_setting_layout)
        LinearLayout dashboardHomeSettingLayout;

    @BindView(R.id.group_list_layout)
        LinearLayout groupListLayout;

    @BindView(R.id.individual_light_list_layout)
        LinearLayout individualLightListLayout;

    @BindView(R.id.dashboard_spinner)
        Spinner dashboardSpinner;
    @BindView(R.id.dash_group_list)
        NonScrollListView dashboargItemList;

//    @BindView(R.id.grouped_light_list)
//    NonScrollListView groupedLightList;



    @BindView(R.id.individual_light_list)
    NonScrollListView individualLightList;

    Unbinder unbinder;
    DashboardItemAdapter dashboardItemAdapter;

    IndividualLightAdapter individualLightAdapter;

    GroupedLightAdapter groupedLightAdapter;

    Activity activity;
    ArrayList<DeviceClass> deviceList;
    ArrayAdapter<GroupDetailsClass> adapter;
    ArrayList<GroupDetailsClass> list;
    ArrayList<GroupedLight> groupedLightArrayList;

    public DashboardFragmentBackUp() {
        // Required empty public constructor
        list=new ArrayList<>();
        groupedLightArrayList=new ArrayList<>();
        deviceList=new ArrayList<>();

    }
    public void getAllGroups()
    {
        list.clear();
        Cursor cursor = sqlHelper.getAllGroup();
        if (cursor.moveToFirst())
        {
            do {
                GroupDetailsClass groupData = new GroupDetailsClass();
                groupData.setGroupId(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_GROUP_ID)));
                groupData.setGroupDimming(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_GROUP_PROGRESS)));
                groupData.setGroupName(cursor.getString(cursor.getColumnIndex(DatabaseConstant.COLUMN_GROUP_NAME)));
                groupData.setGroupStatus(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_GROUP_STATUS)) == 1);
                list.add(groupData);
                // do what ever you want here
            }
            while (cursor.moveToNext());
        }

//
        cursor.close();
        adapter.notifyDataSetChanged();
        dashboardItemAdapter.setList(list);
    }

    public void getAllGroupedLight()
    {
        groupedLightArrayList.clear();

        Cursor cursor = sqlHelper.getAllGroupLight();

        if (cursor.moveToFirst())
        {
            do {
                GroupedLight groupData = new GroupedLight();
                groupData.setGroupId(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_GROUP_ID)));
                groupData.setGroupName(cursor.getString(cursor.getColumnIndex(DatabaseConstant.COLUMN_GROUP_NAME)));
                groupData.setDeviceName(cursor.getString(cursor.getColumnIndex(DatabaseConstant.COLUMN_DEVICE_NAME)));
                groupData.setDeviceUid(cursor.getLong(cursor.getColumnIndex(DatabaseConstant.COLUMN_DEVICE_UID)));
                groupedLightArrayList.add(groupData);
                // do what ever you want here
            }
            while (cursor.moveToNext());
        }

//
        cursor.close();
        groupedLightAdapter.setList(groupedLightArrayList);
    }


    public void getDevice()
{
    deviceList=new ArrayList<>();
    Cursor cursor=sqlHelper.getAllDevice(DatabaseConstant.ADD_DEVICE_TABLE);
    if (cursor.moveToFirst())
    {
        do{
            DeviceClass deviceClass=new DeviceClass();
            deviceClass.setDeviceName(cursor.getString(cursor.getColumnIndex(DatabaseConstant.COLUMN_DEVICE_NAME)));
            deviceClass.setDeviceUID(cursor.getLong(cursor.getColumnIndex(DatabaseConstant.COLUMN_DEVICE_UID)));
            deviceClass.setDeriveType(cursor.getString(cursor.getColumnIndex(DatabaseConstant.COLUMN_DERIVE_TYPE)));
            deviceClass.setDeviceDimming(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_DEVICE_PROGRESS)));
            deviceClass.setGroupId(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_GROUP_ID)));
            deviceClass.setStatus(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.COLUMN_DEVICE_STATUS))==1);
            deviceList.add(deviceClass);
            // do what ever you want here
        }
        while(cursor.moveToNext());
    }
    cursor.close();
    individualLightAdapter.setList(deviceList);
}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getDevice();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        unbinder = ButterKnife.bind(this, view);

        activity=getActivity();
        if (activity==null)
            return view;
        dashboardItemAdapter =new DashboardItemAdapter(activity);
        individualLightAdapter =new IndividualLightAdapter(activity);
        groupedLightAdapter =new GroupedLightAdapter(activity);


//        adapter=new ArrayAdapter<GroupDetailsClass>(this, R.layout.spinerlayout, list);
        adapter=new ArrayAdapter<GroupDetailsClass>(activity,R.layout.spinerlayout,list){
            public View getView(int position, View convertView, ViewGroup parent) {
                // Cast the spinner collapsed item (non-popup item) as a text view
                TextView tv = (TextView) super.getView(position, convertView, parent);

                // Set the text color of spinner item
                tv.setTextColor(Color.GRAY);
                tv.setText(list.get(position).getGroupName());
                // Return the view
                return tv;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent){
                // Cast the drop down items (popup items) as text view
                TextView tv = (TextView) super.getDropDownView(position,convertView,parent);

                // Set the text color of drop down items
                tv.setTextColor(Color.BLACK);
                tv.setText(list.get(position).getGroupName());

                /*// If this item is selected item
                if(position == mSelectedIndex){
                    // Set spinner selected popup item's text color
                    tv.setTextColor(Color.BLUE);
                }*/

                // Return the modified view
                return tv;
            }
        };


       /* adapter = ArrayAdapter.createFromResource(activity,
                R.array.dashboard_spinner, R.layout.spinerlayout);
       */
//       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dashboargItemList.setAdapter(dashboardItemAdapter);
//        groupedLightList.setAdapter(groupedLightAdapter);
        individualLightList.setAdapter(individualLightAdapter);
        dashboardSpinner.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
//        getAllGroups();
//        getAllGroupedLight();
        getDevice();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.dashboard_home_setting_layout)
    public void onViewClicked() {
    }
}
