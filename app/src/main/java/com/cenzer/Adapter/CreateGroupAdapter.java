package com.cenzer.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.Toast;

import com.CustomProgress.CustomDialog.AnimatedProgress;
import com.cenzer.CustomView.NormalText;
import com.cenzer.PogoClasses.DeviceClass;
import com.cenzer.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CreateGroupAdapter extends BaseAdapter  implements Filterable{
    Activity activity;
    ArrayList<DeviceClass> arrayList;
    ArrayList<DeviceClass> filterarrayList;

    ArrayList<String> selectedLight;

    AnimatedProgress animatedProgress;

    private ItemFilter mFilter = new ItemFilter();

    public CreateGroupAdapter(@NonNull Activity context) {
        activity = context;
        arrayList = new ArrayList<>();
        selectedLight=new ArrayList<>();
        filterarrayList = new ArrayList<>();
        animatedProgress = new AnimatedProgress(activity);
        //progressDialog.setCanceledOnTouchOutside(false);
    }
    public void setList(ArrayList<DeviceClass> arrayList)
    {
        this.arrayList.clear();
        this.arrayList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedLight() {
        return selectedLight;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public DeviceClass getItem(int position) {
        if (arrayList.size() <= position)
            return null;
        return arrayList.get(position);
    }

    public void showDialog()
    {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.light_device_details);
        dialog.show();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).
                    inflate(R.layout.create_group_list_item, parent, false);}
        DeviceClass deviceClass=arrayList.get(position);
        deviceClass.setStatus(selectedLight.contains(String.valueOf(position)));
        ViewHolder viewHolder=new ViewHolder(convertView);
        viewHolder.customizeDevice.setOnClickListener(view -> showDialog());
        viewHolder.circleView.setBackground(activity.getResources().getDrawable(deviceClass.getMasterStatus()==0?R.drawable.white_circle_border:R.drawable.yellow_circle));
        viewHolder.deviceLight.setText(deviceClass.getDeviceName());
        if (selectedLight.contains(String.valueOf(position)))
        {
            viewHolder.selectDevice.setText("Remove");
        }
        else {
            viewHolder.selectDevice.setText("Select");
        }
        viewHolder.selectDevice.setOnClickListener(view ->
        {


            if (selectedLight.contains(String.valueOf(position)))
            {
                viewHolder.selectDevice.setText("Select");

                selectedLight.remove(String.valueOf(position));
//                    Toast.makeText(activity, "Will be soon.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(selectedLight.size()>=1)
                {
                    Toast.makeText(activity, "Maximum one value", Toast.LENGTH_SHORT).show();
                    return;
                }

                selectedLight.add(String.valueOf(position));
                viewHolder.selectDevice.setText("Remove");
            }
        });

        return convertView;
    }

    void showAlert(String message, String title) {
        if (title.length() < 1)
            title = "Alert";
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setTitle(title);
        builder.setPositiveButton("Remove", (dialog1, id) -> {
            // User clicked OK button
//            acceptRequest(2,position);
            dialog1.dismiss();
            Toast.makeText(activity, "Will be soon", Toast.LENGTH_SHORT).show();

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public Filter getFilter() {
//        arrayList=filterarrayList;
        return mFilter;
    }


    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

           /* final ArrayList<FriendsRequestData.FRequestDetails> list = filterarrayList;
            int count = list.size();
            final ArrayList<FriendsRequestData.FRequestDetails> nlist = new ArrayList<>(count);

            FriendsRequestData.FRequestDetails filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getUserName().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }*/
    /*        results.values = nlist;
            results.count = nlist.size();*/

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // arrayList = (ArrayList<FriendsRequestData.FRequestDetails>) results.values;
            // notifyDataSetChanged();
        }

    }

    static class ViewHolder {
        @BindView(R.id.customize_device)
        Button customizeDevice;
        @BindView(R.id.select_device)
        Button selectDevice;
        @BindView(R.id.create_group_light)
        NormalText deviceLight;

        @BindView(R.id.review_1)
        ImageButton circleView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
