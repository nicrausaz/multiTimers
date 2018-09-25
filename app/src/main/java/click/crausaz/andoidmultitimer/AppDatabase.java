package click.crausaz.andoidmultitimer;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Timer.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TimerDao timerDao();
}
