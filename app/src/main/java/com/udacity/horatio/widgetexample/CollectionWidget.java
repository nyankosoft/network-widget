package com.udacity.horatio.widgetexample;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.view.View;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
//import com.udacity.horatio.widgetexample.WifiReceiver;

/**
 * Implementation of App Widget functionality.
 */
public class CollectionWidget extends AppWidgetProvider {

    private static final String WIFI_BUTTON_CLICKED    = "WiFiButtonClick";
    private static final String CELLULAR_BUTTON_CLICKED    = "CellularButtonClick";
    private static final String ACCESS_POINT_BUTTON_CLICKED    = "AccessPointButtonClick";

    private static int counter = 0;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Set up the collection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views);
        } else {
            setRemoteAdapterV11(context, views);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews collectionWidgetViews = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
        ComponentName myWidget = new ComponentName(context, CollectionWidget.class);

        // Set a onClick listener
        collectionWidgetViews.setOnClickPendingIntent(R.id.wifi_button_id, getPendingSelfIntent(context, WIFI_BUTTON_CLICKED));
        appWidgetManager.updateAppWidget(myWidget, collectionWidgetViews);
        collectionWidgetViews.setOnClickPendingIntent(R.id.cellular_button_id, getPendingSelfIntent(context, CELLULAR_BUTTON_CLICKED));
        appWidgetManager.updateAppWidget(myWidget, collectionWidgetViews);
        collectionWidgetViews.setOnClickPendingIntent(R.id.ap_button_id, getPendingSelfIntent(context, ACCESS_POINT_BUTTON_CLICKED));
        appWidgetManager.updateAppWidget(myWidget, collectionWidgetViews);

        // Show the current IP adddress in the header
        //WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        //if(wifi != null) {
        //}
        String ipAddress = "?.?.?.?";
        // try {
        //   InetAddress localHost = InetAddress.getLocalHost();
        //   ipAddress = localHost.getHostAddress();
        // } catch(UnknownHostException e) {
        //   ipAddress = e.getMessage();
        // }
        setRemoteViewText(context,R.id.widget_header,ipAddress);

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, WidgetService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private static void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, WidgetService.class));
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        if(WIFI_BUTTON_CLICKED.equals(intent.getAction())) {

            WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            String wifiState = "?";
            try {
              if(wifi != null) {
                  if(wifi.isWifiEnabled()) {
                    wifiState = "ON";
                  } else {
                    wifiState = "OFF";
                  }
              }
            } catch(Exception e) {
              wifiState = e.getMessage();
            }

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
            ComponentName myWidget = new ComponentName(context, CollectionWidget.class);

            counter += 1;
            remoteViews.setTextViewText(R.id.wifi_button_id, "Wi-fi: " + wifiState);

            appWidgetManager.updateAppWidget(myWidget, remoteViews);

        } else if(CELLULAR_BUTTON_CLICKED.equals(intent.getAction())) {
            String cellular = "*";
            int s = isMobileDataEnabled(context);
            if(s==1){cellular = "ON";}
            else if(s==0){cellular = "OFF";}
            else if(s==-1){cellular = "!";}
            else if(s==-2){cellular = "?";}

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
            ComponentName myWidget = new ComponentName(context, CollectionWidget.class);

            counter += 1;
            remoteViews.setTextViewText(R.id.cellular_button_id, "CELLULAR: " + s);

            appWidgetManager.updateAppWidget(myWidget, remoteViews);
        } else if(ACCESS_POINT_BUTTON_CLICKED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
            ComponentName myWidget = new ComponentName(context, CollectionWidget.class);

            counter += 1;
            remoteViews.setTextViewText(R.id.ap_button_id, "AP:" + getApState(context));

            appWidgetManager.updateAppWidget(myWidget, remoteViews);
        }
    }

    private static void setRemoteViewText(Context context, int id, String text) {
      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
      RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
      ComponentName myWidget = new ComponentName(context, CollectionWidget.class);
      remoteViews.setTextViewText(id,text);
      appWidgetManager.updateAppWidget(myWidget, remoteViews);
    }

    public int isMobileDataEnabled(Context context) {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null) {
            return -1;
        }
        int r = -10;
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            r = -2;
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            r = -3;
            method.setAccessible(true); // Make the method callable
            r = -4;
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);
            // Apparently this method.invoke calls is causing an exception
            r = -5;
        } catch (NoSuchMethodException e) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mobileDataEnabled = Settings.Global.getInt(context.getContentResolver(), "mobile_data", 0) == 1;
                return mobileDataEnabled ? 1 : 0;
            } else {
                return -6;
            }
        } catch (InvocationTargetException ite) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
            ComponentName myWidget = new ComponentName(context, CollectionWidget.class);
            remoteViews.setTextViewText(R.id.widget_header, ite.getMessage());
            appWidgetManager.updateAppWidget(myWidget, remoteViews);
            if (ite.getCause() instanceof NoSuchMethodException) {
                return -7;
            } else if (ite.getCause() instanceof Exception) {
                return -8;
            } else {
                return -9;
            }
            //return -10;
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
            return r;
        }

        return mobileDataEnabled ? 1 : 0;
    }

    //check whether wifi hotspot on or off
    public static boolean isApOn(Context context) {
      int s = getApState(context);
      if(s == 1) {
        return true;
      } else {
        // Report everything else as 'off'
        return false;
      }
    }

    // Is there a way to get the 'turning on' state?
    public static int getApState(Context context) {
        WifiManager wifimanager = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            boolean isOn = (Boolean)method.invoke(wifimanager);
            return isOn ? 1 : 0;
        }
        catch(Exception e) {
          setRemoteViewText(context,R.id.widget_header,e.getMessage());
          return -1;
        }
        //return -2;
    }

    // toggle wifi hotspot on or off
    public static boolean configApState(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {
            // if WiFi is on, turn it off
            if(isApOn(context)) {
                wifimanager.setWifiEnabled(false);
            }
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, !isApOn(context));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
