package no.uio.cesar.View.FeedView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.Model.Record;
import no.uio.cesar.R;
import no.uio.cesar.Utils.Export;
import no.uio.cesar.Utils.Uti;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.RecordViewHolder> {

    class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitle, tvDate, tvDescription, tvTime, tvSamples, tvAvgResp;
        private RatingBar rbRating;

        private View recordToggle, btnShare, btnAnalytics, btnDelete;

        RecordViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.record_title);
            tvDate = itemView.findViewById(R.id.record_date);
            tvDescription = itemView.findViewById(R.id.record_description);
            tvTime = itemView.findViewById(R.id.record_time);
            tvSamples = itemView.findViewById(R.id.record_samples);
            tvAvgResp = itemView.findViewById(R.id.record_resp);

            rbRating = itemView.findViewById(R.id.record_rating);

            recordToggle = itemView.findViewById(R.id.record_toggle);

            btnShare = itemView.findViewById(R.id.record_share);
            btnAnalytics = itemView.findViewById(R.id.record_analytics);
            btnDelete = itemView.findViewById(R.id.record_delete);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //listener.onRecordItemClick(v, this.getAdapterPosition());
            if (recordToggle.getVisibility() == View.GONE) {
                recordToggle.setVisibility(View.VISIBLE);
            } else {
                recordToggle.setVisibility(View.GONE);
            }
        }
    }

    private List<Record> mRecords;
    private FeedViewClickListener listener;

    FeedAdapter(FeedViewClickListener listener) {
        mRecords = new ArrayList<>();
        this.listener = listener;
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

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record, parent, false);

        return new RecordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record currentRecord = mRecords.get(position);

        holder.tvTitle.setText(currentRecord.getName());
        holder.tvDescription.setText(currentRecord.getDescription() != null && currentRecord.getDescription().isEmpty()
                ? "No description..."
                : currentRecord.getDescription());

        holder.tvDate.setText(DateFormat.getDateInstance().format(currentRecord.getCreatedAt()));
        holder.rbRating.setRating(currentRecord.getRating());
        holder.tvSamples.setText("" + currentRecord.getNrSamples());

        long[] timeConverted = Uti.splitSecondsToHMS(currentRecord.getMonitorTime());

        holder.tvTime.setText(String.format(Locale.getDefault(),
                "%dh %dm %ds", timeConverted[0], timeConverted[1], timeConverted[2]));

        holder.btnDelete.setOnClickListener(l -> listener.onRecordDeleteClick(currentRecord));
        holder.btnShare.setOnClickListener(l -> listener.onRecordShareClick(currentRecord));

    }
}
