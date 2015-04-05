package com.manzana.rde.acontdowntimer;

import android.graphics.Paint;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;


public class TimerMainActivity extends ActionBarActivity {

    RdeSelectorView npHours;
    RdeSelectorView npMins;
    RdeSelectorView npSecs;
    Button btnStart;
    Button btnReset;
    Button btn3Min;
    Button btn5Min;
    Button btn10Min;
    IntentFilter filter;
    TextView tvTime;

    long initTime = 0;
    long interval = 0;
    boolean amRunning = false;

    private MyRequestReceiver receiver = null;
    private String chosenRingtone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        npHours = (RdeSelectorView) findViewById(R.id.npHours);
        npHours.setCaption("Hour");
        npMins = (RdeSelectorView) findViewById(R.id.npMins);
        npMins.setCaption("Min");
        npSecs = (RdeSelectorView) findViewById(R.id.npSecs);
        npSecs.setCaption("Sec");
        npSecs.setMax(59);
    //    npSecs.setWrapSelectorWheel(true);

        npMins.setMax(59);
        //   npMins.setWrapSelectorWheel(false);

        npHours.setMax(59);
        //    npHours.setWrapSelectorWheel(false);



        btn3Min = (Button) findViewById(R.id.btn3Min);
        btn5Min = (Button) findViewById(R.id.btn5Min);
        btn10Min = (Button) findViewById(R.id.btn10Min);
        btn3Min.setText("3 Min");
        btn5Min.setText("5 Min");

