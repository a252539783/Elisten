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
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener{

    public static final String ACTION="xiyou.mobile.android.elisten.player";
    public static final String ACTION_TYPE="action_type";
    public static final String ARGS="args";
    public static final String GEDAN="gedan";

    public static final int ACTION_GET=5;

    public static final int ACTION_PLAY=0;
    public static final int ACTION_STOP=1;
    public static final int ACTION_GOTO=2;
    public static final int ACTION_NEXT=3;
    public static final int ACTION_PREV=4;

    public static final int MODE_SINGLE=0;

    private MediaPlayer mp;
    private int currentSong=-1;
    private ArrayList songs;
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
        mp.setOnCompletionListener(this);
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
    public void onCompletion(MediaPlayer mp) {
    }


    private class CmdReceiver extends BroadcastReceiver implements Runnable
    {

        @Override
        public void onReceive(Context context, Intent i) {

            switch (i.getIntExtra(ACTION_TYPE,0))
            {
                case ACTION_GET:
                    if (mp.isPlaying())
                    sendBroadcast(new Intent(MainActivity.ACTION).putExtra(MainActivity.ACTION_TYPE,MainActivity.ACTION_GET).putExtra(MainActivity.GET_SONG,((Song)songs.get(currentSong)).name));
                    break;
                case ACTION_PLAY:
                    if (i.getIntExtra(ARGS,-2)==-2||currentSong==i.getIntExtra(ARGS,-2))
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

                        currentSong=i.getIntExtra(ARGS,-2);
                        songs=i.getParcelableArrayListExtra(GEDAN);
                        mp.reset();
                        try {
                            mp.setDataSource(((Song)songs.get(i.getIntExtra(ARGS,0))).path);
                            mp.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mp.start();
                        fps=mp.getDuration()/300;
                        if (!fresh)
                            new Thread(this).start();

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
                sendBroadcast(new Intent(MainActivity.ACTION).putExtra(MainActivity.ACTION_TYPE,MainActivity.ACTION_FRESH).putExtra(ARGS,(int)((float)mp.getCurrentPosition()/mp.getDuration()*100)));

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
