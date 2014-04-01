package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.misc.CustomCallback;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.facebook.model.GraphUser;

import java.util.List;

public class MainMenuActivity extends Activity {
    Intent k;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);


    }


    public void switchMessages(View v) {
        switch (v.getId()) {
            case R.id.profileBut:
                k = new Intent(MainMenuActivity.this, ProfileActivity.class);
                break;
            case R.id.messagesBut:
                k = new Intent(MainMenuActivity.this, MessageActivity.class);
                break;
            //case R.id.challengesBut : k = new Intent(MainMenuActivity.this, ChallengesMenuActivity.class); break;
            case R.id.challengesBut:
                k = new Intent(MainMenuActivity.this, ChallengeActivity.class);
                break;
            case R.id.friendsBut:
                k = new Intent(MainMenuActivity.this, FriendActivity.class);
                break;

            //This switch case enables the JSON Parser test button
            //case R.id.testBut : k = new Intent(MainMenuActivity.this, Test.class); break;
        }
        finish();
        startActivity(k);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutFacebook:
                FacebookManager.logout();
                startActivity(new Intent(this, FacebookMainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
