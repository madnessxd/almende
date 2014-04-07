package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.mongodb.*;

import java.util.concurrent.ExecutionException;

public class ProfileActivity extends Activity{
    Intent home;
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;

    //edit fields
    private EditText sportsInput;
    private EditText nameInput;
    private EditText ageInput;
    private EditText goalInput;
    private EditText cityInput;
    private EditText aboutInput;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileview);
        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    public void editUserBtn(View v){
        //change layout
        setContentView(R.layout.activity_profileviewedit);

        //fields
        aboutInput = (EditText)findViewById(R.id.aboutInput);
        sportsInput = (EditText)findViewById(R.id.sportsInput);
        nameInput = (EditText)findViewById(R.id.nameInput);
        ageInput = (EditText)findViewById(R.id.ageInput);
        goalInput = (EditText)findViewById(R.id.goalInput);
        cityInput = (EditText)findViewById(R.id.cityInput);

        //get the user
        User user = null;
        try {
             user = new DatabaseThread().execute("select").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //set content fields with existing data
        nameInput.setText(user.getName());
        goalInput.setText(user.getGoal());
        aboutInput.setText(user.getAbout());
        ageInput.setText(user.getAge());
        cityInput.setText(user.getCity());
        sportsInput.setText(user.getSports());
    }

    public void cancelEditBtn(View v){
        setContentView(R.layout.activity_profileview);
    }

    public void saveUserBtn(View v){
        if(validation()){
            new DatabaseThread().execute("insert");
        }
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
                k = new Intent(ProfileActivity.this, ProfileActivity.class);
                break;
            case 1:
                k = new Intent(ProfileActivity.this, MessageActivity.class);
                break;
            case 2:
                k = new Intent(ProfileActivity.this, ChallengesMenuActivity.class);
                break;
            case 3:
                k = new Intent(ProfileActivity.this, FriendActivity.class);
                break;
            case 4:
                FacebookManager.logout();
                k = new Intent(ProfileActivity.this, FacebookMainActivity.class);
                break;
        }
        finish();
        startActivity(k);
    }
    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(ProfileActivity.this, MainMenuActivity.class);
        startActivity(home);
        return;
    }

    //for validation
    private boolean validation(){
        boolean succes = true;
        if(!Validation.hasText(aboutInput))succes=false;
        if(!Validation.isNumeric(ageInput,true))succes=false;
        if(!Validation.isLetters(cityInput, false))succes=false;
        if(!Validation.isLetters(nameInput,true))succes=false;
        if(!Validation.hasText(sportsInput))succes=false;
        if(!Validation.isLetters(goalInput,false))succes=false;
        return succes;
    }

    class DatabaseThread extends AsyncTask<String, String, User> {
        /**
         * Creating product
         * */
        protected User doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection userCollection = db.getCollection("user");
            userCollection.setObjectClass(User.class);

            // get the current user from database
            User current = new User();
            current.put("facebookID", Cookie.getInstance().userEntryId);
            User newUser = (User) userCollection.find(current).toArray().get(0);


            if(args[0]=="select"){
                return newUser;
            }else if(args[0]=="insert"){
                insertQuery(current, userCollection);
            }

            return null;
        }

        private void insertQuery(User current, DBCollection userCollection){
            // make a new user, based upon the data of the current
            User newUser = (User) userCollection.find(current).toArray().get(0);
            newUser.setAbout(String.valueOf(aboutInput.getText()));
            newUser.setName(String.valueOf(nameInput.getText()));
            newUser.setAge(String.valueOf(ageInput.getText()));
            newUser.setCity(String.valueOf(cityInput.getText()));
            newUser.setGoal(String.valueOf(goalInput.getText()));
            newUser.setSports(String.valueOf(sportsInput.getText()));

            //overwrite the old one with the new one
            userCollection.findAndModify(current, newUser);
        }
    }
}
