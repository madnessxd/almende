package alm.motiv.AlmendeMotivator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main extends Activity {
    Intent k;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    public void switchMessages(View v) {
        System.out.println("1");
        switch (v.getId()){
            case R.id.profileBut : k = new Intent(Main.this, Profile.class); break;
            case R.id.messagesBut : k = new Intent(Main.this, Messages.class); break;
            case R.id.challengesBut : k = new Intent(Main.this, Challenges.class); break;
            case R.id.friendsBut : k = new Intent(Main.this, Friends.class); break;
            case R.id.testBut : k = new Intent(Main.this, Test.class); break;
        }
        finish();
        startActivity(k);
    }
    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
