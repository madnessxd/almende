package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.models.Challenge;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.mongodb.*;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Kevin on 02/04/2014.
 */
public class ChallengeCreateActivity extends Activity {
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;

    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String PICTURE = "picture";
    private static final String FIELDS = "fields";
    private static final String REQUEST_FIELDS = TextUtils.join(",", new String[]{ID, NAME, PICTURE});

    //Layout variables
    private Button btnCreateChallenge;
    private Spinner spinnerFriends;
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

    private String[] facebookFriends = {"loading..."};
    private String[] facebookFriendsName = {"loading..."};

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

        spinnerFriends = (Spinner) findViewById(R.id.spinner_getFriends);
        //GET FRIENDS
        spinnerFriends.setOnTouchListener(Spinner_OnTouch);

        spinnerFriends.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                challengee = facebookFriends[spinnerFriends.getSelectedItemPosition()];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        DatabaseThread2 dbT = new DatabaseThread2();
        dbT.execute();

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

        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void selectItem(int pos){
        switch (pos){
            case 0:
                k = new Intent(ChallengeCreateActivity.this, ProfileActivity.class);
                break;
            case 1:
                k = new Intent(ChallengeCreateActivity.this, MessageActivity.class);
                break;
            case 2:
                k = new Intent(ChallengeCreateActivity.this, ChallengeOverviewActivity.class);
                break;
            case 3:
                k = new Intent(ChallengeCreateActivity.this, FriendActivity.class);
                break;
            case 4:
                FacebookManager.logout();
                k = new Intent(ChallengeCreateActivity.this, FacebookMainActivity.class);
                break;
        }
        finish();
        startActivity(k);
    }

    private View.OnTouchListener Spinner_OnTouch = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                updateFriends();
            }
            return false;
        }
    };

    public void updateFriends() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, facebookFriendsName);
        spinnerFriends.setAdapter(spinnerArrayAdapter);
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
            finish();
        }
    }

    public void updateUI() {
        if (user.getName() != null) {
            TextView txtChallenger = (TextView) findViewById(R.id.txtChallenger);
            txtChallenger.setText("Challenger:\n" + user.getName());

            String imgId = "https://graph.facebook.com/" + user.getId() + "/picture";
            userPic = (ImageView) findViewById(R.id.imgChallenger);
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

            Challenge challenge = new Challenge(title, challenger, challengee, content, evidence_amount, evidence_type, reward, status, "null");
            userCollection.insert(challenge, WriteConcern.ACKNOWLEDGED);
            return null;
        }
    }

    class DatabaseThread2 extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection userCollection = db.getCollection("user");
            userCollection.setObjectClass(User.class);

            Session session = Session.getActiveSession();

            Request request = new Request(session, "me", null, HttpMethod.GET);
            Response response = request.executeAndWait();

            User curUser = new User();
            curUser.put("facebookID", Cookie.getInstance().userEntryId);
            User newUser = (User) userCollection.find(curUser).toArray().get(0);


            ArrayList<String> arrayMessages = (ArrayList<String>) newUser.get("friends");

            String[] facebookFriendsTemp = new String[arrayMessages.toArray().length];
            String[] facebookFriendsNameTemp = new String[arrayMessages.toArray().length];


            for (int i = 0; i < arrayMessages.toArray().length; i++) {
                facebookFriendsTemp[i] = arrayMessages.toArray()[i].toString().replace("{ "  + '"' + "facebookID" + '"' + " : " + '"',"").replace('"' + "}","");

                request = new Request(session, facebookFriendsTemp[i], null, HttpMethod.GET);
                response = request.executeAndWait();

                if (response.getError() != null) {
                    System.out.println("NOPE");
                } else {
                    GraphUser graphUser = response.getGraphObjectAs(GraphUser.class);
                    facebookFriendsNameTemp[i] = graphUser.getName();
                }
            }

            facebookFriends = facebookFriendsTemp;
            facebookFriendsName = facebookFriendsNameTemp;
            return null;
        }
    }

    public void onBackPressed() {
        finish();
        Intent home = new Intent(ChallengeCreateActivity.this, ChallengeOverviewActivity.class);
        startActivity(home);
        return;
    }
}