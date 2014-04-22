package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainMenuActivity extends Activity {
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

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
                k = new Intent(MainMenuActivity.this, ProfileActivity.class);
                break;
            case 1:
                k = new Intent(MainMenuActivity.this, MessageActivity.class);
                break;
            case 2:
                k = new Intent(MainMenuActivity.this, ChallengeOverviewActivity.class);
                break;
            case 3:
                k = new Intent(MainMenuActivity.this, FriendActivity.class);
                break;
            case 4:
                FacebookManager.logout();
                k = new Intent(MainMenuActivity.this, FacebookMainActivity.class);
                break;
        }
        finish();
        startActivity(k);
    }

    public void switchMessages(View v) {
        switch (v.getId()) {
            case R.id.profileBut:
                k = new Intent(MainMenuActivity.this, ProfileActivity.class);
                break;
            case R.id.messagesBut:
                k = new Intent(MainMenuActivity.this, MessageActivity.class);
                break;
            case R.id.challengesBut:
                k = new Intent(MainMenuActivity.this, ChallengeOverviewActivity.class);
                break;
            case R.id.friendsBut:
                k = new Intent(MainMenuActivity.this, FriendActivity.class);
                break;
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
        showPopUp();
    }

    private void showPopUp() {
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Exit");
        helpBuilder.setMessage("Are you sure you want to exit Sportopia?");
        helpBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });

        helpBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }
}
