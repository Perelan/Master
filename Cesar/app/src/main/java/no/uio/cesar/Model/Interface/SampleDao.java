package no.uio.cesar.Model.Interface;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Sample;

@Dao
public interface SampleDao {

    @Insert
    void insert(Sample sample);

    @Update
    void update(Sample sample);

    @Delete
    void delete(Sample sample);

    @Query("SELECT * FROM sample_table WHERE recordId=:recordId")
    LiveData<List<Sample>> getSamplesForRecord(final long recordId);
}
