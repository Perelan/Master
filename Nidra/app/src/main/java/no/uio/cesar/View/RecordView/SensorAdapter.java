package no.uio.cesar.View.RecordView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.Model.Sensor;
import no.uio.cesar.R;

class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.DeviceViewHolder> {

    class DeviceViewHolder extends RecyclerView.ViewHolder {

        private TextView mSensorName;

        DeviceViewHolder(@NonNull View itemView) {
            super(itemView);

            mSensorName = itemView.findViewById(R.id.sensor_name);
        }
    }

    private ArrayList<Sensor> mDataSet = new ArrayList<>();

    void parseSensorData(List<String> sensors) {
        if (sensors.isEmpty()) return;

        loop:
        for (String s : sensors) {
            String[] parse = s.split(",");

            String capAndId = parse[0],
                    type    = parse[1],
                    metric  = parse[2],
                    desc    = parse[3],
                    freq    = parse[4];

            for (Sensor sens : mDataSet) if (sens.getDesc().equals(desc)) continue loop;

            mDataSet.add(new Sensor(capAndId, type, metric, desc, freq));
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_sensor, viewGroup, false);

        return new DeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder viewHolder, int i) {
        viewHolder.mSensorName.setText(mDataSet.get(i).getDesc());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
