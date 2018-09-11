package click.crausaz.andoidmultitimer;

import android.content.Context;
import android.os.Bundle;
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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

public class Home extends AppCompatActivity {

    private final String[] movies = new String[] {
            "Independence day",
            "The fifth element",
            "The last samurai"
    };
    private Context app_context;
    private final String timers_data_name = "timers_data.json";
    private String timers_json_string = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Button clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        app_context = getApplicationContext();

        ListView list = findViewById(R.id.list_timers);
        getTimers();

        list.setAdapter(new ArrayAdapter<>(this, R.layout.timer, Collections.singletonList(timers_json_string)));
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
            return true;
        }

        if (id == R.id.action_resetall) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void writeNewTimer() {
        // append new object
    }

    private void getFileContent() {
        try {
            Object obj = new JSONParser().parse(new FileReader(app_context.getFilesDir() + "/" + timers_data_name));
            Log.w("data", obj.toString());
            Log.w("data", "test");
        } catch (IOException e) {
            try {
                // create a new file and put json base structure
                new File(app_context.getFilesDir(), timers_data_name).createNewFile();
                JSONObject initer = new JSONObject();
                JSONArray timers_array = new JSONArray();
                initer.put("timers", timers_array);
                FileWriter fileWriter = new FileWriter(app_context.getFilesDir() + "/" + timers_data_name);
                fileWriter.write(initer.toString());
                fileWriter.close();

            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    private void getTimers() {
        // try to open the data file
        // FileOutputStream outputStream;
        // outputStream = openFileOutput(timers_data_name, app_context.MODE_PRIVATE);
        // outputStream.close();
        // get values
        getFileContent();
    }
}
