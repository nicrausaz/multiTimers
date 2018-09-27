package click.crausaz.andoidmultitimer;

import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Home extends AppCompatActivity {

    private CustomAdapter timers_adapter;
    private ArrayList<Timer> timers_list;
    private ListView listView;
    private AppDatabase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DB = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "timers").allowMainThreadQueries().build();

        listView = findViewById(R.id.timers_list);
        initAddButton();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        loadTimersData();
        initListViewListeners(listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_reset_all) {
            return true;
        }

        if (id == R.id.action_delete_all) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.dialog_delete_all_title))
                    .setMessage(getString(R.string.dialog_delete_all_message))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DB.timerDao().deleteAll();
                            loadTimersData();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) { }
                    })
                    .setIcon(R.drawable.baseline_warning_black_18dp)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initAddButton () {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { initAddOrEditTimerDialog("add", null); }
        });
    }

    private void initAddOrEditTimerDialog (String use_case, @Nullable Timer editing_timer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.add_timer_dialog,null);
        final EditText new_name = mView.findViewById(R.id.new_timer_name);
        final EditText new_time = mView.findViewById(R.id.new_timer_time);
        final String usage_case = use_case;
        final Timer edited_timer = editing_timer;
        Button mLogin = mView.findViewById(R.id.add_button);

         if (usage_case == "edit") {
            new_name.setText(editing_timer.timer_name);
            new_time.setText(editing_timer.timer_full_time);
        }

        builder.setView(mView);
        final AlertDialog dialog = builder.create();
        dialog.show();
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String returned_message = validateAddTimerInputs(new_name.getText().toString(), new_time.getText().toString());
                if (returned_message == "success") {
                    if (usage_case == "edit") {
                        edited_timer.timer_name = new_name.getText().toString();
                        edited_timer.timer_full_time = new_time.getText().toString();
                        edited_timer.timer_actual_time = edited_timer.timer_full_time;
                        DB.timerDao().update(edited_timer);
                    } else if (usage_case == "add") {
                        writeNewTimer(new_name.getText().toString(), new_time.getText().toString());
                    }
                    dialog.dismiss();
                    loadTimersData();
                } else {
                    Snackbar.make(view, returned_message, Snackbar.LENGTH_LONG)
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
                Timer selected_timer = (Timer) parent.getItemAtPosition(position);
                triggerTimer(selected_timer);
            }
        });

        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        if (v.getId() == R.id.timers_list){
            // AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo)menuInfo;
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
                Timer to_reset = timers_list.get(selectpos);
                to_reset.timer_actual_time = to_reset.timer_full_time;
                DB.timerDao().delete(to_reset);
                break;
            case 1:
                // edit timer
                Timer to_edit = timers_list.get(selectpos);
                initAddOrEditTimerDialog("edit", to_edit);
                break;
            case 2:
                // delete timer
                Timer to_delete = timers_list.get(selectpos);
                DB.timerDao().delete(to_delete);
                break;
        }
        loadTimersData();
        return super.onContextItemSelected(item);
    }

    private void triggerTimer (Timer selected_timer) {
        TextView textView = findViewById(R.id.name);
        // check if timer is counting
        if (selected_timer.timer_is_running) {
            // stop timer
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_pause_black_18dp, 0, 0, 0);
            selected_timer.timer_is_running = false;
            stopService(new Intent(this, TimerService.class));
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_play_arrow_black_18dp, 0, 0, 0);
            // start time
            selected_timer.timer_is_running = true;
            startService(new Intent(this, TimerService.class));
        }

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "timers").allowMainThreadQueries().build();
        db.timerDao().update(selected_timer);
        loadTimersData();
    }

    private void writeNewTimer(String name, String time) {
        Timer new_timer = new Timer();
        new_timer.timer_name = name;
        new_timer.timer_full_time = time;
        new_timer.timer_actual_time = time;
        DB.timerDao().insertAll(new_timer);
    }

    private void loadTimersData () {
        timers_list = getTimers();
        timers_adapter = new CustomAdapter(this, timers_list);
        listView.setAdapter(timers_adapter);
        timers_adapter.notifyDataSetChanged();
    }

    private String validateAddTimerInputs (String new_name, String new_time) {
        if(new_name.isEmpty() || new_time.isEmpty()){
            return "Please fill inputs";
        } else if (!Pattern.matches("[0-9][0-9]:[0-9][0-9]:[0-9][0-9]", new_time)) {
            return "Wrong time format (must be hh:mm:ss)";
        } else {
            return "success";
        }
    }

    private ArrayList<Timer> getTimers () {
        return (ArrayList<Timer>) DB.timerDao().getAll();
    }
}
