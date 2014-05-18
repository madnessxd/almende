package alm.motiv.AlmendeMotivator;

/**
 * Created by AsterLaptop on 4/13/14.
 */

import java.util.ArrayList;
import java.util.List;

import alm.motiv.AlmendeMotivator.adapters.EntryAdapter;
import alm.motiv.AlmendeMotivator.models.ChallengeHeader;
import alm.motiv.AlmendeMotivator.adapters.Item;
import alm.motiv.AlmendeMotivator.models.Challenge;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.mongodb.*;

public class ChallengeOverviewActivity extends Activity implements OnItemClickListener {
    //menu
    private String[] mMenuOptions;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;

    private ArrayList<Item> items = new ArrayList<Item>();
    private ListView listview = null;
    private DatabaseThread DT = new DatabaseThread();

    //shared preferences
    private static String PREFS_NAME = "sportopiaprefs";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences(PREFS_NAME, 0);
        boolean redirect = settings.getBoolean("termsAgreement", false);
        boolean firstUse = settings.getBoolean("firstUse", false);

        //if we don't have an agreement yet on the terms of use, we redirect the user
        if (!redirect) {
            Intent intent = new Intent(this, TermsActivity.class);
            startActivity(intent);
        } else if (!firstUse) {
            Intent intent = new Intent(this, FirstUseActivity.class);
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challengeoverview);

        listview = (ListView) findViewById(R.id.listView_main);

        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        DT.execute();
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

    public void initListview() {
        try {
            List<DBObject> send = DT.sendChallenges;
            List<DBObject> received = DT.receivedChallenges;

            items.add(new ChallengeHeader("Challenges you send"));
            if (send != null) {
                for (DBObject aChallenge : send) {
                    items.add((Item) aChallenge);
                }
            } else {
                items.add(new ChallengeHeader("No challenges send"));
            }

            items.add(new ChallengeHeader("Challenges you received"));
            if (received != null) {
                for (DBObject aChallenge : received) {
                    items.add((Item) aChallenge);
                }
            } else {
                items.add(new ChallengeHeader("No challenges received"));
            }

            EntryAdapter adapter = new EntryAdapter(this, items);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(this);
        } catch (Exception e) {
            System.out.println(e);
        }

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
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
        );

        helpBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }


    public void onCreatePressed(View v) {
        startActivity(new Intent(this, ChallengeCreateActivity.class));
        finish();
    }


    @Override
    public void onItemClick(AdapterView arg0, View arg1, int position, long arg3) {
        Challenge item = (Challenge) items.get(position);

        //Open the challengeViewActivity and give the current selected Challenge to the activity
        finish();
        Intent intent = new Intent(this, ChallengeViewActivity.class);
        intent.putExtra("id", item.getID().toString());
        this.startActivity(intent);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Menu.selectItem(position, ChallengeOverviewActivity.this);
        }
    }

    private class DatabaseThread extends AsyncTask<String, String, String> {
        public List<DBObject> sendChallenges = null;
        public List<DBObject> receivedChallenges = null;

        ProgressDialog simpleWaitDialog = null;

        @Override
        protected void onPreExecute() {
            simpleWaitDialog = ProgressDialog.show(ChallengeOverviewActivity.this,
                    "Please wait", "Processing");
        }

        @Override
        protected void onPostExecute(String string) {
            simpleWaitDialog.dismiss();
            initListview();
        }

        @Override
        protected String doInBackground(String... args) {
            if (Cookie.getInstance().internet) {
                try {
                    MongoClient client = Database.getInstance();
                    DB db = client.getDB(Database.uri.getDatabase());
                    DBCollection challengeCollection = db.getCollection("challenge");
                    challengeCollection.setObjectClass(Challenge.class);

                    //find al the challenges the user send
                    Challenge query1 = new Challenge();
                    query1.put("challenger", Cookie.getInstance().userEntryId);
                    sendChallenges = challengeCollection.find(query1).toArray();

                    //find al the challenges the user received
                    Challenge query2 = new Challenge();
                    query2.put("challengee", Cookie.getInstance().userEntryId);
                    receivedChallenges = challengeCollection.find(query2).toArray();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }


            return null;
        }

    }
}
