package com.sensordroid.flow.View;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensordroid.flow.R;

import java.util.ArrayList;

class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private ArrayList<BluetoothDevice> mDataset;
    private OnDeviceClickListener mClickListener;

    class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mDeviceTitle, mDeviceMac;
        ImageView mDeviceImage;

        DeviceViewHolder(@NonNull View v) {
            super(v);
            mDeviceTitle = v.findViewById(R.id.device_title);
            mDeviceMac = v.findViewById(R.id.device_mac);
            mDeviceImage = v.findViewById(R.id.device_image);
            v.setOnClickListener(this);
        }

        public void onClick(View v) {
            mClickListener.onDeviceClick(getAdapterPosition());
        }
    }

    DeviceAdapter(ArrayList<BluetoothDevice> data, OnDeviceClickListener listener) {
        mDataset = data;
        mClickListener = listener;
    }

    @NonNull
    @Override

    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.device_item, viewGroup, false);

        return new DeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder viewHolder, int i) {
        BluetoothDevice device = mDataset.get(i);

        viewHolder.mDeviceTitle.setText(device.getName());
        viewHolder.mDeviceMac.setText(device.getAddress());

        if (device.getName().equalsIgnoreCase("oarzpot")) {
            viewHolder.mDeviceImage.setImageResource(R.drawable.flow);
        } else {
            viewHolder.mDeviceImage.setImageResource(R.drawable.bluetooth);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
