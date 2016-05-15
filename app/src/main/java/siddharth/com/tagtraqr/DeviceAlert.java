package siddharth.com.tagtraqr;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class DeviceAlert extends Service {
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
//    private static Timer timer = new Timer();

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 1 seconds.
    private static final long SCAN_PERIOD = 2000;

    public DeviceAlert() {
    }

    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mHandler = new Handler();
//        Handler2 = new Handler();
//        Handler2.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(DeviceAlert.this,"Service Run Again", Toast.LENGTH_SHORT).show();
//                scanLeDevice(true);
//            }
//        }, 2*SCAN_PERIOD);
        scanLeDevice(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Service Destroy", Toast.LENGTH_SHORT).show();
    }
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            Toast.makeText(DeviceAlert.this,"Service Run", Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if(rssi > -100)
                    {
                        // Send Notification
                        Toast.makeText(DeviceAlert.this,"Reached LeScan", Toast.LENGTH_SHORT).show();
                        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Notification noti = new Notification.Builder(DeviceAlert.this)
                                .setContentTitle("Baggage Has Arrived")
                                .setContentText("Baggage has arived on the belt")
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setSound(soundUri)
                                .build();

                        noti.flags = Notification.FLAG_AUTO_CANCEL;
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(0, noti);
                    }
                }
            };
}