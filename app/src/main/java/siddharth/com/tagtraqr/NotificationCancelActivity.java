package siddharth.com.tagtraqr;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by siddharthsingh on 09/03/16.
 */
public class NotificationCancelActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.result);
        cancelsms();
    }
    public void cancelsms() {
        try {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
        } catch (Exception e) {
        }
        finish();
    }
}
