package alm.motiv.AlmendeMotivator.facebook;

import alm.motiv.AlmendeMotivator.*;
import alm.motiv.AlmendeMotivator.models.User;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.widget.Toast;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.mongodb.*;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 10/14/13
 * Time: 9:29 AM
 * To change this template use File | SettingsHelper | File Templates.
 */
public class FacebookMainFragment extends Fragment {

    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String PICTURE = "picture";
    private static final String FIELDS = "fields";

    private static final String REQUEST_FIELDS = TextUtils.join(",", new String[]{ID, NAME, PICTURE});

    private static final String TAG = "MainFragment";
    private UiLifecycleHelper uiHelper;
    private Session userInfoSession; // the Session used to fetch the current user info
    private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me?access_token=";
    private GraphUser user;
    private LoginButton authButton;
    private TextView redirectLabel;
    private TextView infoLabel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_facebook_login, container, false);
        redirectLabel = (TextView) view.findViewById(R.id.redirectLabel);
        redirectLabel.setText("");
        infoLabel = (TextView) view.findViewById(R.id.infoLabel);

        authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("read_stream", "user_about_me"));

        // if no background is set for some reason, then default to Facebook blue
//        if (view.getBackground() == null) {
//            view.setBackgroundColor(getResources().getColor(com.facebook.android.R.color.com_facebook_blue));
//        } else {
//            view.getBackground().setDither(true);
//        }

        return view;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        fetchUserInfo(session);
        updateUI(session);

        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
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

                            //need this so we can access the user's data in mongodb with database entry key
                            Cookie entry = Cookie.getInstance();
                            entry.userEntryId = user.getId();
                            entry.userName = user.getName();

                            updateUI(session);
                            new DatabaseThread().execute();
                        }
                        if (response.getError() != null) {
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

    private void updateUI(final Session session) {
        if (!isAdded()) {
            return;
        }
        //Facebook automatically asigns a value to the authenticationButton. Here we override that call. If the authButton contains Logout, we want the button
        //to become inusable and for the text to change. This notifies the user that a process is being done and that he just needs to wait it out.
        if(authButton.getText().toString().equals("Logout")){
            System.out.println("Button = logout");
            authButton.setEnabled(false);
            authButton.setText("Processing");
        }
        else{
            authButton.setClickable(true);
        }

        if (session.isOpened()) {
            if (user != null) {
                infoLabel.setText("Logged in as: " + user.getName());
            } else {
                infoLabel.setText(getResources().getString(
                        com.facebook.android.R.string.com_facebook_usersettingsfragment_logged_in));
            }
        } else {
            infoLabel.setTextColor(Color.BLACK);
            infoLabel.setShadowLayer(0f, 0f, 0f, Color.BLACK);
            infoLabel.setText(getResources().getString(
                    com.facebook.android.R.string.com_facebook_usersettingsfragment_not_logged_in));
            infoLabel.setCompoundDrawables(null, null, null, null);
            infoLabel.setTag(null);
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
            if (session != null && session.isOpened()) {
                Toast.makeText(getActivity(), "User is logged in.", Toast.LENGTH_LONG).show();


            }
        }
    };

    /**
     * Background Async Task to add the user to our own database, if it doesn't already exist
     */
    class DatabaseThread extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            // To connect to mongodb server
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());

            //get collection and attach class to it
            DBCollection userCollection = db.getCollection("user");
            userCollection.setObjectClass(User.class);

            //query the database to see if the user is already known in our database
            DBObject query = QueryBuilder.start("facebookID").is(user.getId()).get();

            DBCursor cursor = userCollection.find(query);

            if (cursor.count() == 0) {//if no result, add user
                User appUser = new User(user.getId(), user.getName());
                userCollection.insert(appUser, WriteConcern.ACKNOWLEDGED);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Intent intent = new Intent(getActivity(), ChallengeOverviewActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        }
    }

}
