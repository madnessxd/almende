package alm.motiv.AlmendeMotivator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    public void switchMessages(View v) {
        switch (v.getId()){
            case R.id.profileBut : setContentView(R.layout.profile); break;
            case R.id.messagesBut : setContentView(R.layout.messages); break;
            case R.id.challengesBut : setContentView(R.layout.challenges); break;
            case R.id.friendsBut : setContentView(R.layout.friends); break;
            case R.id.motivateBut : setContentView(R.layout.motivation); break;
        }
    }
}
