package siddharth.com.tagtraqr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Date;


public class BarcodeScanner extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {

    TextView macAdd;
    EditText WalletName;
    EditText CardNo;
    EditText Bank1;
    EditText Bank2;
    EditText CardBank1;
    EditText CardBank2;
    ShowcaseView sv;
    ShowcaseView sv1;
    private int N;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    DeviceDBDataSource dataSource;
    private static final String TAG = "DeviceAddActivity";
    protected LocationSettingsRequest mLocationSettingsRequest;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;
    // Labels.
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected String mLastUpdateTimeLabel;

    protected Location mLastLocation;
    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private byte[] image;
    // Stops scanning after 10 seconds.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "Turn On Location Services", Toast.LENGTH_LONG).show();
//        Intent gpsOptionsIntent = new Intent(
//                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivity(gpsOptionsIntent);
        dataSource = new DeviceDBDataSource(this);
        dataSource.open();
        setContentView(R.layout.activity_barcode_scanner);
        macAdd = (TextView)findViewById(R.id.mac_address);
        WalletName = (EditText)findViewById(R.id.input_name);
        CardNo = (EditText)findViewById(R.id.num_add);
        Bank1 = (EditText)findViewById(R.id.bank_name_1);
        Bank2 = (EditText)findViewById(R.id.bank_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Snackbar.make(findViewById(android.R.id.content), "Scan QR Code On Tag To Add", Snackbar.LENGTH_LONG)
                .show();
        // Set labels.
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLastUpdateTimeLabel = getResources().getString(R.string.last_update_time_label);

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
//        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        ViewTarget target = new ViewTarget(R.id.button_scan, this);
        sv = new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setTarget(target)
                .setContentTitle("Add Bag")
                .setContentText("Scan The QR Code to Add")
                .setStyle(R.style.CustomShowcaseTheme2)
                .singleShot(42)
                .build();
        sv.setButtonPosition(lps);
//        ViewTarget target1 = new ViewTarget(R.id.button_scan, this);
//        sv1 = new ShowcaseView.Builder(this)
//                .withMaterialShowcase()
//                .setTarget(target1)
//                .setContentTitle("Done")
//                .setContentText("Click When All Fields Filled")
//                .setStyle(R.style.CustomShowcaseTheme2)
//                .singleShot(42)
//                .build();
//        sv1.setButtonPosition(lps);

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();
    }
    public void onMacClick(View v) {
        if(v.getId() == R.id.button_scan) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_barcode_scanner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.tick_action) {

            new RequestTask().execute();
//            Intent i = new Intent(BarcodeScanner.this, MainPage.class);
//
//            startActivity(i);
//            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(BarcodeScanner.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }

    }

    class RequestTask extends AsyncTask<String, String, String> {

        ProgressDialog dialog = new ProgressDialog(BarcodeScanner.this);
        @Override
        protected void onPreExecute() {
            // Show Progress dialog
            dialog.setMessage("Adding Device");
            dialog.show();
        }
        @Override
        protected String doInBackground(String... uri) {
//            startLocationUpdates();
//            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//            if (mCurrentLocation == null) {
//                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Log.d("Latitude",String.valueOf(mLastLocation.getLatitude()));
                Log.d("Latitude",String.valueOf(mLastLocation.getLongitude()));
//                mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel,
//                        mLastLocation.getLatitude()));
//                mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel,
//                        mLastLocation.getLongitude()));
            } else {
                Log.d("No location Found","I am lost");
            }
            Devices tag = new Devices();
            tag.setMac(macAdd.getText().toString());
            tag.setName(WalletName.getText().toString());
            tag.setName(WalletName.getText().toString());
            tag.setLati(mLastLocation.getLatitude());
            tag.setLongi(mLastLocation.getLongitude());
//                tag.setImage(image);
            tag = dataSource.create(tag);
            Log.i(TAG, "Device created with id: " + tag.getId());
            return "done";
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
            Intent i = new Intent(BarcodeScanner.this, MainPage.class);

