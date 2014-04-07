package alm.motiv.AlmendeMotivator;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by AsterLaptop on 4/7/14.
 */
public class ChallengeEvidence extends Activity {
    //keep track of camera capture intent
    final int CAMERA_CAPTURE = 1;

    //keep track of camera capture intent
    final int SELECT_PICTURE = 2;

    //captured picture uri
    private Uri picUri;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challengeevidence);

        Button captureBtn = (Button)findViewById(R.id.capture_btn);
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    try {
                        //use standard intent to capture an image
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //we will handle the returned data in onActivityResult
                        startActivityForResult(captureIntent, CAMERA_CAPTURE);
                    }catch(ActivityNotFoundException anfe){
                        //display an error message
                        String errorMessage = "Whoops - your device doesn't support capturing images!";
                        Toast toast = Toast.makeText(ChallengeEvidence.this, errorMessage, Toast.LENGTH_SHORT);
                        toast.show();
                    }
            }
        });

        Button browseBtn = (Button)findViewById(R.id.browsePicturseBtn);
        browseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    browseIntent.setType("image/*");
                    startActivityForResult(Intent.createChooser(browseIntent, "Select a picture"), CAMERA_CAPTURE);
                }catch(ActivityNotFoundException anfe){
                    String errorMessage = "Whoops - can't open your images!";
                    Toast toast = Toast.makeText(ChallengeEvidence.this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if(requestCode == CAMERA_CAPTURE){//user is returning from taking the image
                //get the Uri for the captured image
                picUri = data.getData();
                //retrieve a reference to the ImageView
                ImageView picView = (ImageView)findViewById(R.id.picture);
                //display the returned cropped image
                picView.setImageURI(picUri);
            }else if(requestCode == SELECT_PICTURE){
                Uri selectedImageUri = data.getData();
                ImageView picView = (ImageView)findViewById(R.id.picture);
                picView.setImageURI(selectedImageUri);
            }
        }
    }

}
