package xiyou.mobile.android.elisten;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by user on 2016/9/26.
 */
public class PlayerService extends Service implements MediaPlayer.OnErrorListener{

    public static final String ACTION="xiyou.mobile.android.elisten.player";
    public static final String ACTION_TYPE="action_type";
    public static final String ARGS="args";
    public static final String GEDAN="gedan";

    public static final int ACTION_PLAY=0;
    public static final int ACTION_STOP=1;
    public static final int ACTION_GOTO=2;
    public static final int ACTION_NEXT=3;
    public static final int ACTION_PREV=4;

    public static final int MODE_SINGLE=0;

    private MediaPlayer mp;
    private String currentSong="";
    private ArrayList<Song> songs;
    private CmdReceiver cr;
    private int fps=0;
    private int play_mode=0;
    private boolean running=false;

    private boolean uiAlive=true,fresh=false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (running)
            return super.onStartCommand(intent, flags, startId);

        running=true;
        IntentFilter filter=new IntentFilter();

        mp=new MediaPlayer();
        filter.addAction("xiyou.mobile.android.elisten.player");
        cr=new CmdReceiver();
        registerReceiver(cr, filter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("aa", mp.toString());
        return false;
    }



    private class CmdReceiver extends BroadcastReceiver implements Runnable
    {

        @Override
        public void onReceive(Context context, Intent i) {

            switch (i.getIntExtra(ACTION_TYPE,0))
            {
                case ACTION_PLAY:
                    if (i.getStringExtra(ARGS)==null||currentSong.equals(i.getStringExtra(ARGS)))
                    {
                        if (mp.isPlaying())
                        {
                            mp.pause();
                        }else
                        {
                            mp.start();
                            if (!fresh)
                                new Thread(this).start();
                        }
                    }else
                    {
                        mp.reset();
                        try {
                            mp.setDataSource(i.getStringExtra(ARGS));
                            mp.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mp.start();
                        fps=mp.getDuration()/300;
                        if (!fresh)
                            new Thread(this).start();
                        currentSong=i.getStringExtra(ARGS);
                        songs=i.getParcelableExtra(GEDAN);
                    }
                    break;
                case ACTION_GOTO:
                    if (mp.isPlaying())
                    {
                        mp.seekTo(mp.getDuration()*i.getIntExtra(ARGS,0)/100);
                    }
                    break;
            }

        }

        @Override
        public void run() {
            fresh=true;
            while (mp.isPlaying())
            {
                sendBroadcast(new Intent(MainActivity.ACTION).putExtra(ARGS,(int)((float)mp.getCurrentPosition()/mp.getDuration()*100)));

                try {
                    Thread.sleep(fps);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            fresh=false;
        }
    }
}
