package alm.motiv.AlmendeMotivator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ProfileActivity extends Activity{
    Intent home;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileview);
    }
    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(ProfileActivity.this, MainMenuActivity.class);
        startActivity(home);
        return;
    }
}
