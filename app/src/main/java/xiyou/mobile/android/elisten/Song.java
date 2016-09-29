package xiyou.mobile.android.elisten;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by user on 2016/9/28.
 */
public class Song implements Serializable {
    public String name,songer,path;

    public Song(String name,String songer,String path)
    {
        this.name=name;
        this.songer=songer;
        this.path=path;
    }

}
