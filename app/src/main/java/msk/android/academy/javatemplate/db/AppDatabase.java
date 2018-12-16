package msk.android.academy.javatemplate.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;


@Database(entities = {
            InfoEntity.class
        },
        version = 2,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract InfoEntityDao getEntityDao();
}
