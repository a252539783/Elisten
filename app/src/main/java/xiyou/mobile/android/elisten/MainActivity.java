package xiyou.mobile.android.elisten;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener
{

    public static final String ACTION="xiyou.mobile.android.elisten.fresh";
    public static final String ACTION_TYPE="type";

    public static final int ACTION_GET=0;
    public static final int ACTION_FRESH=1;

    public static final String GET_STATE="state";
    public static final String GET_SONG="song";
    public static final String GET_GESHOU="geshou";
    public static final String GET_MODE="mode";
    public static final String GET_GEDAN="gedan";

    private ExpandableListView gedan;
    private ImageButton create_gedan,next,pre,start,switch_mode;
    private DrawerLayout drawer;

    private SeekBar seekbar;
    private int progress=0;
    private TextView text_song,text_geshou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, PlayerService.class));
        setContentView(R.layout.activity_main);

        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1, new Notification.Builder(this).setContentText("aaaa").setTicker("aaaaa").setAutoCancel(true).setContentTitle("aaa").setContentIntent(PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),0)).getNotification());

        seekbar=(SeekBar)findViewById(R.id.seekBar);
        switch_mode=(ImageButton)findViewById(R.id.switch_mode);
        text_song=(TextView)findViewById(R.id.text_song);
        text_geshou=(TextView)findViewById(R.id.text_geshou);
        start=(ImageButton)findViewById(R.id.b_start);
        pre=(ImageButton)findViewById(R.id.b_prev);
        next=(ImageButton)findViewById(R.id.b_next);
        next.setOnClickListener(this);
        start.setOnClickListener(this);
        pre.setOnClickListener(this);
        switch_mode.setOnClickListener(this);
        seekbar.setOnSeekBarChangeListener(this);

        drawer = (DrawerLayout) findViewById(R.id.main_layout);
        create_gedan=(ImageButton)findViewById(R.id.button_new_gedan);
        create_gedan.setOnClickListener(this);
        gedan=(ExpandableListView)findViewById(R.id.list_gedan);
        gedan.setAdapter(new GedanAdapter(this));

        registerReceiver(new FreshReceiver(),new IntentFilter(ACTION));
        freshUI();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        freshUI();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.switch_mode:
                sendBroadcast(new Intent(PlayerService.ACTION).putExtra(PlayerService.ACTION_TYPE,PlayerService.ACTION_SETMODE));
                break;
            case R.id.button_new_gedan:
                break;
            case R.id.b_next:
                sendBroadcast(new Intent(PlayerService.ACTION).putExtra(PlayerService.ACTION_TYPE,PlayerService.ACTION_NEXT));
                break;
            case R.id.b_start:
                Intent i=new Intent(PlayerService.ACTION).putExtra(PlayerService.ACTION_TYPE,PlayerService.ACTION_PLAY);
                sendBroadcast(i);
                break;
            case R.id.b_prev:
                sendBroadcast(new Intent(PlayerService.ACTION).putExtra(PlayerService.ACTION_TYPE,PlayerService.ACTION_PREV));
                break;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.progress=progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Intent i=new Intent(PlayerService.ACTION).putExtra(PlayerService.ACTION_TYPE,PlayerService.ACTION_GOTO).putExtra(PlayerService.ARGS,progress);
        sendBroadcast(i);
    }

    private void freshUI()
    {
        sendBroadcast(new Intent(PlayerService.ACTION).putExtra(PlayerService.ACTION_TYPE,PlayerService.ACTION_GET));

    }


    private class FreshReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent i) {
            switch (i.getIntExtra(ACTION_TYPE,0))
            {
                case ACTION_FRESH:
                    seekbar.setProgress(i.getIntExtra(PlayerService.ARGS,0));
                    break;
                case ACTION_GET:
                    text_song.setText(i.getStringExtra(GET_SONG));
                    text_geshou.setText(i.getStringExtra(GET_GESHOU));

                    switch (i.getIntExtra(GET_MODE,0))
                    {
                        case PlayerService.MODE_CIRCLE:
                            switch_mode.setImageResource(R.drawable.circle);
                            break;
                        case PlayerService.MODE_NORMAL:
                            switch_mode.setImageResource(R.drawable.normal);
                            break;
                        case PlayerService.MODE_RAND:
                            switch_mode.setImageResource(R.drawable.rand);
                            break;
                        case PlayerService.MODE_SINGLECIRCLE:
                            switch_mode.setImageResource(R.drawable.scircle);
                            break;
                    }

                    if (i.getBooleanExtra(GET_STATE,true))
                    {
                        start.setImageResource(R.drawable.pause);
                    }
                    else
                    {
                        start.setImageResource(R.drawable.play);
                    }
                    break;


            }
            seekbar.setProgress(i.getIntExtra(PlayerService.ARGS,0));
            //drawer.invalidate();
        }
    }
}
