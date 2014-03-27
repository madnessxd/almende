package alm.motiv.AlmendeMotivator.facebook;

import alm.motiv.AlmendeMotivator.MainMenuActivity;
import alm.motiv.AlmendeMotivator.R;
import android.content.Intent;
import android.graphics.Color;
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
                            updateUI(session);
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
        if (session.isOpened()) {
            if (user != null) {
                infoLabel.setText("Logged in as: " + user.getName());
            } else {
                infoLabel.setText(getResources().getString(
                        com.facebook.android.R.string.com_facebook_usersettingsfragment_logged_in));
            }
        } else {
            infoLabel.setTextColor( Color.BLACK);
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

                Intent intent = new Intent(getActivity(), MainMenuActivity.class);
                getActivity().startActivity(intent);
            }
        }
    };

}