        btn3Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                npHours.setValue(0);
                npMins.setValue(3);
                npSecs.setValue(0);
            }
        });

        btn5Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                npHours.setValue(0);
                npMins.setValue(5);
                npSecs.setValue(0);
            }
        });

        btn10Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                npHours.setValue(0);
                npMins.setValue(10);
                npSecs.setValue(0);
            }
        });

        btnStart = (Button) findViewById(R.id.btnStart);
        btnReset = (Button) findViewById(R.id.btnReset);



        tvTime = (TextView) findViewById(R.id.tvTime);
        correctWidth(tvTime, 0.81);
    }

    private String convertTimetoStr(long atime) {
        int secs = (int) (atime / 1000);
        int mins = secs / 60;
        int hours = mins / 60;
       // hours = hours % 60;
        mins = mins % 60;
        secs = secs % 60;
        int msecs = (int)atime % 1000;
        int csecs = msecs / 10;
        return String.format("%02d:%02d:%02d.%02d", hours, mins, secs, csecs);
    }

    private long convertStrToTime(String atime) {
        String bigtime = atime;
        long aresult = 0;
        int apos =  atime.indexOf(':');
        if (apos == -1) {
            return aresult;
        }
        String asuby = bigtime.substring(0, apos);
        aresult = Integer.parseInt(asuby) * 3600;
        bigtime = bigtime.substring(apos + 1);
        apos = bigtime.indexOf(':');
        if (apos == -1) {
            return aresult;
        }
        asuby = bigtime.substring(0, apos);
        aresult = aresult + Integer.parseInt(asuby) * 60;
        bigtime = bigtime.substring(apos + 1);
        aresult = aresult + Integer.parseInt(bigtime);

        return aresult * 1000;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer_main, menu);
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


            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");

            if (chosenRingtone.isEmpty())
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
            else
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(chosenRingtone));
            this.startActivityForResult(intent, 5);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                this.chosenRingtone = uri.toString();
            }
            else
            {
                this.chosenRingtone = "";
            }


            SharedPreferences sharedPreferences = this.getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("chosenRingtone", this.chosenRingtone);
            editor.commit();


        }

        if ((this.initTime + this.interval < SystemClock.elapsedRealtime() ) && (amRunning)) {
            amRunning = false;
            this.btnStart.setText("Start");
            this.btnReset.setText("Reset");
            tvTime.setText("00:00:00");
        }
        btn10Min.setEnabled(!amRunning);
        btn3Min.setEnabled(!amRunning);
        btn5Min.setEnabled(!amRunning);
        npSecs.setEnabled(!amRunning);
        npHours.setEnabled(!amRunning);
        npMins.setEnabled(!amRunning);


    }

    public void startChronometer(View view) {
        Resources res = getResources();
        Button abutton = (Button) view;
        String aCaption = abutton.getText().toString();
        if (aCaption.equalsIgnoreCase(res.getString(R.string.Start ))) {
            btnStart.setText(res.getString(R.string.Pause));
            btnReset.setText(res.getString(R.string.Cancel) );

            btn10Min.setEnabled(false);
            btn3Min.setEnabled(false);
            btn5Min.setEnabled(false);
            npSecs.setEnabled(false);
            npHours.setEnabled(false);
            npMins.setEnabled(false);

            String atime = String.format("%02d:%02d:%02d", npHours.getValue(), npMins.getValue(), npSecs.getValue());

            this.interval = convertStrToTime(atime);
            this.initTime = SystemClock.elapsedRealtime();
            Intent msgIntent = new Intent(TimerMainActivity.this, TimerService.class);
            msgIntent.putExtra(TimerService.INTERVAL, this.interval);
            startService(msgIntent);
            this.amRunning = true;
        } else if (aCaption.equalsIgnoreCase(res.getString(R.string.Pause )))  {
            btnStart.setText(res.getString(R.string.Resume));

            //Intent broadcastIntent = new Intent();
            //broadcastIntent.setAction(TimerService.BROADCAST_STOP_ACTION );
            //broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            //broadcastIntent.putExtra(TimerService.STOP_STRING, TimerService.STOP_STRING);
            //sendBroadcast(broadcastIntent);
            stopService(new Intent(this, TimerService.class));
            this.amRunning = false;


            stopService(new Intent(this, TimerService.class));
        } else if (aCaption.equalsIgnoreCase(res.getString(R.string.Resume )))  {
            btnStart.setText(res.getString(R.string.Pause));

            this.interval = convertStrToTime(tvTime.getText().toString());
            this.initTime = SystemClock.elapsedRealtime();
            Intent msgIntent = new Intent(TimerMainActivity.this, TimerService.class);
            msgIntent.putExtra(TimerService.INTERVAL, this.interval);
            startService(msgIntent);
            this.amRunning = true;


        }




    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.manzana.rde.countdowntimer.TimerService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void stopChronometer(View view) {
        btn10Min.setEnabled(true);
        btn3Min.setEnabled(true);
        btn5Min.setEnabled(true);
        npSecs.setEnabled(true);
        npHours.setEnabled(true);
        npMins.setEnabled(true);
        Resources res = getResources();
        Button abutton = (Button) view;
        String aCaption = abutton.getText().toString();
        if (aCaption.equalsIgnoreCase(res.getString(R.string.Cancel ))) {
            btnStart.setText(res.getString(R.string.Start));
            this.amRunning = false;
            initTime = 0;
            interval = 0;
            abutton.setText(res.getString(R.string.Reset));
//            Intent broadcastIntent = new Intent();
//            broadcastIntent.setAction(TimerService.BROADCAST_STOP_ACTION);
//            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
//            broadcastIntent.putExtra(TimerService.STOP_STRING, TimerService.STOP_STRING);
//            sendBroadcast(broadcastIntent);
            stopService(new Intent(this, TimerService.class));
            Intent msgIntent = new Intent(TimerMainActivity.this, TimerService.class);
            msgIntent.putExtra(TimerService.INTERVAL, 0L);
            startService(msgIntent);

            tvTime.setText("00:00:00");
        } else if (aCaption.equalsIgnoreCase(res.getString(R.string.Reset ))) {
            npHours.setValue(0);
            npMins.setValue(0);
            npSecs.setValue(0);
            tvTime.setText("00:00:00");

        }

    }


    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        saveAll();
        try {
            if (receiver != null) {
                unregisterReceiver(receiver);
                receiver = null;
            }
        } catch  (IllegalArgumentException e){

        }catch (Exception e) {
            // TODO: handle exception
        }


    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        receiver = new MyRequestReceiver();
        filter = new IntentFilter(TimerService.BROADCAST_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);
        getAll();



        }

    private void getAll() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        this.chosenRingtone = sharedPreferences.getString("chosenRingtone", "");
        this.initTime = sharedPreferences.getLong("initTime", 0);
        this.interval = sharedPreferences.getLong("interval", 0);
        this.amRunning = sharedPreferences.getBoolean("amRunning", false);

        this.npHours.setValue(sharedPreferences.getInt("npHours", 0));
        this.npMins.setValue(sharedPreferences.getInt("npMins", 0));
        this.npSecs.setValue(sharedPreferences.getInt("npSecs", 0));
        this.btnStart.setText(sharedPreferences.getString("btnStart", "Start"));
        this.btnReset.setText(sharedPreferences.getString("btnReset", "Reset")  );
        if (!amRunning)
            tvTime.setText(sharedPreferences.getString("tvTime","00:00:00" ) );

        if (!amRunning)
            tvTime.setText(sharedPreferences.getString("tvTime","00:00:00" )  );

        if ((this.initTime + this.interval < SystemClock.elapsedRealtime() ) && (amRunning)) {
            amRunning = false;
            this.btnStart.setText("Start");
            this.btnReset.setText("Reset");
            tvTime.setText("00:00:00");
        }
        btn10Min.setEnabled(!amRunning);
        btn3Min.setEnabled(!amRunning);
        btn5Min.setEnabled(!amRunning);
        npSecs.setEnabled(!amRunning);
        npHours.setEnabled(!amRunning);
        npMins.setEnabled(!amRunning);


    }


    private void saveAll() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit().clear();
        editor.putString("tvTime", this.tvTime.getText().toString());
        editor.putLong("initTime", this.initTime);
        editor.putLong("interval", this.interval);
        editor.putBoolean("amRunning", this.amRunning);
        editor.putInt("npHours", this.npHours.getValue());
        editor.putInt("npMins", this.npMins.getValue());
        editor.putInt("npSecs", this.npSecs.getValue());
        editor.putString("btnStart", this.btnStart.getText().toString());
        editor.putString("btnReset", this.btnReset.getText().toString() );
        editor.putString("chosenRingtone", this.chosenRingtone);

        editor.putString("tvTime", this.tvTime.getText().toString());

        editor.commit();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class MyRequestReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String updateString = intent.getStringExtra(TimerService.UPDATE_STRING );
            String finalMessage = intent.getStringExtra(TimerService.FINAL_STRING);
            if (updateString != null)
                tvTime.setText(updateString);

            if (finalMessage != null) {
                amRunning = false;
                btnStart.setText("Start");
                btnReset.setText("Reset");
                tvTime.setText("00:00:00");
                btn10Min.setEnabled(true);
                btn3Min.setEnabled(true);
                btn5Min.setEnabled(true);
                npSecs.setEnabled(true);
                npHours.setEnabled(true);
                npMins.setEnabled(true);

            }



        }


    }

    public void correctWidth(TextView textView, double afactor)
    {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float dpHeight = displayMetrics.heightPixels ;
        //float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels ;
        float awidth = dpWidth;
        if (awidth > dpHeight) {
        //    awidth = dpHeight;
            awidth = awidth / 2;
        }
        int desiredWidth =   (int) (afactor *  awidth);
        Paint paint = new Paint();
        Rect bounds = new Rect();

        paint.setTypeface(textView.getTypeface());
        float textSize = textView.getTextSize();
        paint.setTextSize(textSize);
        String text = textView.getText().toString();
        paint.getTextBounds(text, 0, text.length(), bounds);

        while (bounds.width() > desiredWidth)
        {
            textSize--;
            paint.setTextSize(textSize);
            paint.getTextBounds(text, 0, text.length(), bounds);
        }
        while (bounds.width() < desiredWidth)
        {
            textSize++;
            paint.setTextSize(textSize);
            paint.getTextBounds(text, 0, text.length(), bounds);
        }

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);


    }



}
