package siddharth.com.tagtraqr;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by siddharthsingh on 26/01/16.
 */
public class MainLoginActivity extends Activity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;
    private static final String GMAIL_SCOPE = "oauth2:https://www.googleapis.com/auth/gmail.readonly";

    private static final String TAG = "MAinLoginActivity";

    String accountName;
    private String success_login="false";


    private Button btnSignIn;
    TextView text;

    String token;
    public JSONObject json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_access_token);

        btnSignIn = (Button) findViewById(R.id.nextbtn);
        btnSignIn.setOnClickListener(this);
        //text=(TextView)findViewById(R.id.token_value);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Log.d("acount name", accountName);

            new getAuthToken().execute();
        }
    }


    public class getAuthToken extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(),
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
            Log.d("tokenval", s);
            //text.append("token"+s);
            Intent i = new Intent(MainLoginActivity.this,
                    siddharth.com.tagtraqr.MainPage.class);
            startActivity(i);
            finish();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.nextbtn)
        {
            Intent googlePicker = AccountPicker.newChooseAccountIntent(null,
                    null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE},
                    true, null, null, null, null);
            startActivityForResult(googlePicker, 1);

        }

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
