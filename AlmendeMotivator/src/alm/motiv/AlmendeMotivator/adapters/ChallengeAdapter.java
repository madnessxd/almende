package alm.motiv.AlmendeMotivator.adapters;

import alm.motiv.AlmendeMotivator.R;
import alm.motiv.AlmendeMotivator.models.Challenge;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.model.GraphUser;

import java.util.ArrayList;

/**
 * Created by Kevin on 05/04/2014.
 */
public class ChallengeAdapter extends BaseAdapter {

    private ArrayList<Challenge> challenges;
    private LayoutInflater inflater;
    private Context context;

    public ChallengeAdapter(Activity context) {
        this.context = context;
        this.challenges = new ArrayList<Challenge>();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolder {
        TextView challengeTitle;
        TextView challengerName;
        Button viewChallenge;
    }

    public void setChallenges(ArrayList<Challenge> challenges) {
        this.challenges = challenges;
        notifyDataSetChanged();
    }

    public ArrayList<Challenge> getChallenges() {
        return this.challenges;
    }

    @Override
    public int getCount() {
        return challenges.size();
    }

    @Override
    public Object getItem(int i) {
        return challenges.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_challenge, null);

            viewHolder = new ViewHolder();
            viewHolder.challengeTitle = (TextView) convertView.findViewById(R.id.txtChallengeViewTitle);
            viewHolder.challengerName = (TextView) convertView.findViewById(R.id.txtChallengeViewName);

            convertView.setTag(viewHolder);
        }
        return convertView;
    }

    public ViewHolder setView(int arrayIndex, ViewHolder viewHolder) {
        String title = challenges.get(arrayIndex).getTitle();
        String challenger = challenges.get(arrayIndex).getChallenger();

        viewHolder.challengeTitle.setText("Challenge title: " + title);
        viewHolder.challengerName.setText("Challenger: " + challenger);

        return null;
    }
}
