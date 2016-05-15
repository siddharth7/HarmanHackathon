package siddharth.com.tagtraqr;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class Login extends android.support.v4.app.Fragment implements View.OnClickListener {

    private TextView _signupLink;
    private Button mLoginbtn,mUpdatebtn;
    private CoordinatorLayout coordinatorLayout;
    private static final int RC_SIGN_IN = 0;
    //private static final String GMAIL_SCOPE = "oauth2:https://www.googleapis.com/auth/gmail.readonly";
    //private static final String GMAIL_SCOPE ="oauth2:https://www.googleapis.com/auth/plus.login ";
    private static final String GMAIL_SCOPE="oauth2:https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.me";

    private static final String TAG = "MAinLoginActivity";

    String accountName;
    private String success_login="false";
    DeviceDBDataSource dataSource;


    String token, devAddr="blank,", devLat="blank,", devLong="blank,",devName="blank,";
    public JSONObject json;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSource = new DeviceDBDataSource(getActivity());
        dataSource.open();
        List<Devices> tagList = dataSource.findAll();
        for(int i=0;i<tagList.size();i++)
        {
            devAddr+=tagList.get(i).getMac()+",";
            devLat+=tagList.get(i).getLati()+",";
            devLong+=tagList.get(i).getLongi()+",";
            devName+=tagList.get(i).getName()+",";
        }

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login,container, false);


        mLoginbtn = (Button) view.findViewById(R.id.btn_login);
        mUpdatebtn = (Button) view.findViewById(R.id.btn_update);
        _signupLink = (TextView) view.findViewById(R.id.link_login);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        mLoginbtn.setOnClickListener(this);
        mUpdatebtn.setOnClickListener(this);
        _signupLink.setOnClickListener(this);

        return view;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onClick(View v) {
        if(isNetworkAvailable())
        {
            switch (v.getId()) {
                case R.id.btn_login:
                    if(mLoginbtn.getText()== "LOGIN") {
                        Intent googlePicker = AccountPicker.newChooseAccountIntent(null,
                                null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE},
                                true, null, null, null, null);
                        startActivityForResult(googlePicker, 1);
                    }
                    else
                    {
                        mLoginbtn.setText("LOGIN");
                        _signupLink.setText("Click Login to access cloud");
                    }
                    break;
                case R.id.btn_update:
                    new RequestTask().execute("http://tagtraqr.com/register-by-token/google-oauth2/?access_token=" + token + "&macaddr=" + devAddr + "&device_lat=" + devLat + "&device_long=" + devLong + "&device_name=" + devName);
                    break;

            }
        }
        else
        {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No Internet Connection!", Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.DKGRAY);
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
    //    public static void RevokeAcess(String token2) throws ClientProtocolException, IOException
//    {
//        HttpClient client = new DefaultHttpClient();
//        HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/revoke?token="+token2);
//        client.execute(post);
//    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Log.d("acount name", accountName);

            new getAuthToken().execute();
        }
    }
    public class getAuthToken extends AsyncTask<String, String, String> {

        ProgressDialog dialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            // Show Progress dialog
            dialog.setMessage("Authenticating..");
            dialog.show();
        }
        @Override
        protected String doInBackground(String... arg0) {
            try {
                token = GoogleAuthUtil.getToken(getActivity(),
                        accountName, GMAIL_SCOPE);

            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            }

            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            Log.d("tokenval", s);
//            mLoginbtn.setVisibility(View.INVISIBLE);
            mLoginbtn.setText("LOGOUT");
            mUpdatebtn.setVisibility(View.VISIBLE);
            //_signupLink.setText("token:" + s);
            Intent ii= new Intent(getActivity(), siddharth.com.tagtraqr.MapsActivity.class);
            startActivity(ii);

        }
    }

    class RequestTask extends AsyncTask<String, String, String>{

        ProgressDialog dialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            // Show Progress dialog
            dialog.setMessage("Checking Server..");
            dialog.show();
        }
        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                Log.d("serversentdata",statusLine.toString());
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
            //Do anything with response..
            _signupLink.setText(result);
        }
    }



}
