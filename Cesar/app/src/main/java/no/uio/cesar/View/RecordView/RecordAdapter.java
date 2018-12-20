package no.uio.cesar.View.RecordView;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.Model.Record;
import no.uio.cesar.R;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitle;

        RecordViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.record_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onRecordItemClick(v, this.getAdapterPosition());
        }
    }

    private List<Record> mRecords;
    private RecordViewClickListener listener;

    RecordAdapter(RecordViewClickListener listener) {
        mRecords = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_item, parent, false);

        return new RecordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record currentRecord = mRecords.get(position);

        holder.tvTitle.setText(currentRecord.getName());
    }

    @Override
    public int getItemCount() {
        return mRecords.size();
    }

    public List<Record> getRecords() { return mRecords; }

    public void insertRecord(List<Record> records) {
        this.mRecords = records;
        notifyDataSetChanged();
    }

}
