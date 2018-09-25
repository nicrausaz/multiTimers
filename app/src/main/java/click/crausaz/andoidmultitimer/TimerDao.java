package click.crausaz.andoidmultitimer;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TimerDao {
    @Query("SELECT * FROM Timer")
    List<Timer> getAll();

    /*@Query("SELECT * FROM Timer WHERE timer_id IN (:userIds)")
    List<Timer> loadAllByIds(int[] userIds);*/

    @Insert
    void insertAll(Timer... timers);

    @Delete
    void delete(Timer user);
}
