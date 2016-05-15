//package siddharth.com.tagtraqr;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Binder;
//import android.os.Handler;
//import android.os.IBinder;
//import android.util.Log;
//import android.widget.Toast;
//
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//
//public class BleScanService extends Service implements BluetoothAdapter.LeScanCallback {
//
//    private final static String TAG = BleScanService.class.getSimpleName();
//
//    private final IBinder mBinder = new LocalBinder();
//
//    private BluetoothManager mBluetoothManager;
//
//    private BluetoothAdapter mBluetoothAdapter;
//    final BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
//
//
//    private String macAddress;
//    String deviceaddr;
//
//    private int flag1;
//    int timerflag=0;
//    long time1=0, time2=0;
//    private ScheduledExecutorService scheduleTaskExecutor;
//    ScheduledFuture<?> result;
//    ScheduledThreadPoolExecutor stpe;
//
//
//
//    public class LocalBinder extends Binder {
//        public BleScanService getService() {
//            return BleScanService.this;
//        }
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return mBinder;
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        return super.onUnbind(intent);
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        initialize();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        macAddress = intent.getStringExtra("Mac");
//        Log.d(TAG, "Service Started");
//        Log.e(TAG, "Device Address" + macAddress);
//
//        long timeToScan = 7000;
//        startScan(timeToScan);
//        flag1 = 1;
//        //return super.onStartCommand(intent, flags, startId);
//        return START_NOT_STICKY;
//    }
//
//    /**
//     * Initializes a reference to the local bluetooth adapter.
//     *
//     * @return Return true if the initialization is successful.
//     */
//    public boolean initialize() {
//        // For API level 18 and above, get a reference to BluetoothAdapter
//        // through
//        // BluetoothManager.
//        if (mBluetoothManager == null) {
//            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//            if (mBluetoothManager == null) {
//                Log.e(TAG, "Unable to initialize BluetoothManager.");
//                return false;
//            }
//        }
//
//        if (mBluetoothAdapter == null) {
//            mBluetoothAdapter = mBluetoothManager.getAdapter();
//            if (mBluetoothAdapter == null) {
//                Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
//                return false;
//            }
//        }
//
//        Log.d(TAG, "Initialzed scanner.");
//        return true;
//    }
//
//    /**
//     * Checks if bluetooth is correctly set up.
//     *
//     * @return
//     */
//    protected boolean isInitialized() {
//        return mBluetoothManager != null && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
//    }
//
//    /**
//     * Checks if ble is ready and bluetooth is correctly setup.
//     *
//     * @return
//     */
//    protected boolean isReady() {
//        return isInitialized() && isBleReady();
//    }
//
//    /**
//     * Checks if the device is ble ready.
//     *
//     * @return
//     */
//    protected boolean isBleReady() {
//        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
//    }
//
//    @Override
//    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//
//        if (macAddress.equals(device.getAddress())) {
//            Intent intent3 = new Intent(this, NotificationShareActivity.class);
//            intent3.putExtra("mac_address", device.getAddress());
//            PendingIntent pIntent3 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent3, 0);
//            Toast.makeText(BleScanService.this, "Reached LeScan", Toast.LENGTH_SHORT).show();
//            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//            Notification noti = new Notification.Builder(BleScanService.this)
//                    .setContentTitle("BagTraqr")
//                    .setContentText("Baggage has arrived").setSmallIcon(R.drawable.ic_add_alert_black_24dp)
//                    .setSound(soundUri)
//                    .build();
//
//            noti.flags = Notification.FLAG_AUTO_CANCEL;
//            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            notificationManager.notify(0, noti);
//
////                startdemoservice();
//        }
//    }
//
//    /**
//     * Starts the bluetooth low energy scan It scans at least the
//     * delayStopTimeInMillis.
//     *
//     * @param delayStopTimeInMillis
//     *            the duration of the scan
//     * @return <code>true</code> if the scan is successfully started.
//     */
//    public boolean startScan(long delayStopTimeInMillis) {
//        if (!isReady())
//            return false;
//
//        if (delayStopTimeInMillis <= 0) {
//            Log.w(TAG, "Did not start scanning with automatic stop delay time of " + delayStopTimeInMillis);
//                return false;
//            }
//
//            Log.d(TAG, "Auto-Stop scan after " + delayStopTimeInMillis + " ms");
////            getMainHandler().postDelayed(new Runnable() {
////
////                @Override
////                public void run() {
////                    Log.d(TAG, "Stopped scan.");
////                    stopScan();
////                }
////            }, delayStopTimeInMillis);
//        return startScan();
//    }
//
//    /**
//     * @return an handler with the main (ui) looper.
//     */
//
//
//
//    private Handler getMainHandler() {
//        return new Handler(getMainLooper());
//    }
//
//    /**
//     * Starts the bluetooth low energy scan. It scans without time limit.
//     *
//     * @return <code>true</code> if the scan is successfully started.
//     */
//    public boolean startScan() {
//
//        if (!isReady())
//            return false;
//
//            if (mBluetoothAdapter != null) {
//                Log.d(TAG, "Started scan.");
//                return mBluetoothAdapter.startLeScan(this);
//            } else {
//                Log.d(TAG, "BluetoothAdapter is null.");
//                return false;
//            }
//    }
//
//    /**
//     * Stops the bluetooth low energy scan.
//     */
//    public void stopScan() {
//        if (!isReady())
//            return;
//
//        if (mBluetoothAdapter != null)
//            mBluetoothAdapter.startLeScan(this);
//        else {
//            Log.d(TAG, "BluetoothAdapter is null.");
//        }
//    }
//    @Override
//    public void onDestroy() {
//        result.cancel(true);
//        mBluetoothAdapter.stopLeScan(this);
//        Log.d("pool size", String.valueOf(stpe.getPoolSize()));
//        Log.d("queue size",String.valueOf(stpe.getQueue().size()));
//        stpe.shutdown();
//        stopSelf();
//
//        Log.d("in destroy","Destroying");
//        super.onDestroy();
//        Log.d("in destroy", "Destroyed :)");
//    }
//
//
//
//}
package siddharth.com.tagtraqr;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BleScanService extends Service implements BluetoothAdapter.LeScanCallback {

    private final static String TAG = BleScanService.class.getSimpleName();

    private final IBinder mBinder = new LocalBinder();

    private BluetoothManager mBluetoothManager;

    private BluetoothAdapter mBluetoothAdapter;
    final BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();


    private String macAddress;
    String deviceaddr;

    private int flag1;
    int timerflag=0;
    long time1=0, time2=0;
    private ScheduledExecutorService scheduleTaskExecutor;
    ScheduledFuture<?> result;
    ScheduledThreadPoolExecutor stpe;



    public class LocalBinder extends Binder {
        public BleScanService getService() {
            return BleScanService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        macAddress = intent.getStringExtra("Mac");
        Log.d(TAG, "Service Started");
        Log.e(TAG, "Device Address" + macAddress);

        long timeToScan = 7000;
        startScan(timeToScan);
        flag1 = 1;
        //return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    /**
     * Initializes a reference to the local bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter == null) {
                Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
                return false;
            }
        }

        Log.d(TAG, "Initialzed scanner.");
        return true;
    }

    /**
     * Checks if bluetooth is correctly set up.
     *
     * @return
     */
    protected boolean isInitialized() {
        return mBluetoothManager != null && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    /**
     * Checks if ble is ready and bluetooth is correctly setup.
     *
     * @return
     */
    protected boolean isReady() {
        return isInitialized() && isBleReady();
    }

    /**
     * Checks if the device is ble ready.
     *
     * @return
     */
    protected boolean isBleReady() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

        if(macAddress.equals(device.getAddress()))
        {
            if(timerflag==0){
                timerflag=1;
                Toast.makeText(BleScanService.this, "Tracking initiated", Toast.LENGTH_SHORT).show();
                stpe = (ScheduledThreadPoolExecutor)
                        Executors.newScheduledThreadPool(1);
                Runnable delayTask = new Runnable(){
                    @Override
                    public void run() {
                        try{
                            Log.d("time", "started1");
                            Thread.sleep(11000);
                            Log.d("time","ended1");
                        }catch(Exception e){

                        }
                    }
                };
                result= stpe.scheduleWithFixedDelay(delayTask, 0, 1, TimeUnit.SECONDS);

            }
            else if(timerflag==1)
            {
                result.cancel(true);
                Log.d("pool size", String.valueOf(stpe.getPoolSize()));
                Log.d("queue size",String.valueOf(stpe.getQueue().size()));
                stpe.shutdown();
                Intent intent = new Intent(this, NotificationReceiverActivity.class);
                Intent intent2 = new Intent(this, NotificationCancelActivity.class);
                Intent intent3 = new Intent(this, NotificationShareActivity.class);
                intent3.putExtra("mac_address", device.getAddress());
                PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
                PendingIntent pIntent2 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent2, 0);
                PendingIntent pIntent3 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent3, 0);

                Toast.makeText(BleScanService.this, "Reached LeScan", Toast.LENGTH_SHORT).show();
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                final Notification noti = new Notification.Builder(BleScanService.this)
                        .setContentTitle("Wallet Stolen")
                        .setContentText("Would you like to inform banks").setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                        .setSound(soundUri)
                        .addAction(R.drawable.ic_check_black_24dp, "Yes", pIntent)
                        .addAction(R.drawable.ic_share_black_24dp,"Share",pIntent3)
                        .addAction(R.drawable.ic_close_black_24dp, "No", pIntent2)
                        .build();

                noti.flags = Notification.FLAG_AUTO_CANCEL;
                final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                stpe = (ScheduledThreadPoolExecutor)
                        Executors.newScheduledThreadPool(1);
                Runnable delayTask = new Runnable(){
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(15000);
                            Log.d("time", "started2");
                            notificationManager.notify(0, noti);
                            Log.d("time", "ended2");
                        }catch(Exception e){

                        }
                    }
                };
                result= stpe.scheduleWithFixedDelay(delayTask, 0, 1, TimeUnit.SECONDS);

            }
        }

        Log.d(TAG, "Found ble device " + device.getName() + " " + device.getAddress() + " " + rssi);
        if (macAddress.equals(device.getAddress())) {
            deviceaddr=macAddress;
            Log.d("aya", "hurray");
            //Toast.makeText(BleScanService.this, "scan value" + rssi, Toast.LENGTH_SHORT).show();
        }



        if (rssi < -94) {
            if (macAddress.equals(device.getAddress())) {
                Log.d("Reached In onLeScan", "Yes");
                Log.d("Mac From Intent; ", macAddress);
                Log.d("MAC from service ", device.getAddress());
            }
            if (macAddress.equals(device.getAddress()))
            {
                Intent intent = new Intent(this, NotificationReceiverActivity.class);
                Intent intent2 = new Intent(this, NotificationCancelActivity.class);
                Intent intent3 = new Intent(this, NotificationShareActivity.class);
                intent3.putExtra("mac_address", device.getAddress());
                PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
                PendingIntent pIntent2 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent2, 0);
                PendingIntent pIntent3 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent3, 0);
                Toast.makeText(BleScanService.this, "Reached LeScan", Toast.LENGTH_SHORT).show();
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                Notification noti = new Notification.Builder(BleScanService.this)
                        .setContentTitle("Wallet Stolen")
                        .setContentText("Would you like to inform banks").setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                        .setSound(soundUri)
                        .addAction(R.drawable.ic_check_black_24dp, "Yes", pIntent)
                        .addAction(R.drawable.ic_share_black_24dp,"Share",pIntent3)
                        .addAction(R.drawable.ic_close_black_24dp, "No", pIntent2)
                        .build();

                noti.flags = Notification.FLAG_AUTO_CANCEL;
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, noti);

