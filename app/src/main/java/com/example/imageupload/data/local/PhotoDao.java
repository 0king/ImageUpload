package com.example.imageupload.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.imageupload.data.model.Photo;

import java.util.List;

@Dao
public interface PhotoDao {

    @Query("SELECT * FROM photos")
    LiveData<List<Photo>> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Photo p);

    @Delete
    void delete (Photo p);

}
