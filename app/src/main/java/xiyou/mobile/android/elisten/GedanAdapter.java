package xiyou.mobile.android.elisten;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by user on 2016/9/26.
 */
public class GedanAdapter extends BaseExpandableListAdapter {

    private int count=1;
    private Context context;
    private Object[] gedans;
    private LayoutInflater ll;
    private ArrayList<SongsAdapter> child;

    public GedanAdapter(Context c)
    {
        context=c;
        ll=LayoutInflater.from(c);
        child=new ArrayList<>();
        init();
    }

    private void init()
    {
        SharedPreferences settings=context.getSharedPreferences("set", Context.MODE_APPEND);
        if (settings.contains("gedan"))
        {
            gedans=settings.getStringSet("gedan",new HashSet<String>()).toArray();
        }else
        {
            Set g=new HashSet<>();
            g.add("本地列表");
            settings.edit().putStringSet("gedan",g).commit();
            gedans=g.toArray();
        }

        count=gedans.length;
    }

    @Override
    public int getGroupCount() {
        return count;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return child.get(groupPosition).getCount();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView==null)
        {
            convertView=ll.inflate(R.layout.item_gedan,parent,false);
            GedanView gv=new GedanView(convertView.findViewById(R.id.text_gedan_name),convertView.findViewById(R.id.text_gedan_num));
            convertView.setTag(gv);
            child.add(new SongsAdapter(context));
            gv.name.setText((String)gedans[groupPosition]);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        return child.get(groupPosition).getView(childPosition,convertView,parent);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private static class GedanView
    {
        public TextView name,num;

        public GedanView(View name,View num)
        {
            this.name=(TextView)name;
            this.num=(TextView)num;
        }
    }
}
