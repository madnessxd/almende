package com.example.Test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    String APP_ID;
    Facebook fb;
    ImageView picture, login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Set global variables
        APP_ID = getString(R.string.app_id);
        fb = new Facebook(APP_ID);
        picture = (ImageView) findViewById(R.id.picture);

        //Start facebook login
        Session.openActiveSession(this, true, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {
                    Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                TextView activity = (TextView) findViewById(R.id.txtHelloWorld);
                                activity.setText("Hello " + user.getFirstName());
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    public void loginClicked(View v) {
        /*System.out.println(fb.isSessionValid());
        if (fb.isSessionValid()) {
            //Already logged in - logout
            try {
                System.out.println("Try logout");
                fb.logout(getApplicationContext());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //session not valid - login to facebook
            fb.authorize(this, new Facebook.DialogListener() {
                @Override
                public void onComplete(Bundle values) {
                    System.out.println("onComplete");
                    Toast.makeText(MyActivity.this, "onComplete", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFacebookError(FacebookError e) {
                    System.out.println("FacebookError");
                    Toast.makeText(MyActivity.this, "onFacebookError", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(DialogError e) {
                    System.out.println("onError");
                    Toast.makeText(MyActivity.this, "onError", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {
                    System.out.println("onCancel");
                    Toast.makeText(MyActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                }
            });
        }*/
    }
}
