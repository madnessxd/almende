package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.models.Challenge;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Kevin on 26/03/2014.
 */
public class ChallengeViewActivity extends Activity implements Serializable {
    Intent home;
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;
    TextView title;
    TextView challenger;
    TextView challengee;
    TextView content;
    TextView evidence;
    TextView reward;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        updateUI();
    }

    public void updateUI(){
        Intent intent = getIntent();
        title = (TextView)findViewById(R.id.txtStaticChallengeName);
        challenger = (TextView)findViewById(R.id.txtChallenger);
        challengee = (TextView)findViewById(R.id.txtChallengee);
        content = (TextView)findViewById(R.id.viewChallengeContent);
        evidence = (TextView)findViewById(R.id.viewChallengeEvidence);
        reward = (TextView)findViewById(R.id.viewChallengeReward);

        title.setText(intent.getExtras().getString("title"));
        challenger.setText(intent.getExtras().getString("challenger"));
        challengee.setText(intent.getExtras().getString("challengee"));
        content.setText(intent.getExtras().getString("content"));
        evidence.setText(intent.getExtras().getInt("evidenceAmount") + " " + intent.getExtras().getString("evidenceType"));
        reward.setText(intent.getExtras().getString("reward"));
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void selectItem(int pos) {
        switch (pos) {
            case 0:
                k = new Intent(ChallengeViewActivity.this, ProfileActivity.class);
                break;
            case 1:
                k = new Intent(ChallengeViewActivity.this, MessageActivity.class);
                break;
            case 2:
                k = new Intent(ChallengeViewActivity.this, ChallengesMenuActivity.class);
                break;
            case 3:
                k = new Intent(ChallengeViewActivity.this, FriendActivity.class);
                break;
            case 4:
                FacebookManager.logout();
                k = new Intent(ChallengeViewActivity.this, FacebookMainActivity.class);
                break;
        }
        finish();
        startActivity(k);
    }

    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(ChallengeViewActivity.this, MainMenuActivity.class);
        startActivity(home);
        return;
    }
}
