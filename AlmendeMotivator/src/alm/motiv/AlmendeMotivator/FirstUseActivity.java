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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
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

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstuse);
        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();

        //views

        hours = (EditText)findViewById(R.id.hoursInput);
        period = (Spinner)findViewById(R.id.spPeriod);
        motivation1 = (EditText)findViewById(R.id.motivationReason1);
        motivation2 = (EditText)findViewById(R.id.motivationReason2);
        motivation3 = (EditText)findViewById(R.id.motivationReason3);
        motivation4 = (EditText)findViewById(R.id.motivationReason4);
        partOfTheDay = (Spinner)findViewById(R.id.spPartOfTheDay);
    }

    public void onSubmitFirstUsePressed(View v){
        if(validation()){
            baseline.setHours(hours.getText().toString());
            baseline.setPeriod(period.getSelectedItem().toString());
            baseline.setPartOfTheDay(partOfTheDay.getSelectedItem().toString());

            new DatabaseThread().execute();

        }
    }

    //for validation
    private boolean validation(){
        boolean succes = true;
        if(!Validation.isNumericWihtoutLimitations(hours, true))succes=false;
        if(!Validation.isLetters(motivation1, true))succes=false;
        if(!Validation.isLetters(motivation2, true))succes=false;
        if(!Validation.isLetters(motivation3, false))succes=false;
        if(!Validation.isLetters(motivation4, false))succes=false;
        return succes;
    }

    class DatabaseThread extends AsyncTask<String, String, String> {
        private ProgressDialog simpleWaitDialog;
        @Override
        protected void onPreExecute() {
            simpleWaitDialog = ProgressDialog.show(FirstUseActivity.this,
                    "Please wait", "Processing");

        }

        protected void onPostExecute(String result) {
            simpleWaitDialog.setMessage("Process completed.");
            simpleWaitDialog.dismiss();

            //TODO motivations need to be added to baseline
            Intent newIntent = new Intent(FirstUseActivity.this, ChallengeOverviewActivity.class);
            startActivity(newIntent);
            editor.putBoolean("firstUse",true);
            editor.commit();
        }

        @Override
        protected String doInBackground(String... strings) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());

            DBCollection userCollection = db.getCollection("user");
            userCollection.setObjectClass(User.class);

            User match = new User();
            match.put("facebookID", Cookie.getInstance().userEntryId);

            User update = (User)userCollection.findOne(match);
            update.put("nulmeting", baseline);

            userCollection.update(match, update);

            return null;
        }
    }

}
