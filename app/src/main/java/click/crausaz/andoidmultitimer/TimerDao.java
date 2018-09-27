package click.crausaz.andoidmultitimer;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TimerDao {
    @Query("SELECT * FROM Timer")
    List<Timer> getAll();

    @Insert
    void insertAll(Timer... timers);

    @Update
    void update(Timer... timers);

    @Delete
    void delete(Timer ...timer);

    @Query("DELETE FROM Timer")
    void deleteAll();
}
