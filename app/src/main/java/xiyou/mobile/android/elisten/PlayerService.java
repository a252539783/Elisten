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
import java.util.Random;


public class PlayerService extends Service implements MediaPlayer.OnCompletionListener,Runnable{

    public static final String ACTION="xiyou.mobile.android.elisten.player";
    public static final String ACTION_TYPE="action_type";
    public static final String ARGS="args";
    public static final String GEDAN="gedan";

    public static final int ACTION_GET=5;

    public static final int ACTION_PLAY=0;
    public static final int ACTION_SETMODE=1;
    public static final int ACTION_GOTO=2;
    public static final int ACTION_NEXT=3;
    public static final int ACTION_PREV=4;

    public static final int MODE_SINGLE=0;
    public static final int MODE_SINGLECIRCLE=1;
    public static final int MODE_CIRCLE=2;
    public static final int MODE_NORMAL=3;
    public static final int MODE_RAND=4;

    private MediaPlayer mp;
    private int currentSong=-1;
    private ArrayList songs;
    private CmdReceiver cr;
    private int fps=0;
    private int play_mode=3;
    private boolean running=false;
    private Random rand;

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
        rand=new Random(System.currentTimeMillis());
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
        switch (play_mode)
        {
            case MODE_CIRCLE:
                if (currentSong==songs.size()-1)
                {
                    currentSong=0;
                }
                else
                {
                    currentSong++;
                }
                loadSong();
                startSong();
                break;
            case MODE_SINGLE:
                mp.seekTo(0);
                mp.pause();
                break;
            case MODE_SINGLECIRCLE:
                mp.seekTo(0);
                break;
            case MODE_RAND:
                currentSong=rand.nextInt()%songs.size();
                loadSong();
                startSong();
                break;
            case MODE_NORMAL:
                if (currentSong==songs.size()-1)
                {
                    currentSong=0;
                    loadSong();
                }else
                {
                    currentSong++;
                    loadSong();
                    startSong();
                }
                break;
        }
        freshUI();
    }


    private class CmdReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent i) {

            switch (i.getIntExtra(ACTION_TYPE,0))
            {
                case ACTION_GET:
                    break;
                case ACTION_PLAY:
                    if (i.getIntExtra(ARGS,-2)==-2||currentSong==i.getIntExtra(ARGS,-2))
                    {
                        if (mp.isPlaying())
                        {
                            mp.pause();
                        }else
                        {
                            startSong();
                        }
                    }else
                    {

                        currentSong=i.getIntExtra(ARGS,-2);
                        songs=i.getParcelableArrayListExtra(GEDAN);
                        loadSong();
                        fps=mp.getDuration()/300;
                        startSong();
                    }
                    break;
                case ACTION_GOTO:
                    if (currentSong!=-1)
                    {
                        mp.seekTo(mp.getDuration()*i.getIntExtra(ARGS,0)/100);
                    }
                    break;
                case ACTION_SETMODE:
                    if (play_mode==4)
                        play_mode=1;
                    else
                        play_mode++;
                    break;
                case ACTION_NEXT:
                    next(1);
                    break;
                case ACTION_PREV:
                    next(-1);
                    break;
            }

            freshUI();

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

    private void loadSong()
    {
        mp.reset();
        try {
            mp.setDataSource(((Song)songs.get(currentSong)).path);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void next(int n)
    {
        if (play_mode==MODE_RAND)
        {
            currentSong=rand.nextInt()%songs.size();
        }
        else
        {
            if (currentSong==songs.size()-1)
            {
                currentSong=0;
            }
            else
            {
                currentSong+=n;
            }
        }

        loadSong();
        startSong();
    }

    private void startSong()
    {
        mp.start();
        if (!fresh)
            new Thread(this).start();
    }

    private void freshUI()
    {
        if (currentSong!=-1)
        sendBroadcast(new Intent(MainActivity.ACTION).putExtra(MainActivity.ACTION_TYPE,MainActivity.ACTION_GET).putExtra(MainActivity.GET_SONG,((Song)songs.get(currentSong)).name).putExtra(MainActivity.GET_GESHOU,((Song)songs.get(currentSong)).songer).putExtra(MainActivity.GET_STATE,mp.isPlaying()).putExtra(MainActivity.GET_MODE,play_mode));
    }
}
