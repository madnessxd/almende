package alm.motiv.AlmendeMotivator;

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

public class ProfileEditActivity extends Activity{
    //menu
    private String[] mMenuOptions;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;

    //our user
    User user=null;

    //edit fields
    private EditText sportsInput;
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
        setContentView(R.layout.activity_profileviewedit);

        //menu
        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //we want to know whose profile we need to display
        requestFrom = getIntent();
        try{ facebookIdFriend = requestFrom.getExtras().getString("facebookIdFriend"); }catch (Exception e){ }

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

    public void initView(){

        //fields
        aboutInput = (EditText)findViewById(R.id.aboutInput);
        sportsInput = (EditText)findViewById(R.id.sportsInput);
        ageInput = (EditText)findViewById(R.id.ageInput);
        goalInput = (EditText)findViewById(R.id.goalInput);
        cityInput = (EditText)findViewById(R.id.cityInput);

        //set content fields with existing data
        goalInput.setText(user.getGoal());
        aboutInput.setText(user.getAbout());
        ageInput.setText(user.getAge());
        cityInput.setText(user.getCity());
        sportsInput.setText(user.getSports());

    }

    public void saveUserBtn(View v) throws InterruptedException {
        if(validation()&&Cookie.getInstance().internet){
            new DatabaseThread().execute("insert");
        } else{
            Toast.makeText(getApplicationContext(), "You forgot to select a challengee", Toast.LENGTH_SHORT).show();
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Menu.selectItem(position, ProfileEditActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent home = new Intent(ProfileEditActivity.this, ChallengeOverviewActivity.class);
        startActivity(home);
        return;
    }

    //for validation
    private boolean validation(){
        boolean succes = true;
        if(!Validation.hasText(aboutInput))succes=false;
        if(!Validation.isNumeric(ageInput,true))succes=false;
        if(!Validation.isLetters(cityInput, false))succes=false;
        if(!Validation.hasText(sportsInput))succes=false;
        if(!Validation.isLetters(goalInput, false))succes=false;
        return succes;
    }

    class DatabaseThread extends AsyncTask<String, String, String> {
        private ProgressDialog simpleWaitDialog;
        private boolean redirect = false;

        @Override
        protected void onPreExecute() {
            simpleWaitDialog = ProgressDialog.show(ProfileEditActivity.this,
                    "Please wait", "Loading");

        }

        protected void onPostExecute(String result) {
            try {
                simpleWaitDialog.dismiss();
                simpleWaitDialog = null;
            } catch (Exception e) {
                // nothing
            }
            if(redirect){
                finish();
                Intent redirection = new Intent(ProfileEditActivity.this, ProfileActivity.class);
                startActivity(redirection);
            }else{
                initView();
            }
        }

        protected String doInBackground(String... args) {
            if(Cookie.getInstance().internet){
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
                    User aUser = (User) userCollection.find(current).toArray().get(0);

                    if(args[0]=="select"){
                        user = aUser;
                    }else if(args[0]=="insert"){
                        insertQuery(current,aUser, userCollection);
                        redirect=true;
                    }
                }catch (Exception e){
                    System.out.println(e);
                }
            }
            return null;
        }

        private void insertQuery(User current, User newUser, DBCollection userCollection){
            newUser.setAbout(String.valueOf(aboutInput.getText()));
            newUser.setAge(String.valueOf(ageInput.getText()));
            newUser.setCity(String.valueOf(cityInput.getText()));
            newUser.setGoal(String.valueOf(goalInput.getText()));
            newUser.setSports(String.valueOf(sportsInput.getText()));

            //overwrite the old one with the new one
            userCollection.findAndModify(current, newUser);
        }
    }
}
