package alm.motiv.AlmendeMotivator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Created by AsterLaptop on 4/23/14.
 */
public class TermsActivity extends Activity {
    private static String PREFS_NAME = "sportopiaprefs";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

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

    @Override
        public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsofuse);
        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
    }

    public void onDisagreePressed(View v){
        editor.putBoolean("termsAgreement", false);
        editor.commit();

        final AlertDialog d = new AlertDialog.Builder(this)
                .setNegativeButton("OK", null)
                .setTitle("Terms of use")
                .setMessage("You will not be able to use the app unless you agree with the terms of use")
                .show();
    }

    public void onAgreePressed(View v){
        editor.putBoolean("termsAgreement", true);
        editor.commit();

        //redirect user to first use screen
        finish();
        Intent intent = new Intent(this, FirstUseActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //Do nothing because people shouldn't cancel this
    }
}
