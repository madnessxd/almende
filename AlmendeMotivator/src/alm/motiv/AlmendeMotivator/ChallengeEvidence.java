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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by AsterLaptop on 4/7/14.
 */
public class ChallengeEvidence extends Activity {
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
        Intent intent = getIntent();

        //TODO fill with challenge data
        //number of evidence the challenger wants
        numberOfEvidence = Integer.valueOf(intent.getExtras().getString("evidenceAmount"));
        //list in which we will store the uploaded pictures their URI
        pictureUriList = new ArrayList<String>();

        TextView challengeLabel = (TextView) findViewById(R.id.numberOfEvidenceLbl);

        challengeLabel.setText("Your challenger wants you to upload " + numberOfEvidence + " photo's");
    }

    public void addEvidence(View v) {
        showPopUp();
    }

    public void addReference(final Uri uri) {
        //reference to the picture uploaded
        LinearLayout theLayout = (LinearLayout) findViewById(R.id.evidenceRow);

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
        theLayout.addView(referenceButton);

        //Check if our challengee has enough evidence
        numberOfCreatedEvidence = numberOfCreatedEvidence + 1;
        if (numberOfCreatedEvidence == 3) {
            //if so disable the add evidence button
            Button addEvidenceBtn = (Button) findViewById(R.id.addEvidenceBtn);
            addEvidenceBtn.setEnabled(false);

            System.out.println(pictureUriList);
            //Add our send button
            Button sendEvidence = new Button(this);
            sendEvidence.setText("Send evidence");
            sendEvidence.setWidth(100);
            sendEvidence.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatabaseThread().execute("insert");
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
                pictureUriList.add(getRealPathFromURI(picUri));
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
        helpBuilder.setView(inflater.inflate(R.layout.evidence, null));

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
            Log.i("Async-Example", "onPreExecute Called");
            simpleWaitDialog = ProgressDialog.show(ChallengeEvidence.this,
                    "Please wait", "Processing");

        }

        protected void onPostExecute(byte[] result) {
            Log.i("Async-Example", "onPostExecute Called");
            simpleWaitDialog.setMessage("Process completed.");
            simpleWaitDialog.dismiss();

        }

        protected byte[] doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());

            DBCollection challengeCollection = db.getCollection("challenge");
            challengeCollection.setObjectClass(Challenge.class);

            //TODO make dynamic. Use challenge data.
            Challenge match = new Challenge();
            match.put("_id", new ObjectId(intent.getExtras().getString("challengeid")));

            if (args[0] == "insert") {
                GridFS gfsPhoto = new GridFS(db, "challenge");

                //loop through the picture uri list
                for (String uri : pictureUriList) {
                    File imageFile = new File(uri);
                    GridFSInputFile gfsFile = null;
                    try {
                        gfsFile = gfsPhoto.createFile(imageFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    gfsFile.setFilename(intent.getExtras().getString("title"));
                    gfsFile.save();

                    BasicDBObject evidence = new BasicDBObject();
                    evidence.put("evidenceID", gfsFile.getId().toString());

                    BasicDBObject update = new BasicDBObject();
                    update.put("$push", new BasicDBObject("evidence", evidence));
                    //with the evidenceSubmitted field the challenger knows he can check the submitted evidence
                    update.put("evidenceSubmitted", "true");

                    challengeCollection.update(match, update);
                }

            } else if (args[0] == "somethingelse") {
                GridFS gfsPhoto = new GridFS(db, "challenge");
                //TODO set this piece of code where the challengee will validate the evidence and use challenge data
                GridFSDBFile image = gfsPhoto.findOne(new ObjectId("53458331726c7e2f54bd529f"));

                InputStream inputStream = image.getInputStream();

                OutputStream outputStream = null;

                try {

                    // write the inputStream to a FileOutputStream
                    outputStream =
                            new FileOutputStream(new File(android.os.Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/temp.jpg"));

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
                            // outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(ChallengeEvidence.this, MainMenuActivity.class);
        startActivity(home);
        return;
    }

}
