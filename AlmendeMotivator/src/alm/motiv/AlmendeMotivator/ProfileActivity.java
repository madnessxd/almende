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

public class ProfileActivity extends Activity{
    Intent home;
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;

    //edit fields
    private EditText aboutInput;
    private EditText sportsInput;
    private EditText nameInput;
    private EditText ageInput;
    private EditText goalInput;
    private EditText cityInput;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileview);
        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    public void editUser(View v){
        setContentView(R.layout.activity_profileviewedit);
    }

    public void updateUser(View v){
        aboutInput = (EditText)findViewById(R.id.aboutInput);
        sportsInput = (EditText)findViewById(R.id.sportsInput);
        nameInput = (EditText)findViewById(R.id.nameInput);
        ageInput = (EditText)findViewById(R.id.ageInput);
        goalInput = (EditText)findViewById(R.id.goalInput);
        cityInput = (EditText)findViewById(R.id.cityInput);
        new DatabaseThread().execute();
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

    class DatabaseThread extends AsyncTask<String, String, String> {
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection userCollection = db.getCollection("user");
            userCollection.setObjectClass(User.class);

            // get the current user from database
            User current = new User();
            current.put("facebookID", Cookie.getInstance().userEntryId);

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

            return null;
        }
    }
}
