package xiyou.mobile.android.elisten;

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

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener
{

    public static final String ACTION="xiyou.mobile.android.elisten.fresh";

    ExpandableListView gedan;
    private ImageButton create_gedan,next,pre,start;
    private SeekBar seekbar;
    private int progress=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, PlayerService.class));
        setContentView(R.layout.activity_main);

        seekbar=(SeekBar)findViewById(R.id.seekBar);
        start=(ImageButton)findViewById(R.id.b_start);
        pre=(ImageButton)findViewById(R.id.b_prev);
        next=(ImageButton)findViewById(R.id.b_next);
        next.setOnClickListener(this);
        start.setOnClickListener(this);
        pre.setOnClickListener(this);
        seekbar.setOnSeekBarChangeListener(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        create_gedan=(ImageButton)findViewById(R.id.button_new_gedan);
        create_gedan.setOnClickListener(this);
        gedan=(ExpandableListView)findViewById(R.id.list_gedan);
        gedan.setAdapter(new GedanAdapter(this));

        registerReceiver(new FreshReceiver(),new IntentFilter("xiyou.mobile.android.elisten.fresh"));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_new_gedan:
                break;
            case R.id.b_next:
                break;
            case R.id.b_start:
                Intent i=new Intent(PlayerService.ACTION).putExtra(PlayerService.ACTION_TYPE,PlayerService.ACTION_PLAY);
                sendBroadcast(i);
                break;
            case R.id.b_prev:
                break;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.progress=progress;
        Log.e("aaa",""+progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Intent i=new Intent(PlayerService.ACTION).putExtra(PlayerService.ACTION_TYPE,PlayerService.ACTION_GOTO).putExtra(PlayerService.ARGS,progress);
        sendBroadcast(i);
    }

    private class FreshReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            seekbar.setProgress(intent.getIntExtra("args",0));
        }
    }
}
