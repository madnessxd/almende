package alm.motiv.AlmendeMotivator.adapters;

import alm.motiv.AlmendeMotivator.R;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.model.GraphUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

/**
 * Created by thijs on 11/4/13.
 */
public class FriendsAdapter extends BaseAdapter {

    private ArrayList<GraphUser> models;
    private LayoutInflater inflater;
    private Context context;

    public FriendsAdapter(Activity context) {
        this.context = context;
        this.models = new ArrayList<GraphUser>();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolder {
        TextView friendName;
        ImageView friendImage;
    }

    public void removeModel(int position){
        this.models.remove(position);
        notifyDataSetChanged();
    }

    public void setModels(ArrayList<GraphUser> models) {
        this.models = models;
        notifyDataSetChanged();
    }

    public ArrayList<GraphUser> getModels() {
        return this.models;
    }

    @Override
    public int getCount() {
        return models.size();
    }


    @Override
    public GraphUser getItem(int position) {
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GraphUser model = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_friend, null);

            viewHolder = new ViewHolder();
            viewHolder.friendName = (TextView) convertView.findViewById(R.id.friendName);
            viewHolder.friendImage = (ImageView) convertView.findViewById(R.id.friendImage);

            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();

        String imgId = "https://graph.facebook.com/" + model.getId() + "/picture";

        // imgId = "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash1/t1.0-1/c0.33.200.200/p200x200/248489_10150308474960968_2461155_n.jpg";

        //Log.d("facebook", "url = " + imgId);

        Picasso.with(context).load(imgId).into(viewHolder.friendImage, new Callback() {
            @Override
            public void onSuccess() {
               // Log.d("facebook", "success = ");

            }

            @Override
            public void onError() {
               // Log.d("facebook", "error = ");


            }
        });

        viewHolder.friendName.setText(model.getName());

        return convertView;
    }
}