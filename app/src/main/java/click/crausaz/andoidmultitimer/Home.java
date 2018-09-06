package click.crausaz.andoidmultitimer;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

public class Home extends AppCompatActivity {

    private final String[] movies = new String[] {
            "Independence day",
            "The fifth element",
            "The last samurai"
    };
    private Context app_context;
    private String timers_data_name = "timers_data.json";

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
        writeNewTimer();

        list.setAdapter(new ArrayAdapter<String>(this, R.layout.timer, movies));
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
        JSONObject obj = new JSONObject();
        String content = Files.rea(timers_data_name);

        try {
            obj.put("Name", "Timer1");
            obj.put("Time", "00:00:05");

            FileOutputStream outputStream;
            outputStream = openFileOutput(timers_data_name, app_context.MODE_PRIVATE);
            outputStream.write(obj.toString().getBytes());
            outputStream.close();

        } catch (Exception e) {

        }

    }

    private void getTimers() {
        try {
            // try to open the data file
            FileOutputStream outputStream;
            outputStream = openFileOutput(timers_data_name, app_context.MODE_PRIVATE);
            outputStream.close();
        } catch (Exception e) {
            // error, create a new file
            new File(app_context.getFilesDir(), timers_data_name);
        }
    }
}
