package no.uio.cesar.Model.Interface;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import no.uio.cesar.Model.Module;

@Dao
public interface ModuleDao {
    @Insert
    void insert(Module module);

    @Update
    void update(Module module);

    @Delete
    void delete(Module module);

    @Query("SELECT * FROM module_table")
    LiveData<List<Module>> getAllModules();
}
