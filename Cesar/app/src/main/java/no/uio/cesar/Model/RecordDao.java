package no.uio.cesar.Model;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RecordDao {

    @Insert
    void insert(Record record);

    @Update
    void update(Record record);

    @Delete
    void delete(Record record);

    @Query("SELECT * FROM record_table ORDER BY createdAt DESC")
    LiveData<List<Record>> getAllRecords();
}