//                startdemoservice();
            }
        }
    }

    /**
     * Starts the bluetooth low energy scan It scans at least the
     * delayStopTimeInMillis.
     *
     * @param delayStopTimeInMillis
     *            the duration of the scan
     * @return <code>true</code> if the scan is successfully started.
     */
    public boolean startScan(long delayStopTimeInMillis) {
        if (!isReady())
            return false;

        if (delayStopTimeInMillis <= 0) {
            Log.w(TAG, "Did not start scanning with automatic stop delay time of " + delayStopTimeInMillis);
            return false;
        }

        Log.d(TAG, "Auto-Stop scan after " + delayStopTimeInMillis + " ms");
//            getMainHandler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    Log.d(TAG, "Stopped scan.");
//                    stopScan();
//                }
//            }, delayStopTimeInMillis);
        return startScan();
    }

    /**
     * @return an handler with the main (ui) looper.
     */



    private Handler getMainHandler() {
        return new Handler(getMainLooper());
    }

    /**
     * Starts the bluetooth low energy scan. It scans without time limit.
     *
     * @return <code>true</code> if the scan is successfully started.
     */
    public boolean startScan() {

        if (!isReady())
            return false;

        if (mBluetoothAdapter != null) {
            Log.d(TAG, "Started scan.");
            return mBluetoothAdapter.startLeScan(this);
        } else {
            Log.d(TAG, "BluetoothAdapter is null.");
            return false;
        }
    }

    /**
     * Stops the bluetooth low energy scan.
     */
    public void stopScan() {
        if (!isReady())
            return;

        if (mBluetoothAdapter != null)
            mBluetoothAdapter.startLeScan(this);
        else {
            Log.d(TAG, "BluetoothAdapter is null.");
        }
    }
    @Override
    public void onDestroy() {
        result.cancel(true);
        mBluetoothAdapter.stopLeScan(this);
        Log.d("pool size", String.valueOf(stpe.getPoolSize()));
        Log.d("queue size",String.valueOf(stpe.getQueue().size()));
        stpe.shutdown();
        stopSelf();

        Log.d("in destroy","Destroying");
        super.onDestroy();
        Log.d("in destroy", "Destroyed :)");
    }



}