            startActivity(i);
            finish();
        }
    }
    public void onBackPressed() {
        dataSource.close();
        finish();
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_pirates:
                if (checked) {
                    // Pirates are the best
                    CardNo.setVisibility(View.VISIBLE);
                    Bank1.setVisibility(View.VISIBLE);
                    Bank2.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.radio_ninjas:
                if (checked) {
                    // Ninjas rule
                    CardNo.setVisibility(View.INVISIBLE);
                    Bank1.setVisibility(View.INVISIBLE);
                    Bank2.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            image = getBytes(imageBitmap);
//        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                macAdd.setText(result.getContents());
//                Toast.makeText(this, "Add Photo of Wallet" + result.getContents(), Toast.LENGTH_LONG).show();
//                dispatchTakePictureIntent();
//
//                Devices tag = new Devices();
//                tag.setMac(result.getContents());
//                tag.setName(WalletName.getText().toString());
////                tag.setImage(image);
//
//                tag = dataSource.create(tag);
//                Log.i(TAG, "Device created with id: " + tag.getId());
            }
        } else {
            Log.d("MainActivity", "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

//    private void updateValuesFromBundle(Bundle savedInstanceState) {
//        Log.i(TAG, "Updating values from bundle");
//        if (savedInstanceState != null) {
//            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
//            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
//            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
//                mRequestingLocationUpdates = savedInstanceState.getBoolean(
//                        REQUESTING_LOCATION_UPDATES_KEY);
////                setButtonsEnabledState();
//            }
//
//            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
//            // correct latitude and longitude.
//            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
//                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
//                // is not null.
//                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
//            }
//
//            // Update the value of mLastUpdateTime from the Bundle and update the UI.
//            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
//                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
//            }
////            updateUI();
//        }
//    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//
//        // Sets the desired interval for active location updates. This interval is
//        // inexact. You may not receive updates at all if no location sources are available, or
//        // you may receive them slower than requested. You may also receive updates faster than
//        // requested if other applications are requesting location at a faster interval.
//        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
//
//        // Sets the fastest rate for active location updates. This interval is exact, and your
//        // application will never receive updates faster than this value.
//        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
//
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }

//    public void startUpdatesButtonHandler(View view) {
//        if (!mRequestingLocationUpdates) {
//            mRequestingLocationUpdates = true;
////            setButtonsEnabledState();
//            startLocationUpdates();
//        }
//    }

//    public void stopUpdatesButtonHandler(View view) {
//        if (mRequestingLocationUpdates) {
//            mRequestingLocationUpdates = false;
////            setButtonsEnabledState();
//            stopLocationUpdates();
//        }
//    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
//    protected void startLocationUpdates() {
//        // The final argument to {@code requestLocationUpdates()} is a LocationListener
//        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
//        LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient, mLocationRequest, this);
//    }
//
////    private void setButtonsEnabledState() {
////        if (mRequestingLocationUpdates) {
////            mStartUpdatesButton.setEnabled(false);
////            mStopUpdatesButton.setEnabled(true);
////        } else {
////            mStartUpdatesButton.setEnabled(true);
////            mStopUpdatesButton.setEnabled(false);
////        }
////    }
//
//    /**
//     * Removes location updates from the FusedLocationApi.
//     */
//    protected void stopLocationUpdates() {
//        // It is a good practice to remove location requests when the activity is in a paused or
//        // stopped state. Doing so helps battery performance and is especially
//        // recommended in applications that request frequent location updates.
//
//        // The final argument to {@code requestLocationUpdates()} is a LocationListener
//        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
//        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//    }
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
//            mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel,
//                    mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel,
//                    mLastLocation.getLongitude()));
        } else {
//            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

//    /**
//     * Callback that fires when the location changes.
//     */
//    @Override
//    public void onLocationChanged(Location location) {
//        mCurrentLocation = location;
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
////        updateUI();
////        Toast.makeText(this, getResources().getString(R.string.location_updated_message),
////                Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onConnectionSuspended(int cause) {
//        // The connection to Google Play services was lost for some reason. We call connect() to
//        // attempt to re-establish the connection.
//        Log.i(TAG, "Connection suspended");
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
//        // onConnectionFailed.
//        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
//    }


    /**
     * Stores activity data in the Bundle.
     */
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
//        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
//        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
//        super.onSaveInstanceState(savedInstanceState);
//    }
}