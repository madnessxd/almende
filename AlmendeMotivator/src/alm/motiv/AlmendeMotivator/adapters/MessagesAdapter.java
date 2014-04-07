package alm.motiv.AlmendeMotivator.adapters;

import alm.motiv.AlmendeMotivator.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Gebruiker on 6-4-14.
 */
public class MessagesAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private Context context;

    public MessagesAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        //return mThumbIds.length;
        return 1;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {  // if it's not recycled, initialize some attributes

        }
        return convertView;
    }
}