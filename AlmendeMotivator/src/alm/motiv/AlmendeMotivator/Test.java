package alm.motiv.AlmendeMotivator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Test extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    EditText inputTitle;
    EditText inputPrice;
    EditText inputDesc;

    // url to create new product
   // private static String url_create_product = "https://oege.ie.hva.nl/~schulta001/almende/create.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        System.out.println("1");
        // Edit Text
       inputTitle = (EditText) findViewById(R.id.txtTitle);

        // Create button
        Button btnCreateProduct = (Button) findViewById(R.id.btnSubmit);

        // button click event
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                new CreateTest().execute();
            }
        });
    }

    /**
     * Background Async Task to Create new product
     * */
    class CreateTest extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pDialog = new ProgressDialog(NewProductActivity.this);
            pDialog.setMessage("Creating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();*/
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            HttpResponse response = null;
            String title = inputTitle.getText().toString();
            System.out.println(title);

            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost post = new HttpPost("http://145.109.160.96/almende/create.php");

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("title", title));

            try {
                post.setEntity(new UrlEncodedFormEntity(params));
                response = httpclient.execute(post);
            }catch (Exception e) {
                System.out.println("PANIEK"+e);
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            //pDialog.dismiss();
        }

    }
}