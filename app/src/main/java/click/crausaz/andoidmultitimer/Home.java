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
import java.util.concurrent.TimeUnit;
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

        DB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "timers").build();

        listView = findViewById(R.id.timers_list);
        initAddButton();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        loadAndSetTimersDataAsync();
        initListViewListeners(listView);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_home, menu);
        if (timers_list.size() == 0) {
            menu.getItem(2).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_reset_all) {
            // a tester
            resetAllTimers();
            return true;
        }

        if (id == R.id.action_delete_all) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);

            // Confirmation dialog
            builder.setTitle(getString(R.string.dialog_delete_all_title))
                    .setMessage(getString(R.string.dialog_delete_all_message))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteAllTimers();
                            // TODO: Find a way to replace this
                            try {
                                // replace this with proper code
                                TimeUnit.MILLISECONDS.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            loadAndSetTimersDataAsync();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) { }
                    })
                    .setIcon(R.drawable.baseline_warning_black_18dp)
                    .show();

            loadAndSetTimersDataAsync();
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
        // Build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.add_timer_dialog,null);
        final EditText new_name = mView.findViewById(R.id.new_timer_name);
        final EditText new_time = mView.findViewById(R.id.new_timer_time);
        Button button_add = mView.findViewById(R.id.add_button);


        final String usage_case = use_case;
        final Timer edited_timer = editing_timer;

        // If editing mode, preload field with existing data
        if (usage_case == "edit") {
            new_name.setText(editing_timer.timer_name);
            new_time.setText(editing_timer.timer_full_time);
        }

        builder.setView(mView);
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Action on confirm button
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String returned_message = validateTimerInputs(new_name.getText().toString(), new_time.getText().toString());

                if (returned_message == "success") {
                    if (usage_case == "edit") {
                        // Replace Timer values with new ones
                        edited_timer.timer_name = new_name.getText().toString();
                        edited_timer.timer_full_time = new_time.getText().toString();
                        edited_timer.timer_actual_time = edited_timer.timer_full_time;
                        updateTimer(edited_timer);
                    } else if (usage_case == "add") {
                        // Create & add new Timer
                        Timer new_timer = new Timer();
                        new_timer.timer_name = new_name.getText().toString();
                        new_timer.timer_full_time = new_time.getText().toString();
                        new_timer.timer_actual_time = new_time.getText().toString();
                        insertTimer(new_timer);
                    }
                    dialog.dismiss();
                    loadAndSetTimersDataAsync();
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
        int selected_pos = menuinfo.position;

        switch (item.getItemId()) {
            case 0:
                // reset timer
                Timer to_reset = timers_list.get(selected_pos);
                to_reset.timer_actual_time = to_reset.timer_full_time;
                updateTimer(to_reset);
                loadAndSetTimersDataAsync();
                break;
            case 1:
                // edit timer
                Timer to_edit = timers_list.get(selected_pos);
                initAddOrEditTimerDialog("edit", to_edit);
                loadAndSetTimersDataAsync();
                break;
            case 2:
                // delete timer
                Timer to_delete = timers_list.get(selected_pos);
                deleteTimer(to_delete);
                // TODO: Find a way to replace this
                try {
                    // replace this with proper code
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                loadAndSetTimersDataAsync();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void triggerTimer (Timer selected_timer) {
        TextView textView = findViewById(R.id.name);
        Intent intent = new Intent(this, TimerService.class);
        // check if timer is counting
        if (selected_timer.timer_is_running) {
            // stop timer
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_pause_black_18dp, 0, 0, 0);
            selected_timer.timer_is_running = false;
//            intent.putExtra("timer_name", selected_timer.timer_name);

            stopService(new Intent(this, TimerService.class));
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_play_arrow_black_18dp, 0, 0, 0);
            // start time
            selected_timer.timer_is_running = true;
//            Intent intent = new Intent(this, TimerService.class);
            intent.putExtra("timer_name", selected_timer.timer_name);
            intent.putExtra("timer_time", selected_timer.timer_actual_time);
            startService(intent);
        }
         updateTimer(selected_timer);
         updateViewTimers();
    }

    private String validateTimerInputs (String new_name, String new_time) {
        if(new_name.isEmpty() || new_time.isEmpty()){
            return "Please fill inputs";
        } else if (!Pattern.matches("[0-9][0-9]:[0-9][0-9]:[0-9][0-9]", new_time)) {
            return "Wrong time format (must be hh:mm:ss)";
        } else {
            return "success";
        }
    }

    private void updateViewTimers () {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timers_adapter = new CustomAdapter(getApplicationContext(), timers_list);
                listView.setAdapter(timers_adapter);
                timers_adapter.notifyDataSetChanged();
            }
        });
    }

    // -- Functions using DB
    private void loadAndSetTimersDataAsync () {
        new Thread(new Runnable(){
            @Override
            public void run(){
                timers_list = (ArrayList<Timer>) DB.timerDao().getAll();
                updateViewTimers();
            }
        }).start();
    }

    private void insertTimer (Timer timer) {
        final Timer new_timer = timer;
        new Thread(new Runnable(){
            @Override
            public void run(){
                DB.timerDao().insertAll(new_timer);
            }
        }).start();
    }

    private void updateTimer (Timer timer) {
        final Timer updt_timer = timer;
        new Thread(new Runnable(){
            @Override
            public void run(){
                DB.timerDao().update(updt_timer);
            }
        }).start();
    }

    private void deleteTimer (Timer timer) {
        final Timer dlt_timer = timer;
        new Thread(new Runnable(){
            @Override
            public void run(){
                DB.timerDao().delete(dlt_timer);
            }
        }).start();
    }

    private void resetAllTimers () {
        // set all to non running
        new Thread(new Runnable(){
            @Override
            public void run(){
                DB.timerDao().resetAll();
            }
        }).start();
    }

    private void deleteAllTimers () {
        new Thread(new Runnable(){
            @Override
            public void run(){
                DB.timerDao().deleteAll();
            }
        }).start();
    }
}
