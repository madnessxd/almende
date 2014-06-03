package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.models.Level;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;
import com.mongodb.*;

public class ProfileActivity extends Activity{
    //menu
    private String[] mMenuOptions;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;

    private Level level = Level.BEGINNER;

    //our user
    User user=null;

    //edit fields
    private EditText sportsInput;
    private EditText nameInput;
    private EditText ageInput;
    private EditText goalInput;
    private EditText cityInput;
    private EditText aboutInput;

    //the intent that called this activity this is used to display either the user or a friend's profile
    private Intent requestFrom;
    private String facebookIdFriend = null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileview);
        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //we want to know whose profile we need to display
        requestFrom = getIntent();
        try{facebookIdFriend = requestFrom.getExtras().getString("facebookIdFriend");}catch (Exception e){}

        new DatabaseThread().execute("select");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

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

    //on menu pressed
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    private void initLabels(){
        try{
            //set labels
            TextView nameContent = (TextView)findViewById(R.id.name);
            TextView aboutContent = (TextView)findViewById(R.id.aboutContent);
            TextView sportsContent = (TextView)findViewById(R.id.sportsContent);
            TextView cityContent = (TextView)findViewById(R.id.cityContent);
            TextView ageContent = (TextView)findViewById(R.id.ageContent);
            TextView goalContent = (TextView)findViewById(R.id.goalContent);
            TextView xpText = (TextView)findViewById(R.id.progressText);
            ProgressBar xpBar = (ProgressBar)findViewById(R.id.progressXP);
            Button btnEdit = (Button)findViewById(R.id.btnEdit);

            nameContent.setText(user.getName());
            aboutContent.setText(user.getAbout());
            sportsContent.setText(user.getSports());
            cityContent.setText(user.getCity());
            ageContent.setText(user.getAge());
            goalContent.setText(user.getGoal());

            //manage XP
            int XP=0;
            try{XP = user.getXP();}catch (Exception e){
                //do nothing
            }
            setLevelOfUser(XP);

            xpBar.setMax(level.getMaxXP());
            xpBar.setProgress(XP);
            xpText.setText(level.toString().toLowerCase() + ": "+ XP +"xp /"+level.getMaxXP()+"xp");

            if(facebookIdFriend==null){
                btnEdit.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            System.out.println(e);
        }

    }

    public void setLevelOfUser(int XP){
        if(XP<Level.BEGINNER.getMaxXP())level = level.BEGINNER;

        else if(XP<Level.NOVICE.getMaxXP())level = level.NOVICE;

        else if(XP<Level.ATHLETE.getMaxXP())level = level.ATHLETE;

        else if(XP<Level.CHAMPION.getMaxXP())level = level.MASTER;


        else if(XP>Level.CHAMPION.getMaxXP()) level = level.CHAMPION;
    }

    public void editUserBtn(View v){
        finish();
        Intent edit = new Intent(this, ProfileEditActivity.class);
        if(facebookIdFriend!=null){
            edit.putExtra("facebookIdFriend",facebookIdFriend);
        }
        startActivity(edit);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Menu.selectItem(position, ProfileActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent home = new Intent(ProfileActivity.this, ChallengeOverviewActivity.class);
        startActivity(home);
        return;
    }

    class DatabaseThread extends AsyncTask<String, String, String> {
        private ProgressDialog simpleWaitDialog;

        @Override
        protected void onPreExecute() {
            simpleWaitDialog = ProgressDialog.show(ProfileActivity.this,
                    "Please wait", "Loading");

        }

        protected void onPostExecute(String result) {
            try {
                simpleWaitDialog.dismiss();
                simpleWaitDialog = null;
            } catch (Exception e) {
                // nothing
            }
            initLabels();
        }

        protected String doInBackground(String... args) {
            if(Cookie.getInstance().internet)
            {
                try{

                    MongoClient client = Database.getInstance();
                    DB db = client.getDB(Database.uri.getDatabase());
                    DBCollection userCollection = db.getCollection("user");
                    userCollection.setObjectClass(User.class);

                    // get the current user from database
                    User current = new User();
                    if(facebookIdFriend!=null){
                        current.put("facebookID", facebookIdFriend);
                    }else{
                        current.put("facebookID", Cookie.getInstance().userEntryId);
                    }
                    user = (User) userCollection.find(current).toArray().get(0);
                }catch(Exception e){
                    System.out.println(e);
                }
            }
            return null;
        }
    }
}
