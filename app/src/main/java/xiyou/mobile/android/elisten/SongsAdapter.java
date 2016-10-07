package xiyou.mobile.android.elisten;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by user on 2016/9/26.
 */
public class SongsAdapter extends BaseAdapter implements Runnable,View.OnClickListener{

    private int count=0;
    private Context context;
    private LayoutInflater ll;
    private ArrayList<Song> songs;
    private ArrayList<View> list;
    private String gedan=null;

    public SongsAdapter(Context c,String gedan)
    {
        context=c;
        this.gedan=gedan;
        ll=LayoutInflater.from(c);
        list=new ArrayList<>();
    }

    public SongsAdapter(Context c)
    {
        context=c;
        ll=LayoutInflater.from(c);
        list=new ArrayList<>();
        new Thread(this).start();
    }

    @Override
    public void run() {
        songs=new ArrayList<>();
        Cursor c=context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA},
                "_size>500000",null,null);
        while (c.moveToNext())
        {
            songs.add(new Song(c.getString(0),c.getString(1),c.getString(2)));
            count++;
        }
        c.close();
        context.sendBroadcast(new Intent(PlayerService.ACTION).putExtra(PlayerService.ACTION_TYPE,PlayerService.ACTION_INIT).putExtra(PlayerService.GEDAN,songs));
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (list.size()>position)
            convertView=list.get(position);
        else {
            convertView = ll.inflate(R.layout.item_song, parent, false);
            SongsView sv = new SongsView(convertView.findViewById(R.id.text_song_name), convertView.findViewById(R.id.text_song_geshou));
            convertView.setTag(sv);
            sv.name.setText(songs.get(position).name);
            sv.songer.setText(songs.get(position).songer);
            convertView.setId(position);
            convertView.setOnClickListener(this);
            list.add(convertView);
        }

        return convertView;
    }

    private static class SongsView
    {
        public TextView name,songer;

        public SongsView(View name,View songer)
        {
            this.name=(TextView)name;
            this.songer=(TextView)songer;
        }
    }


    @Override
    public void onClick(View v) {
        Intent i=new Intent(PlayerService.ACTION).putExtra(PlayerService.ACTION_TYPE,PlayerService.ACTION_PLAY).putExtra(PlayerService.ARGS, v.getId());
        context.sendBroadcast(i);
    }
}
