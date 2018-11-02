package click.crausaz.andoidmultitimer;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class TimerService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private String timer_name;
    private String timer_time;

    private Handler customHandler = new Handler();
    private long start_time = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO: try to check if running

            customHandler.postDelayed(updateTimerThread, 0);

            // once timer is done, kill process, open alert reset icon and time (update db)
            // stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        timer_name = intent.getStringExtra("timer_name");
        timer_time = intent.getStringExtra("timer_time");

        start_time = SystemClock.uptimeMillis();

        mServiceHandler.handleMessage(msg);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
        Toast.makeText(this, "Paused / Done", Toast.LENGTH_SHORT).show();
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {
            int full_time_seconds = getSecondsFromTime();
            timeInMilliseconds = SystemClock.uptimeMillis() - start_time;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);

            if (secs == full_time_seconds) {
                // Timer is Over
                Log.i("Over", "DONE");
                Thread.currentThread().interrupt();
            }
            Log.i("Seconds", secs + "");
            customHandler.postDelayed(this, 1000);
        }
    };

    private int getSecondsFromTime () {
        String[] hourMinSecs = timer_time.split(":");
        int hour = Integer.parseInt(hourMinSecs[0]);
        int mins = Integer.parseInt(hourMinSecs[1]);
        int secs = Integer.parseInt(hourMinSecs[2]);
        int totalSeconds = (hour * 60 * 60) + (mins * 60) + secs ;
        return totalSeconds;
    }

    private String getTimeFromSeconds (int seconds) {

        return null;
    }
}
