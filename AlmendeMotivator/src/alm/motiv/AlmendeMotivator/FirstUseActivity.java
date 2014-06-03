package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.models.Baseline;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

/**
 * Created by AsterLaptop on 4/23/14.
 */
public class FirstUseActivity extends Activity {
    private static String PREFS_NAME = "sportopiaprefs";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Baseline baseline = new Baseline();

    //views
    private EditText hours;
    private Spinner period;
    private EditText motivation1;
    private EditText motivation2;
    private EditText motivation3;
    private EditText motivation4;
    private Spinner partOfTheDay;
    private Spinner gender;
    private EditText reasonsNotToExercise;
    private EditText email;
    private Spinner living;
    private Spinner company;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstuse);
        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();

        //views

        hours = (EditText) findViewById(R.id.hoursInput);
        motivation1 = (EditText) findViewById(R.id.motivationReason1);
        motivation2 = (EditText) findViewById(R.id.motivationReason2);
        motivation3 = (EditText) findViewById(R.id.motivationReason3);
        motivation4 = (EditText) findViewById(R.id.motivationReason4);
        partOfTheDay = (Spinner) findViewById(R.id.spPartOfTheDay);
        gender = (Spinner) findViewById(R.id.spGender);
        living = (Spinner) findViewById(R.id.spLiving);
        company = (Spinner) findViewById(R.id.spCompany);
        reasonsNotToExercise = (EditText) findViewById(R.id.reasonsNoToSport);
        email = (EditText) findViewById(R.id.email);
    }

    public void onSubmitFirstUsePressed(View v) {
        if (validation() && Cookie.getInstance().internet) {
            baseline.setHours(hours.getText().toString());
            baseline.setPartOfTheDay(partOfTheDay.getSelectedItem().toString());
            baseline.setMotivations(motivation1.getText().toString(), motivation2.getText().toString(), motivation3.getText().toString(), motivation4.getText().toString());
            baseline.setCompany(company.getSelectedItem().toString());
            baseline.setLiving(living.getSelectedItem().toString());
            baseline.setGender(gender.getSelectedItem().toString());
            baseline.setReasonsNotToSport(reasonsNotToExercise.getText().toString());
            baseline.setEmail(email.getText().toString());

            new DatabaseThread().execute();

        } else {
            Toast.makeText(getApplicationContext(), "Not everything has been filled in correctly.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //google analytics
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        //google analytics
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

    //for validation
    private boolean validation() {
        boolean succes = true;
        if (!Validation.isNumericWithoutLimitations(hours, true)) succes = false;
        if (!Validation.hasText(motivation1)) succes = false;
        if (!Validation.hasText(motivation2)) succes = false;
        //if (!Validation.hasText(reasonsNotToExercise)) succes = false;
        if (!Validation.hasText(email)) succes = false;
        return succes;
    }

    @Override
    public void onBackPressed() {
        //Override so people can't cancel this process
    }

    class DatabaseThread extends AsyncTask<String, String, String> {
        private ProgressDialog simpleWaitDialog;

        @Override
        protected void onPreExecute() {
            simpleWaitDialog = ProgressDialog.show(FirstUseActivity.this,
                    "Please wait", "Processing");

        }

        protected void onPostExecute(String result) {
            try {
                simpleWaitDialog.dismiss();
                simpleWaitDialog = null;
            } catch (Exception e) {
                // nothing
            }

            Intent newIntent = new Intent(FirstUseActivity.this, FollowFriendActivity.class);
            startActivity(newIntent);
            finish();
            editor.putBoolean("firstUse", true);
            editor.commit();
        }

        @Override
        protected String doInBackground(String... strings) {
            if (Cookie.getInstance().internet) {
                try {
                    MongoClient client = Database.getInstance();
                    DB db = client.getDB(Database.uri.getDatabase());

                    DBCollection userCollection = db.getCollection("user");
                    userCollection.setObjectClass(User.class);

                    User match = new User();
                    match.put("facebookID", Cookie.getInstance().userEntryId);

                    User update = (User) userCollection.findOne(match);
                    update.put("nulmeting", baseline);

                    userCollection.update(match, update);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            return null;
        }
    }

}
