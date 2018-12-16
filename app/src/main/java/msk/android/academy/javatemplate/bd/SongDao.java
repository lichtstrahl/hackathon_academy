package msk.android.academy.javatemplate.bd;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import msk.android.academy.javatemplate.model.Song;

@Dao
public interface SongDao {
    @Query("SELECT * FROM song")
    Flowable<List<Song>> getAll();

    @Query("SELECT * FROM song WHERE id = :id")
    Song getById(long id);

    @Insert
    void insert(Song employee);

    @Update
    void update(Song employee);

    @Delete
    void delete(Song employee);
}
