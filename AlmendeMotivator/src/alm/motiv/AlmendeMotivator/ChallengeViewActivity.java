package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.adapters.MessageAdapter;
import alm.motiv.AlmendeMotivator.models.Challenge;
import alm.motiv.AlmendeMotivator.models.Message;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.squareup.picasso.Picasso;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Kevin on 26/03/2014.
 */
public class ChallengeViewActivity extends Activity implements Serializable {
    private Intent home;
    private Intent k;
    private Intent intent;

    private String[] mMenuOptions;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;

    private String id;

    //when a user adds a comment, this object will be used
    private Message message = new Message();

    private ListView messagesListview;

    private Challenge currentChallenge = null;

    private Spinner spCategories;
    private TextView txtStatus;

    private ArrayList<BasicDBObject> messages = null;

    private Boolean messageSend = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        intent = getIntent();
        messagesListview = (ListView) findViewById(R.id.lstMessages);
        View headerView = View.inflate(this, R.layout.activity_challenge_header, null);
        View footerView = View.inflate(this, R.layout.activity_challenge_footer, null);
        messagesListview.addHeaderView(headerView);
        messagesListview.addFooterView(footerView);

        id = intent.getExtras().getString("id");

        //first database call because we need information about the challenge
        new DatabaseThread().execute("get challenge");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    public void updateUI(String challengerName, String challengeeName) {
        try{
            TextView title = (TextView) findViewById(R.id.txtStaticChallengeName);
            TextView challenger = (TextView) findViewById(R.id.txtChallenger);
            TextView challengee = (TextView) findViewById(R.id.txtChallengee);
            TextView content = (TextView) findViewById(R.id.viewChallengeContent);
            TextView evidence = (TextView) findViewById(R.id.viewChallengeEvidence);
            TextView reward = (TextView) findViewById(R.id.viewChallengeReward);
            ImageView imgChallenger = (ImageView) findViewById(R.id.imgChallenger);
            ImageView imgChallengee = (ImageView) findViewById(R.id.imgChallengee);
            txtStatus = (TextView)findViewById(R.id.txtStatus);

            title.setText(currentChallenge.getTitle());
            challenger.setText(challengerName);
            challengee.setText(challengeeName);
            content.setText(currentChallenge.getContent());
            evidence.setText(currentChallenge.getEvidenceAmount() + " " + currentChallenge.getEvidenceType());
            if(!currentChallenge.getReward().equals("")){
                reward.setText("XP: "+currentChallenge.getXPreward() + "\nAdditional Reward: "+currentChallenge.getReward() );
            }else{
                reward.setText("XP: "+currentChallenge.getXPreward() );
            }

            String imgSource1 = "https://graph.facebook.com/" + currentChallenge.getChallenger() + "/picture?type=normal&height=200&width=200";
            String imgSource2 = "https://graph.facebook.com/" + currentChallenge.getChallengee() + "/picture?type=normal&height=200&width=200";

            Picasso.with(getApplicationContext()).load(imgSource1).into(imgChallenger);
            Picasso.with(getApplicationContext()).load(imgSource2).into(imgChallengee);

            imgChallengee.setMinimumHeight(300);
            imgChallengee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    visitProfile(currentChallenge.getChallengee());
                }
            });

            imgChallenger.setMinimumHeight(300);
            imgChallenger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    visitProfile(currentChallenge.getChallenger());
                }
            });

            updateStatusElements();

            //get comments from challenge
            messages = currentChallenge.getComments();
            if (messages == null) {
                Message emptyMessage = new Message();
                emptyMessage.setContent("This challenge doesn't have comments.");
                emptyMessage.setAuthor(" ");
                emptyMessage.setDate(new Date());
                messages = new ArrayList<BasicDBObject>();
                messages.add(emptyMessage);
            }
            MessageAdapter adapter = new MessageAdapter(this, messages);
            messagesListview.setAdapter(adapter);
        }catch(Exception e){
            System.out.println(e);}


    }

    public void visitProfile(String viewProfileOf){
        Intent displayFriend = new Intent(ChallengeViewActivity.this, ProfileActivity.class);
        displayFriend.putExtra("viewFriendProfile", true);
        displayFriend.putExtra("facebookIdFriend", viewProfileOf);
        finish();
        startActivity(displayFriend);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Menu.selectItem(position, ChallengeViewActivity.this);
        }
    }

    public void onAcceptPressed(View v) {
        if(Cookie.getInstance().internet){
            try{
                currentChallenge.setStatus("accepted");
                currentChallenge.updateLoginDate();
                new DatabaseThread().execute("");
                updateStatusElements();
                finish();
                Intent newIntent = new Intent(this, ChallengeOverviewActivity.class);
                this.startActivity(newIntent);
            }catch(Exception e ){
                System.out.println(e);
            }

        }
    }

    public void onCompletePressed(View v) {
        if(Cookie.getInstance().internet){
            String gps = "no GPS";
            try {
                gps = getGPS();
            } catch (Exception e) {
                System.out.println("no gps" + e);
            }
            finish();
            currentChallenge.updateLoginDate();
            Intent newIntent = new Intent(this, ChallengeEvidence.class);
            newIntent.putExtra("evidenceAmount", currentChallenge.getEvidenceAmount());
            newIntent.putExtra("title", currentChallenge.getTitle());
            newIntent.putExtra("challengeid", id);
            newIntent.putExtra("gps", gps);
            this.startActivity(newIntent);
        }
    }

    public String getGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        locationListener.onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

        return locationListener.getLocation();
    }

    public void onEvidencePressed(View v) {
        if(Cookie.getInstance().internet){
            new DatabaseThread().execute("select");
        }
    }

    public void onDeclinePressed(View v) {
        if(Cookie.getInstance().internet){
            currentChallenge.setStatus("declined");
            currentChallenge.updateLoginDate();
            new DatabaseThread().execute("");
            updateStatusElements();
            finish();
            Intent newIntent = new Intent(this, ChallengeOverviewActivity.class);
            this.startActivity(newIntent);
        }
    }

    public void onCommentPressed(View v) {
        if(Cookie.getInstance().internet){
            currentChallenge.updateLoginDate();
       	    new DatabaseThread().execute("");
            updateStatusElements();
            showPopup();
        }
    }

    private AlertDialog d;
    private EditText content;
    public void onApprovePressed(View v) {
        if(Cookie.getInstance().internet){
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.popup_approvement,null);
            content = (EditText)convertView.findViewById(R.id.txtApprovementExplained);
            currentChallenge.updateLoginDate();
            d = new AlertDialog.Builder(this)
                    .setView(convertView)
                    .show();
        }

    }

    public void onApproveEvidencePressed(View v){
        if (popUpValidation(content)) {
            currentChallenge.setRated("Approved");
            currentChallenge.setStatus("closed");
            currentChallenge.setRatedMessage(content.getText().toString());
            currentChallenge.updateLoginDate();
            new DatabaseThread().execute("");
            d.dismiss();
        }
    }

    public void onDisapproveEvidencePressed(View v){
        if (popUpValidation(content)) {
            currentChallenge.setStatus("closed");
            currentChallenge.setRated("Disapproved");
            currentChallenge.setRatedMessage(content.getText().toString());
            currentChallenge.updateLoginDate();
            new DatabaseThread().execute("");
            d.dismiss();
        }
    }

    private boolean popUpValidation(EditText content) {
        Boolean success = true;

        if (!Validation.hasText(content)) {
            success = false;
        }

        if (!success) {
            Toast.makeText(ChallengeViewActivity.this, "Please fill in everything", Toast.LENGTH_LONG).show();
            return false;
        } else {
            message.setAuthor(Cookie.getInstance().userName);
            message.setTitle("Evidence approvement");
            message.setReceiver(currentChallenge.getChallengee());
            message.setLiked("false");
            message.setContent(content.getText().toString());
            message.setDate(new Date());
            return true;
        }
    }

    public void updateStatusElements() {
        String temp = currentChallenge.getStatus();
        Boolean userMadeChallenge = false;

        if (currentChallenge.getChallenger().equals(Cookie.getInstance().userEntryId)) {
            userMadeChallenge = true;
        }

        Button accept = (Button) findViewById(R.id.btnAccept);
        Button decline = (Button) findViewById(R.id.btnDecline);
        Button complete = (Button) findViewById(R.id.btnComplete);

        if (temp.equals("new")) {
            if(userMadeChallenge){
                txtStatus.setText("Challenge is waiting for reply");
            }else{
                accept.setVisibility(View.VISIBLE);
                decline.setVisibility(View.VISIBLE);
                txtStatus.setText("The challenger is waiting for your reply");
            }
            txtStatus.setBackgroundColor(getResources().getColor(R.color.waitingChallenge));
            return;
        }

        if (temp.equals("accepted")) {
            if(userMadeChallenge){
                txtStatus.setText("Challenge has been accepted");
            }else{
                complete.setVisibility(View.VISIBLE);
                txtStatus.setText("You have accepted the challenge");

                //hide previous buttons
                accept.setVisibility(View.GONE);
                decline.setVisibility(View.GONE);
            }
            txtStatus.setBackgroundColor(getResources().getColor(R.color.acceptedChallenge));
            return;
        }

        if (temp.equals("closed")) {
            String text = txtStatus.getText().toString();
            text += " It has been "+currentChallenge.getRated().toLowerCase();
            txtStatus.setText(text);
            txtStatus.setVisibility(View.VISIBLE);
            LinearLayout buttonRow = (LinearLayout) findViewById(R.id.buttonRow);
            buttonRow.setVisibility(View.GONE);
            return;
        }

        if (temp.equals("completed")) {
            if(userMadeChallenge){
                //we only want the challenger to see this button
                Button approve = (Button) findViewById(R.id.btnApprove);
                approve.setVisibility(View.VISIBLE);
                txtStatus.setText("Evidence has been turned in");


                //text for the status bar

            }else{
                txtStatus.setText("You have turned in evidence");

            }

            Button evidence = (Button) findViewById(R.id.btnEvidence);
            evidence.setVisibility(View.VISIBLE);

            TextView evidenceText = (TextView)findViewById(R.id.txtEvidence);
            evidenceText.setVisibility(View.VISIBLE);

            txtStatus.setBackgroundColor(getResources().getColor(R.color.completedChallenge));

            //hide our complete button
            complete.setVisibility(View.GONE);

            return;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(ChallengeViewActivity.this, ChallengeOverviewActivity.class);
        startActivity(home);
        return;
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

    public void showPopup() {
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.popup_comment, null);

        //input for content for the comment
        final EditText content = (EditText) convertView.findViewById(R.id.txtContent);

        //listview so that the categoryf of the comment can be selected
       /* String categories[] = {"Motivational", "Meet Up", "Inspirational", "Other"};
        final ListView lv = (ListView) convertView.findViewById(R.id.lstCategories);
        lv.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categories);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                message.setCategory(lv.getItemAtPosition(i).toString());
            }
        });*/

        spCategories = (Spinner)convertView.findViewById(R.id.spCategories);

        final AlertDialog d = new AlertDialog.Builder(this)
                .setPositiveButton("Add Comment", null)
                .setNegativeButton("Cancel", null)
                .setView(convertView)
                .setTitle("Add a comment")
                .show();

        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Boolean success = true;

                if (!Validation.hasText(content)) {
                    success = false;
                }

                if (!success) {
                    Toast.makeText(ChallengeViewActivity.this, "Please fill in everything", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    message.setAuthor(Cookie.getInstance().userName);
                    message.setTitle("Comment");
                    message.setReceiver(currentChallenge.getChallengee());
                    message.setLiked("false");
                    message.setDate(new Date());
                    message.setCategory(spCategories.getSelectedItem().toString());
                    message.setContent(content.getText().toString());
                    new DatabaseThread().execute("unchanged");
                    updateMessagesInListview();
                }

                d.dismiss();
            }
        });
    }

    public void updateMessagesInListview() {
        if (messages != null) {
            BasicDBObject aMessage = messages.get(0);
            if(aMessage.get("Content")=="This challenge doesn't have comments."){
                messages.remove(0);
            }
            messages.add(message);
        }
        ((BaseAdapter) ((HeaderViewListAdapter) messagesListview.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
    }

    class DatabaseThread extends AsyncTask<String, String, Challenge> {
        private ProgressDialog simpleWaitDialog;
        private Boolean updateUI = false;
        private DB db = null;
        private DBCollection challengeCollection;
        private DBCollection userCollection;
        private Challenge current;
        private ArrayList<BasicDBObject> evidenceList;

        private String challengeeName = "";
        private String challengerName = "";

        @Override
        protected void onPreExecute() {
            simpleWaitDialog = ProgressDialog.show(ChallengeViewActivity.this,
                    "Please wait", "Processing");

        }

        protected void onPostExecute(Challenge result) {
            try {
                simpleWaitDialog.dismiss();
                simpleWaitDialog = null;
            } catch (Exception e) {
                // nothing
            }
            if (updateUI) {
                updateUI(challengerName, challengeeName);
            }
            currentChallenge.updateLoginDate();
        }

        protected Challenge doInBackground(String... args) {
            if(Cookie.getInstance().internet){
            try{
                MongoClient client = Database.getInstance();
                db = client.getDB(Database.uri.getDatabase());
                challengeCollection = db.getCollection("challenge");
                challengeCollection.setObjectClass(Challenge.class);

                userCollection = db.getCollection("user");
                userCollection.setObjectClass(User.class);

                // get the current challenge from database
                current = new Challenge();
                current.put("_id", new ObjectId(id));

                if (currentChallenge == null) {
                    //this variable tells us that the view needs to be constructed for use
                    updateUI = true;
                    currentChallenge = (Challenge) challengeCollection.findOne(current);


                    User challengeeUser = new User();
                    challengeeUser.put("facebookID", currentChallenge.getChallengee());
                    User newUser1 = (User) userCollection.find(challengeeUser).toArray().get(0);
                    challengeeName = newUser1.getName();

                    User challengerUser = new User();
                    challengerUser.put("facebookID", currentChallenge.getChallenger());
                    User newUser2 = (User) userCollection.find(challengerUser).toArray().get(0);
                    challengerName = newUser2.getName();
                    return null;
                }

                if (args[0].equals("select")) {
                    evidenceList = currentChallenge.getEvidence();
                    downloadEvidence();
                    return null;
                } else if (args[0].equals("unchanged")) {
                    //if the status is unchanged, we want to add a comment
                    addCommentToChallenge();
                    return null;
                } else if (args[0] == "") {
                    updateQuery();
                }

            }catch(Exception e){
                System.out.println(e);
            }
            }

            return null;
        }

        private void addCommentToChallenge() {
            Challenge challenge = (Challenge) challengeCollection.findOne(current);
            //for some reason we need to get the currentchallenge again from mongodb otherwise it won't update the document
            //long time= System.currentTimeMillis();
            //challengeCollection.update(challenge, new BasicDBObject("$set", new BasicDBObject("Date", time)));
            challengeCollection.update(challenge, new BasicDBObject("$push", new BasicDBObject("comments", message)));
            messageSend = true;
        }

        private void updateQuery() {
            if (currentChallenge.getStatus().equals("closed")) {
                if(currentChallenge.getRated().equals("Approved")){
                    updateXP();
                }
                evidenceList = currentChallenge.getEvidence();
                deleteEvidence();
                Intent redirectMe = new Intent(ChallengeViewActivity.this, ChallengeOverviewActivity.class);
                startActivity(redirectMe);
            }
            challengeCollection.findAndModify(current, currentChallenge);
        }

        private void updateXP() {
            DBCollection userCollection = db.getCollection("user");

            User match = new User();
            match.put("facebookID", currentChallenge.getChallengee().toString());

            User update = (User) userCollection.findOne(match);
            int reward = currentChallenge.getEvidenceAmount() * 300;
            try {
                update.setXP(update.getXP() + reward);
            } catch (Exception e) {
                update.setXP(reward);
            }

            userCollection.update(match, update);
        }

        private void deleteEvidence(){
            GridFS gfsPhoto = new GridFS(db, "challenge");

            for (BasicDBObject evidence : evidenceList) {
                String evidenceID = evidence.get("evidenceID").toString();
                gfsPhoto.remove(new ObjectId(evidenceID));
            }

            currentChallenge.setEvidence("removed");
        }

        private void downloadEvidence() {
            GridFS gfsPhoto = new GridFS(db, "challenge");

            for (BasicDBObject evidence : evidenceList) {
                String evidenceID = evidence.get("evidenceID").toString();

                GridFSDBFile image = gfsPhoto.findOne(new ObjectId(evidenceID));

                InputStream inputStream = image.getInputStream();

                OutputStream outputStream = null;

                try {
                    // write the inputStream to a FileOutputStream
                    outputStream = new FileOutputStream(new File(android.os.Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + image.getFilename() + ".jpg"));

                    int read = 0;
                    byte[] bytes = new byte[1024];

                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }

                    System.out.println("Done!");

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
