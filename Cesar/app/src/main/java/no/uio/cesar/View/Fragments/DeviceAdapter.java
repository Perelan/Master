package no.uio.cesar.View.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.R;

class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    class DeviceViewHolder extends RecyclerView.ViewHolder {

        private TextView mSensorName;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);

            mSensorName = itemView.findViewById(R.id.sensor_name);
        }
    }


    ArrayList<Sensor> mDataSet;


    public DeviceAdapter(ArrayList<Sensor> mDataSet) {
        this.mDataSet = mDataSet;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.sensor_item, viewGroup, false);

        return new DeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder viewHolder, int i) {
        viewHolder.mSensorName.setText(mDataSet.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
