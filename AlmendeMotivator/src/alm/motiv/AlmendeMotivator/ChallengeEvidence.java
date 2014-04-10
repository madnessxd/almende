package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.models.Challenge;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.squareup.picasso.Picasso;
import org.apache.http.util.ByteArrayBuffer;
import org.bson.types.ObjectId;

import java.io.*;

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
    private String realpath;

    private AlertDialog helpDialog;

    private ProgressDialog simpleWaitDialog;
    ImageView temp;


    private Intent home;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challengeevidence);

       temp = (ImageView)findViewById(R.id.tempimg);

       TextView challengeLabel = (TextView)findViewById(R.id.numberOfEvidenceLbl);
        //TODO fill with challenge data
        numberOfEvidence=3;
        challengeLabel.setText("Your challenger wants you to upload " +numberOfEvidence+" photo's");
        }

    public void addEvidence(View v){
        showPopUp();
    }

    public void addReference(final Uri uri){
        //reference to the picture uploaded
        LinearLayout theLayout = (LinearLayout) findViewById(R.id.evidenceRow);

        Button referenceButton = new Button(this);
        referenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //realpath = getRealPathFromURI(picUri);
                //new DatabaseThread().execute();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "image/*");
                startActivity(intent);
            }
        });
        referenceButton.setText("Show evidence");
        theLayout.addView(referenceButton);

        //Check if our challengee has enough evidence
        numberOfCreatedEvidence= numberOfCreatedEvidence+1;
        if(numberOfCreatedEvidence==3){
            //if so disable the add evidence button
            Button addEvidenceBtn = (Button)findViewById(R.id.addEvidenceBtn);
            addEvidenceBtn.setEnabled(false);

            //Add our send button
            Button sendEvidence = new Button(this);
            sendEvidence.setText("Send evidence");
            sendEvidence.setEnabled(false);
            sendEvidence.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // new DatabaseThread().execute();
                }
            });
            theLayout.addView(sendEvidence);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = this.getContentResolver().query(contentUri,  proj, null, null, null);
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
            if(requestCode == CAMERA_CAPTURE){//user is returning from taking the image
                //get the Uri for the captured image
                picUri = data.getData();
                addReference(picUri);
            }else if(requestCode == SELECT_PICTURE){
                picUri = data.getData();
                addReference(picUri);
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

    public void browse(View v){
        try{
            Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
            browseIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(browseIntent, "Select a picture"), CAMERA_CAPTURE);
            helpDialog.dismiss();
        }catch(ActivityNotFoundException anfe){
            String errorMessage = "Whoops - can't open your images!";
            Toast toast = Toast.makeText(ChallengeEvidence.this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void takePicture(View v){
        try {
            //use standard intent to capture an image
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //we will handle the returned data in onActivityResult
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
            helpDialog.dismiss();
        }catch(ActivityNotFoundException anfe){
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
                    "Wait", "Downloading Image");

        }

        protected void onPostExecute(byte[] result) {
            Log.i("Async-Example", "onPostExecute Called");
            simpleWaitDialog.dismiss();

        }
        protected byte[] doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());

            /*File imageFile = new File(realpath);
            GridFS gfsPhoto = new GridFS(db, "challenge");
            GridFSInputFile gfsFile = null;
            try {
                gfsFile = gfsPhoto.createFile(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            gfsFile.setFilename("img");
            gfsFile.save();*/

            GridFS gfsPhoto = new GridFS(db, "challenge");

            GridFSDBFile image = gfsPhoto.findOne(new ObjectId("53458331726c7e2f54bd529f"));

            InputStream is = image.getInputStream();


            /*GridFSInputFile file = myChallenges.createFile(data);
                file.setFilename("tempppp");
                file.save();
                String id = file.getId().toString();

            GridFS gfsPhoto = new GridFS(db, "challenge");

            GridFSDBFile image = gfsPhoto.findOne(new ObjectId(id));

            InputStream stream = image.getInputStream();*/
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
