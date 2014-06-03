package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.models.Challenge;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.mongodb.gridfs.GridFSInputFile;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by AsterLaptop on 4/7/14.
 */
public class ChallengeEvidence extends Activity {
    //menu
    private String[] mMenuOptions;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private EditText amountHours;

    //keep track of camera capture intent
    final int CAMERA_CAPTURE = 1;

    //keep track of camera capture intent
    final int SELECT_PICTURE = 2;

    private int numberOfEvidence;

    private int numberOfCreatedEvidence = 0;

    //captured picture uri
    private Uri picUri;

    private AlertDialog helpDialog;

    private ProgressDialog simpleWaitDialog;

    private Intent home;

    //the intent that can contain extras
    private Intent intent;

    //the picture uri collection
    private ArrayList<String> pictureUriList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challengeevidence);

        //get extras from our intent
        intent = getIntent();

        //menu
        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //number of popup_evidence the challenger wants
        numberOfEvidence = Integer.valueOf(intent.getExtras().getInt("evidenceAmount"));
        //list in which we will store the uploaded pictures their URI
        pictureUriList = new ArrayList<String>();

        TextView challengeLabel = (TextView) findViewById(R.id.numberOfEvidenceLbl);
        amountHours = (EditText) findViewById(R.id.txtAmountHours);
        challengeLabel.setText("Your challenger wants you to upload " + numberOfEvidence + " photo(s)");
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

    public void addEvidence(View v) {
        showPopUp();
    }

    public void addReference(final Uri uri) {
        //reference to the picture uploaded
        LinearLayout theLayout = (LinearLayout) findViewById(R.id.evidenceRow);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 1, 0, 0);

        Button referenceButton = new Button(this);
        referenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "image/*");
                startActivity(intent);
            }
        });
        referenceButton.setText("Show evidence");
        referenceButton.setBackgroundColor(getResources().getColor(R.color.darkPurple));
        referenceButton.setTextColor(getResources().getColor(R.color.white));
        referenceButton.setTextSize(22);
        referenceButton.setGravity(Gravity.LEFT);
        referenceButton.setTextAppearance(this, R.style.button);
        referenceButton.setPadding(20, 20, 20, 20);
        referenceButton.setWidth(100);
        referenceButton.setLayoutParams(params);
        theLayout.addView(referenceButton);

        //Check if our challengee has enough popup_evidence
        numberOfCreatedEvidence = numberOfCreatedEvidence + 1;
        if (numberOfCreatedEvidence == numberOfEvidence) {
            //if so disable the add popup_evidence button
            Button addEvidenceBtn = (Button) findViewById(R.id.addEvidenceBtn);
            addEvidenceBtn.setVisibility(View.GONE);
            addEvidenceBtn.setEnabled(false);

            //Add our send button
            Button sendEvidence = new Button(this);
            sendEvidence.setText("Upload the evidence");
            sendEvidence.setBackgroundColor(getResources().getColor(R.color.darkPurple));
            sendEvidence.setTextColor(getResources().getColor(R.color.white));
            sendEvidence.setTextSize(22);
            sendEvidence.setGravity(Gravity.LEFT);
            sendEvidence.setTextAppearance(this, R.style.button);
            sendEvidence.setPadding(20, 20, 20, 20);
            sendEvidence.setWidth(100);
            sendEvidence.setLayoutParams(params);
            sendEvidence.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean allowed = true;
                    if (amountHours.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "You forgot to enter the amount of hours!", Toast.LENGTH_LONG).show();
                        allowed = false;
                    } else if (amountHours.getText().toString().length() > 2) {
                        Toast.makeText(getApplicationContext(), "The amount of hours can only be 2 decimals big", Toast.LENGTH_LONG).show();
                        allowed = false;
                    } else if (Integer.parseInt(amountHours.getText().toString()) - 1 == -1) {
                        Toast.makeText(getApplicationContext(), "Amount of hours can't be 0", Toast.LENGTH_LONG).show();
                        allowed = false;
                    }
                    if (allowed) {
                        new DatabaseThread().execute("insert");
                    }
                }
            });
            theLayout.addView(sendEvidence);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = this.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE) {//user is returning from taking the image
                //get the Uri for the captured image
                picUri = data.getData();
                addReference(picUri);
                try{
                    pictureUriList.add(getRealPathFromURI(picUri));
                }catch (Exception e){
                    System.out.println(e + "huh");
                }
            } else if (requestCode == SELECT_PICTURE) {
                picUri = data.getData();
                addReference(picUri);
                pictureUriList.add(getRealPathFromURI(picUri));
            }
        }
    }

    private void showPopUp() {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setView(inflater.inflate(R.layout.popup_evidence, null));

        helpDialog = helpBuilder.create();
        helpDialog.show();
    }

    public void browse(View v) {
        try {
            Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
            browseIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(browseIntent, "Select a picture"), CAMERA_CAPTURE);
            helpDialog.dismiss();
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - can't open your images!";
            Toast toast = Toast.makeText(ChallengeEvidence.this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void takePicture(View v) {
        try {
            //use standard intent to capture an image
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //we will handle the returned data in onActivityResult
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
            helpDialog.dismiss();
        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support capturing images!";
            Toast toast = Toast.makeText(ChallengeEvidence.this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    class DatabaseThread extends AsyncTask<String, String, byte[]> {

        @Override
        protected void onPreExecute() {
            simpleWaitDialog = ProgressDialog.show(ChallengeEvidence.this,
                    "Please wait", "Processing");

        }

        protected void onPostExecute(byte[] result) {
            try {
                simpleWaitDialog.dismiss();
                simpleWaitDialog = null;
            } catch (Exception e) {
                // nothing
            }
            Intent newIntent = new Intent(ChallengeEvidence.this, ChallengeOverviewActivity.class);
            startActivity(newIntent);
        }

        protected byte[] doInBackground(String... args) {
            if(Cookie.getInstance().internet){
                try{
                    MongoClient client = Database.getInstance();
                    DB db = client.getDB(Database.uri.getDatabase());

                    DBCollection challengeCollection = db.getCollection("challenge");
                    challengeCollection.setObjectClass(Challenge.class);

                    Challenge match = new Challenge();
                    match.put("_id", new ObjectId(intent.getExtras().getString("challengeid")));

                    if (args[0] == "insert") {
                        GridFS gfsPhoto = new GridFS(db, "challenge");

                        int counter = 1;
                        //loop through the picture uri list
                        for (String uri : pictureUriList) {
                            File imageFile = new File(uri);
                            GridFSInputFile gfsFile = null;
                            try {
                                gfsFile = gfsPhoto.createFile(imageFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            gfsFile.setFilename(intent.getExtras().getString("title") + counter);
                            gfsFile.save();

                            BasicDBObject evidence = new BasicDBObject();
                            evidence.put("evidenceID", gfsFile.getId().toString());

                            BasicDBObject setCarrier = new BasicDBObject();
                            setCarrier.put("gps", intent.getExtras().getString("gps"));
                            setCarrier.put("status", "completed");

                            long time= System.currentTimeMillis();
                            setCarrier.put("Date", time);
                            setCarrier.put("amountHours", amountHours.getText().toString());
                            setCarrier.put("endDate", new Date());

                            Challenge update = new Challenge();
                            //update the status of the challenge, so that the challenger knows he can check the popup_evidence
                            update.put("$set", setCarrier);

                            //put a reference to the popup_evidence picture in the challenge
                            update.put("$push", new BasicDBObject("evidence", evidence));


                            challengeCollection.update(match, update);
                            counter++;
                        }
                    }
                }catch (Exception e){
                    System.out.println(e);
                }
            }

            return null;
        }
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

    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(ChallengeEvidence.this, ChallengeOverviewActivity.class);
        startActivity(home);
        return;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Menu.selectItem(position, ChallengeEvidence.this);
        }
    }

}
