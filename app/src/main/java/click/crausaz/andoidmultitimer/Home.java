package click.crausaz.andoidmultitimer;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    public CustomAdapter timers_adapter;
    public ArrayList<Timer> timers_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ListView listView = findViewById(R.id.timers_list);
        setSupportActionBar(toolbar);
        initAddButton();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        timers_list = getTimers();
        timers_adapter = new CustomAdapter(this, timers_list);
        listView.setAdapter(timers_adapter);
        initListViewListeners(listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_resetall) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initAddButton () {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { initAddTimerDialog(); }
        });
    }

    private void initAddTimerDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.add_timer_dialog,null);
        final EditText new_name = mView.findViewById(R.id.new_timer_name);
        final EditText new_time = mView.findViewById(R.id.new_timer_time);
        Button mLogin = mView.findViewById(R.id.add_button);

        builder.setView(mView);
        final AlertDialog dialog = builder.create();
        dialog.show();
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!new_name.getText().toString().isEmpty() && !new_time.getText().toString().isEmpty()){
                    writeNewTimer(new_name.getText().toString(), new_time.getText().toString());
                    dialog.dismiss();
                } else {
                    Snackbar.make(view, "Please fill values", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    private void initListViewListeners (ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String selectedItem = parent.getItemAtPosition(position).toString();

                // Display the selected item text on TextView
                Log.i("selected:", selectedItem);
            }
        });

        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        if (v.getId() == R.id.timers_list){
            AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.add(0,0,0,"Reset");
            menu.add(0,1,1,"Edit");
            menu.add(0,2,2,"Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuinfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int selectpos = menuinfo.position;

        switch (item.getItemId()) {
            case 0:
                // reset timer
                break;
            case 1:
                // edit timer
                break;
            case 2:
                // delete timer
                AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "timers").allowMainThreadQueries().build();
                Timer to_delete = timers_list.get(selectpos);
                db.timerDao().delete(to_delete);
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void triggerTimer (View view) {
        Log.i("click", "clickefoasdbopfbsioudfg");
    }

    private void writeNewTimer(String name, String time) {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "timers").allowMainThreadQueries().build(); // replace allowMainThread by async methods ?!
        Timer new_timer = new Timer();
        new_timer.timer_name = name;
        new_timer.timer_full_time = time;
        new_timer.timer_actual_time = time;
        db.timerDao().insertAll(new_timer);
        timers_adapter.notifyDataSetChanged();
    }

    private ArrayList<Timer> getTimers() {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "timers").allowMainThreadQueries().build();
        return (ArrayList<Timer>) db.timerDao().getAll();
    }
}
