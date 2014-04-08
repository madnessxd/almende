package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.models.Challenge;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.mongodb.*;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Kevin on 02/04/2014.
 */
public class ChallengeCreateActivity extends Activity {


    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String PICTURE = "picture";
    private static final String FIELDS = "fields";

    private static final String REQUEST_FIELDS = TextUtils.join(",", new String[]{ID, NAME, PICTURE});

    //Layout variables
    private Button btnCreateChallenge;
    private Spinner spinnerAmount;
    private Spinner spinnerType;
    private EditText textTitle;
    private EditText textContent;
    private EditText textReward;
    private ImageView userPic;

    //create challenge variables
    private String title;
    private String challenger;
    private String challengee;
    private String content;
    private int evidence_amount;
    private String evidence_type;
    private String reward;
    private String status;

    //Facebook variables
    private GraphUser user;
    private Session userInfoSession;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createchallenge);

        fetchUserInfo(Session.getActiveSession());

        spinnerAmount = (Spinner) findViewById(R.id.spinner_evidence_amount);
        spinnerAmount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                evidence_amount = Integer.parseInt(adapterView.getSelectedItem().toString());
                TextView xp = (TextView) findViewById(R.id.txtExperiencePoints);
                int xpAmount = Integer.parseInt(adapterView.getSelectedItem().toString()) * 300;
                xp.setText(xpAmount + "XP");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Isn't possible so, do nothing
            }
        });

        spinnerType = (Spinner) findViewById(R.id.spinner_evidence_type);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                evidence_type = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Isn't possible so, do nothing
            }
        });

        textTitle = (EditText) findViewById(R.id.txtChallengeName);
        textContent = (EditText) findViewById(R.id.txtChallengeContent);
        textReward = (EditText) findViewById(R.id.txtReward);

        btnCreateChallenge = (Button) findViewById(R.id.btnCreateChallenge);
        btnCreateChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChallenge();
            }
        });
    }

    public boolean checkFields() {
        boolean allFieldsEntered = true;
        StringBuilder error = new StringBuilder();
        if (textTitle.getText().toString().matches("")) {
            allFieldsEntered = false;
            error.append("You forgot to enter a challenge title!\n");
        }
        if (textContent.getText().toString().matches("")) {
            allFieldsEntered = false;
            error.append("You forgot to enter a challenge description!");
        }

        //If errors are added to the StringBuilder
        if (error.length() != 0) {
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
        }
        return allFieldsEntered;
    }

    public void createChallenge() {
        if (checkFields()) {
            setChallengeInfo();
            DatabaseThread db = new DatabaseThread();
            db.execute();
            Toast.makeText(getApplicationContext(), "Challenge created!", Toast.LENGTH_LONG).show();
        }
    }

    public void updateUI() {
        if (user.getName() != null) {
            TextView txtChallenger = (TextView) findViewById(R.id.txtChallenger);
            txtChallenger.setText("Challenger:\n" + user.getName());

            String imgId = "https://graph.facebook.com/" + user.getId() + "/picture";
            userPic = (ImageView)findViewById(R.id.imgChallenger);
            Picasso.with(getApplicationContext()).load(imgId).into(userPic);
        }
    }

    public void setChallengeInfo() {
        title = textTitle.getText().toString();
        content = textContent.getText().toString();
        reward = textReward.getText().toString();
        challenger = user.getId();
        status = "new_challenge";
    }


    private void fetchUserInfo(final Session session) {
        final Session currentSession = session;
        if (currentSession != null && currentSession.isOpened()) {

            if (currentSession != userInfoSession) {

                Request request = Request.newMeRequest(currentSession, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser me, Response response) {
                        if (currentSession == session) {
                            user = me;
                            updateUI();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString(FIELDS, REQUEST_FIELDS);
                request.setParameters(parameters);
                Request.executeBatchAsync(request);
                userInfoSession = currentSession;
            }
        } else {
            user = null;
        }
    }

    class DatabaseThread extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            // To connect to mongodb server
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());

            //get collection and attach class to it
            DBCollection userCollection = db.getCollection("challenge");
            userCollection.setObjectClass(Challenge.class);

            //TODO Add Challengee from appFriendslist
            Challenge challenge = new Challenge(title, challenger, challenger, content, evidence_amount, evidence_type, reward, status);
            userCollection.insert(challenge, WriteConcern.ACKNOWLEDGED);
            return null;
        }
    }
}