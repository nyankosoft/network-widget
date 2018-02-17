//
//  Copyright 2015  Google Inc. All Rights Reserved.
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.

//package com.google.example.wificonnected;
package com.udacity.horatio.widgetexample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.udacity.horatio.widgetexample.CollectionWidget;

/**
 * Receives wifi changes and creates a notification when wifi connects to a network,
 * displaying the SSID and MAC address.
 *
 * Put the following in your manifest
 *
 * <receiver android:name=".WifiReceiver" android:exported="false" >
 *   <intent-filter>
 *     <action android:name="android.net.wifi.WIFI_STATE_CHANGED" />
 *   </intent-filter>
 * </receiver>
 * <service android:name=".WifiReceiver$WifiActiveService" android:exported="false" />
 *
 * To activate logging use: adb shell setprop log.tag.WifiReceiver VERBOSE
 */
public class WifiReceiver extends BroadcastReceiver {

    private final static String TAG = WifiReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())
                && WifiManager.WIFI_STATE_ENABLED == wifiState) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Wifi is now enabled");
                //CollectionWidget.
            }
            context.startService(new Intent(context, WifiActiveService.class));
        }
    }

    /**
     * Getting the network info and displaying the notification is handled in a service
     * as we need to delay fetching the SSID name. If this is done when the receiver is
     * called, the name isn't yet available and you'll get null.
     *
     * As the broadcast receiver is flagged for termination as soon as onReceive() completes,
     * there's a chance that it will be killed before the handler has had time to finish. Placing
     * it in a service lets us control the lifetime.
     */
    public static class WifiActiveService extends Service {

        private final static String TAG = WifiActiveService.class.getSimpleName();

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            // Calling getSystemService with Context.WIFI_SERVICE causes this error:
            // Error: The WIFI_SERVICE must be looked up on the Application context or memory will leak
            // on devices < Android N. Try changing  to .getApplicationContext()  [WifiManagerLeak]
            final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            // This also causes the same error
            //final WifiManager wifiManager = (WifiManager) getSystemService(getApplicationContext().WIFI_SERVICE);

            // Need to wait a bit for the SSID to get picked up;
            // if done immediately all we'll get is null
            long delayInMilliseconds = 5000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WifiInfo info = wifiManager.getConnectionInfo();
                    String mac = info.getMacAddress();
                    String ssid = info.getSSID();
                    if (Log.isLoggable(TAG, Log.VERBOSE)) {
                        Log.v(TAG, "The SSID & MAC are " + ssid + " " + mac);
                    }
                    createNotification(ssid, mac);
                    stopSelf();
                }
            }, delayInMilliseconds);
            return START_NOT_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        /**
         * Creates a notification displaying the SSID & MAC addr
         */
        private void createNotification(String ssid, String mac) {
            Notification n = new NotificationCompat.Builder(this)
                    .setContentTitle("Wifi Connection")
                    .setContentText("Connected to " + ssid)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("You're connected to " + ssid + " at " + mac))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(0, n);
        }
    }
}
