package siddharth.com.tagtraqr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by siddharthsingh on 12/01/16.
 */
public class NotificationReceiverActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.result);
        sendingsms();
    }
        public void sendingsms() {
            try {
                SmsManager manager = SmsManager.getDefault();
                manager.sendTextMessage("+919910803829", null, "Bank: SBI A/C: 10230809358329", null, null);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Sending SMS failed.",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            finish();
        }
}
