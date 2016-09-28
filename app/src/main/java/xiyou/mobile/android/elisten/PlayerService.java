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

/**
 * Created by user on 2016/9/26.
 */
public class PlayerService extends Service implements MediaPlayer.OnErrorListener{

    public static final String ACTION="xiyou.mobile.android.elisten.player";
    public static final String ACTION_TYPE="action_type";
    public static final String ARGS="args";

    public static final int ACTION_PLAY=0;
    public static final int ACTION_STOP=2;
    public static final int ACTION_GOTO=3;

    private MediaPlayer mp;
    private String currentSong="";
    private CmdReceiver cr;
    private boolean running=false;

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
        Log.e("aa",mp.toString());
        return false;
    }

    private class CmdReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent i) {

            switch (i.getIntExtra(ACTION_TYPE,0))
            {
                case ACTION_PLAY:
                    if (currentSong.equals(i.getStringExtra(ARGS)))
                    {
                        if (mp.isPlaying())
                        {
                            mp.pause();
                        }else
                        {
                            mp.start();
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
                        currentSong=i.getStringExtra(ARGS);
                    }
            }

        }
    }
}
