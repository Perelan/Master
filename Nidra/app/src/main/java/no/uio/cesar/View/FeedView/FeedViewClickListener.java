package no.uio.cesar.View.FeedView;

import android.view.View;

import no.uio.cesar.Model.Record;

public interface FeedViewClickListener {
    void onRecordAnalyticsClick(Record record);
    void onRecordDeleteClick(Record record);
    void onRecordShareClick(Record record);
}
