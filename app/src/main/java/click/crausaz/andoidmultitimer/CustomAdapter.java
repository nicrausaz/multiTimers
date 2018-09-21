package click.crausaz.andoidmultitimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {
    Context context;
    String countryList[];
    String descriptions[];
    LayoutInflater inflater;

    public CustomAdapter(Context applicationContext, String[] countryList, String[] descriptions) {
        this.context = applicationContext;
        this.countryList = countryList;
        this.descriptions = descriptions;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return countryList.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.activity_home, null);
        TextView actual_time = (TextView) view.findViewById(R.id.actual_time);
        TextView full_time = (TextView) view.findViewById(R.id.full_time);
        actual_time.setText(countryList[i]);
        full_time.setText(descriptions[i]);
        return view;
    }
}