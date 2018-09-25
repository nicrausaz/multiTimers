package click.crausaz.andoidmultitimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Timer> {

    private Context mContext;
    private List<Timer> timers_list;

    public CustomAdapter( Context context, ArrayList<Timer> list) {
        super(context, 0, list);
        mContext = context;
        timers_list = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.timer, parent,false);

        Timer current_timer = timers_list.get(position);

        TextView name = listItem.findViewById(R.id.name);
        name.setText(current_timer.timer_name);

        TextView actual_time = listItem.findViewById(R.id.actual_time);
        actual_time.setText(current_timer.timer_actual_time);

        TextView full_time = listItem.findViewById(R.id.full_time);
        full_time.setText(current_timer.timer_full_time);

        return listItem;
    }
}