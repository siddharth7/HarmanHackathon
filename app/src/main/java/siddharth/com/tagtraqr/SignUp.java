package siddharth.com.tagtraqr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SignUp extends Activity implements View.OnClickListener{

    EditText name,password,email,password2;
    Button signup;
    private String mname,mpassword,memail;
    private String result = "";
    public JSONObject json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name=(EditText)findViewById(R.id.name);
        password=(EditText)findViewById(R.id.password);
        password2=(EditText)findViewById(R.id.password_confirm);
        email=(EditText)findViewById(R.id.email);
        signup=(Button)findViewById(R.id.btnCreate_acc);
        signup.setOnClickListener(this);
    }
    public String POST(String url, String name, String password, String email ){
        InputStream inputStream = null;
        try {

            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url);

            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username",name);
            jsonObject.put("password", password);
            jsonObject.put("email", email);
            Log.d("json obj", String.valueOf(jsonObject));
            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        Log.d("final result", result);
        return result;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.btnCreate_acc:
                if(validate()) {
                    // call AsynTask to perform network operation on separate thread
                    new HttpAsyncTask().execute("http://128.199.83.48/users/");
                }
                break;
        }
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog dialog = new ProgressDialog(SignUp.this);

        protected void onPreExecute() {
            // Show Progress dialog
            dialog.setMessage("Registering..");
            dialog.show();
        }
        @Override
        protected String doInBackground(String... urls) {

            mname=name.getText().toString();
            memail=email.getText().toString();
            mpassword=password.getText().toString();

            return POST(urls[0],mname,mpassword,memail);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            Toast.makeText(getBaseContext(), "Check Email", Toast.LENGTH_LONG).show();
            try {
                json = new JSONObject(result);
                Log.d("Data recieved from server", String.valueOf(json));
//                JSONObject json_LL = json.getJSONObject("LL");
//                success_login=json.getString("success");
//                Log.d("success value",success_login);
//                Log.d("HTTP", "HTTP: OK");
//                Log.d("token received",result);
               Intent i = new Intent(getApplicationContext(), MainPage.class);
                startActivity(i);
                finish();
//                }
//                else
//                {
//                    Toast.makeText(getApplicationContext(),"Authentication error",Toast.LENGTH_SHORT).show();
//
//                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
    public boolean validate() {
        boolean valid = true;

        String checkname = name.getText().toString();
        String checkemail =  email.getText().toString();
        String checkpassword = password.getText().toString();
        String confirmpassword = password2.getText().toString();

        if (checkname.isEmpty()) {
            name.setError("Cannot be Empty");
            valid = false;
        } else {
            name.setError(null);
        }
        if (checkemail.isEmpty(

        ) || !android.util.Patterns.EMAIL_ADDRESS.matcher(checkemail).matches()) {
            email.setError("Invalid Email");
            valid = false;
        } else {
            email.setError(null);
        }

        if (checkpassword.isEmpty() || checkpassword.length() < 4) {
            password.setError("Greater than 4 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
        }
        if(!(checkpassword.equals(confirmpassword))){
            password.setError("Password Donot Match");
            valid  = false;
        }else {
            password.setError(null);
        }
        return valid;
    }
}
