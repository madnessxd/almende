package alm.motiv.AlmendeMotivator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Kevin on 26/03/2014.
 */
public class ChallengeActivity extends Activity {
    Intent home;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challengeview);
    }

    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(ChallengeActivity.this, MainMenuActivity.class);
        startActivity(home);
        return;
    }

}
