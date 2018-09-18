package click.crausaz.andoidmultitimer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Home extends AppCompatActivity {

    private Context app_context;
    private final String timers_data_name = "timers_data.json";
    private JSONObject json_timers;
    private String timers_json_string = "";
    private FilesHelpers file_helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init components
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ListView list = findViewById(R.id.list_timers);
        setSupportActionBar(toolbar);
        initAddButton();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        app_context = getApplicationContext();
        file_helper = new FilesHelpers();

        getTimers();
        // writeNewTimer();

        Log.i("timers:", json_timers.toString());

        // convert JSON to ArrayAdapter
        ArrayList<String> list_timers = new ArrayList<>();
        JSONArray timers_array = null;
        try {
            timers_array = json_timers.getJSONArray("timers");
            if (timers_array != null) {
                for (int i = 0; i < timers_array.length(); i++){
                    list_timers.add(timers_array.getString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        list.setAdapter(new ArrayAdapter<>(this, R.layout.timer, list_timers));
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
            public void onClick(View view) {
                Snackbar.make(view, "Button clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void triggerTimer (View view) {
        Log.i("click", "clickefoasdbopfbsioudfg");
    }

    private void writeNewTimer() {
        // append new object
        // get file content
        try {
            timers_json_string = file_helper.getFileContentJSONString(app_context, timers_data_name);
            JSONObject jsonObject = new JSONObject(timers_json_string);
            file_helper.writeJSONInFile(app_context, timers_data_name, jsonObject);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Timer new_timer = new Timer("timer1", "00:00:30", "00:00:30");
        JSONParser parser = new JSONParser();
        //
        //JSONObject json = (JSONObject) parser.parse(stringToParse);

        JSONObject obj = new JSONObject();
        try {
            obj.putOpt("name", "timer1");
            obj.putOpt("full_time", "00:00:30");
            obj.putOpt("actual_time", "00:00:30");
            // file_helper.writeJSONInFile(app_context, timers_data_name, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTimers() {
        JSONObject main = new JSONObject();
        JSONArray timers = new JSONArray();

        JSONObject timer1 = new JSONObject();
        JSONObject timer2 = new JSONObject();

        try {
            timer1.put("name", "test1");
            timer1.put("full_time", "00:00:30");
            timer1.put("actual_time", "00:00:30");
            timer2.put("name", "test2");
            timer2.put("full_time", "00:01:30");
            timer2.put("actual_time", "00:01:30");
            timers.put(timer1);
            timers.put(timer2);
            main.put("timers", timers);
            json_timers = main;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*
        try {
            timers_json_string = file_helper.getFileContentJSONString(app_context, timers_data_name);
        } catch (IOException e) {
            try {
                // create a new file and put json base structure
                file_helper.createNewFile(app_context, timers_data_name);
                // JSONObject initer = new JSONObject();
                // JSONArray timers_array = new JSONArray();
                // initer.put("timers", timers_array);
                // FileWriter fileWriter = new FileWriter(app_context.getFilesDir() + "/" + timers_data_name);
                // fileWriter.write(initer.toString());
                // fileWriter.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        */
    }
}
