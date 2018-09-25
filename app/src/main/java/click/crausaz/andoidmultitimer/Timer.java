package click.crausaz.andoidmultitimer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Timer{
    @PrimaryKey
    public int timer_id;
    @ColumnInfo(name = "timer_name")
    public String timer_name;
    @ColumnInfo(name = "timer_full_time")
    public String timer_full_time;
    @ColumnInfo(name = "timer_actual_time")
    public String timer_actual_time;
}
