package alm.motiv.AlmendeMotivator.adapters;

import alm.motiv.AlmendeMotivator.ChallengeViewActivity;
import alm.motiv.AlmendeMotivator.ChallengesMenuActivity;
import alm.motiv.AlmendeMotivator.Database;
import alm.motiv.AlmendeMotivator.R;
import alm.motiv.AlmendeMotivator.models.Challenge;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Kevin on 05/04/2014.
 */
public class ChallengeAdapter extends BaseAdapter implements Serializable {

    private ArrayList<Challenge> challenges;
    private LayoutInflater inflater;
    private Context context;

    private String getChallengee;
    private String getChallenger;

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

    public Challenge getChallengesPosition(int i) {
        return this.challenges.get(i);
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

    class DatabaseThread extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection userCollection = db.getCollection("user");
            userCollection.setObjectClass(User.class);

            String returnString = "";

            Session session = Session.getActiveSession();
            Request request = new Request(session, args[0], null, HttpMethod.GET);
            Response response = request.executeAndWait();

            if (response.getError() != null) {
                System.out.println("NOPE");
            } else {
                GraphUser graphUser = response.getGraphObjectAs(GraphUser.class);
                returnString = graphUser.getName();
            }
            return returnString;
        }
    }

    private String getNameThroughID(String id){
        String name = "a";

        DatabaseThread dbt = new DatabaseThread();
        try {
            name = dbt.execute(id).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return name;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Challenge currentChallenge = getChallengesPosition(position);

        ViewHolder viewHolder = new ViewHolder();
        convertView = inflater.inflate(R.layout.list_item_challenge, null);

        viewHolder.challengeTitle = (TextView) convertView.findViewById(R.id.txtChallengeViewTitle);
        viewHolder.challengerName = (TextView) convertView.findViewById(R.id.txtChallengeViewName);
        viewHolder.viewChallenge = (Button) convertView.findViewById(R.id.btnViewChallenge);
        convertView.setTag(viewHolder);

        viewHolder.challengeTitle.setText(currentChallenge.getTitle());

        viewHolder.challengerName.setText(getNameThroughID(currentChallenge.getChallengee()));

        viewHolder.viewChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open the challengeViewActivity and give the current selected Challenge to the activity
                Intent intent = new Intent(context, ChallengeViewActivity.class);
                //TODO This works as a cheap workaround because I can't send a Serializable object. Fix
                intent.putExtra("title", currentChallenge.getTitle());
                intent.putExtra("challenger", getNameThroughID(currentChallenge.getChallenger()));
                intent.putExtra("challengee", getNameThroughID(currentChallenge.getChallengee()));
                intent.putExtra("content", currentChallenge.getContent());
                intent.putExtra("evidenceAmount", currentChallenge.getEvidenceAmount());
                intent.putExtra("evidenceType", currentChallenge.getEvidenceType());
                intent.putExtra("reward", currentChallenge.getReward());
                intent.putExtra("status", currentChallenge.getStatus());
                intent.putExtra("id", currentChallenge.getID().toString());
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
