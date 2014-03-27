package alm.motiv.AlmendeMotivator.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * FacebookManager controls connection with Facebook and gets requests from Facebook
 *
 * @author Thijs
 */

public class FacebookManager {

    /**
     * Fields
     */
    private final String ME_HOME_PREFIX = "/me/home";
    private final String GET_ME_HOME = "message, from, link, likes, comments.limit(1).summary(true), created_time, id";

    private final Activity activity;

    private Session session;
    private final SharedPreferences sharedPreferences;


    /**
     * Constructor
     *
     * @param activity
     */
    public FacebookManager(Activity activity) {
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("facebookManager",
                Context.MODE_PRIVATE);

    }

    /**
     * check if is logged in
     */
    public boolean isLoggedIn() {
        session = Session.getActiveSession();

        if (session != null) {
            if (session.getState() == SessionState.OPENED) {
                return true;
            }
        }

        return false;
    }

    public static void logout() {
        Session.getActiveSession().closeAndClearTokenInformation();
    }

    public boolean isSessionChanged() {
        session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            long lastExpirationDateInMillis = sharedPreferences.getLong("lastExpirationdate", 0);

            if (lastExpirationDateInMillis != 0) {
                if (lastExpirationDateInMillis == session.getExpirationDate().getTime()) {
                    return false;
                }
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("lastExpirationdate", session.getExpirationDate().getTime());
            editor.commit();
            return true;
        }
        return false;
    }


   /* *//**
     * Get timeline posts Async from the Logged in User
     *//*
    public void getTimelineUserAsync(final CrCallBack completionCallback, final CrCallBack failCallback) {
        Session.openActiveSession(activity, false, new Session.StatusCallback() {

            // callback when session changes state
            @Override
            public void call(Session session, SessionState state,
                             Exception exception) {

                if (state.isOpened()) {
                    // to the /me API
                    Bundle params = new Bundle();
                    params.putString("scope", "read_stream");
                    params.putString("fields", GET_ME_HOME);

                    // params.putString("limit", "20");
                    Request request = Request.newGraphPathRequest(session,
                            ME_HOME_PREFIX, new Request.Callback() {

                        @Override
                        public void onCompleted(Response response) {
                            GraphObject object = response.getGraphObject();
                            ArrayList<SocialMediaFeed> facebookFeeds = new ArrayList<SocialMediaFeed>();

                            try {
                                if (object != null) {
                                    JSONObject jsonobject = object.getInnerJSONObject();

                                    JSONArray data_array = jsonobject .getJSONArray("data");

                                    for (int i = 0; i < data_array.length(); i++) {
                                        SocialMediaFeed feed = new SocialMediaFeed(data_array.getJSONObject(i));
                                        if (!feed.getContent().equals("")) {
                                            facebookFeeds.add(feed);
                                        }
                                    }

                                    setFacebookFeeds(facebookFeeds);
                                }

                            } catch (JSONException e) {
                                //e.printStackTrace();
                            }

                            if (facebookFeeds.size() != 0) {
                                completionCallback.callback(facebookFeeds);
                            }else{
                                failCallback.callback(null);
                            }

                        }
                    });
                    request.setParameters(params);

                    request.executeAsync();
                }else{
                    failCallback.callback(null);
                }
            }
        });


    }*/

}
