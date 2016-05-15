package siddharth.com.tagtraqr;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by siddharthsingh on 10/03/16.
 */
public class NotificationShareActivity extends AppCompatActivity {
    TextView mac;
    ListView lis;
    public ArrayList<String> getNameEmailDetails() {
        ArrayList<String> emlRecs = new ArrayList<String>();
        HashSet<String> emlRecsHS = new HashSet<String>();
        Context context = getApplicationContext();
        ContentResolver cr = context.getContentResolver();
        String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";
        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
        if (cur.moveToFirst()) {
            do {
                // names comes in hand sometimes
                String name = cur.getString(1);
                String emlAddr = cur.getString(3);

                // keep unique only
                if (emlRecsHS.add(emlAddr.toLowerCase())) {
                    emlRecs.add(emlAddr);
                }
            } while (cur.moveToNext());
        }

        cur.close();
        Collections.sort(emlRecs);
        return emlRecs;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Intent intent = getIntent();
        Log.d("share","in share");
        final String mac_addr=intent.getStringExtra("mac_address");
        mac=(TextView) findViewById(R.id.mac_addr_view);
        lis=(ListView) findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                getNameEmailDetails());
        lis.setAdapter(arrayAdapter);
        mac.setText(mac_addr);
        lis.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object o = lis.getItemAtPosition(position);
                String str = (String) o;//As you are using Default String Adapter
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", str, null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Track your friend's device using TagTraqr");
                intent.putExtra(Intent.EXTRA_TEXT, "Hi " + str +
                        ", I lost a device and would like you to track it. Please add this mac address " +
                        mac_addr + " and start tracking");
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });
    }
}
