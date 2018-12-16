package msk.android.academy.javatemplate.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface InfoEntityDao {
    @Update
    void update(InfoEntity entity);
    @Delete
    void delete(InfoEntity entity);
    @Insert
    void insert(InfoEntity entity);

    @Query("SELECT * FROM InfoEntity WHERE artist = :artist AND track = :track")
    InfoEntity searchInfiEntity(String artist, String track);
}
