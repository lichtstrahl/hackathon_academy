package msk.android.academy.javatemplate.bd;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import msk.android.academy.javatemplate.model.Song;

@Database(entities = {Song.class}, version = 2)
public abstract class SongDatabase extends RoomDatabase {
    public abstract SongDao songDao();
}