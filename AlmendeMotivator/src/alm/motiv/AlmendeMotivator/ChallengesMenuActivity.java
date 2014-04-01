package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChallengesMenuActivity extends Activity {
    Intent home;
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challengesmenu);
        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void selectItem(int pos){
        switch (pos){
            case 0:
                k = new Intent(ChallengesMenuActivity.this, ProfileActivity.class);
                break;
            case 1:
                k = new Intent(ChallengesMenuActivity.this, MessageActivity.class);
                break;
            case 2:
                k = new Intent(ChallengesMenuActivity.this, ChallengeActivity.class);
                break;
            case 3:
                k = new Intent(ChallengesMenuActivity.this, FriendActivity.class);
                break;
            case 4:
                FacebookManager.logout();
                k = new Intent(ChallengesMenuActivity.this, FacebookMainActivity.class);
                break;
        }
        finish();
        startActivity(k);
    }

    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(ChallengesMenuActivity.this, MainMenuActivity.class);
        startActivity(home);

        return;
    }

    public void onCreatePressed(View v) {
        startActivity(new Intent(ChallengesMenuActivity.this, ChallengeActivity.class));
    }
}
