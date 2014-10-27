package tv.piratemedia.radiodisable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by eliotstocker on 27/10/14.
 */
public class DisableReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        if(preferences.getBoolean("enabled", false)) {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                WifiInfo wi = wm.getConnectionInfo();
                String SSID = wi.getSSID().substring(1, wi.getSSID().length() - 1);
                SharedPreferences Networks = context.getSharedPreferences(PreferencesAct.NETWORKS_PREFS, Context.MODE_PRIVATE);

                Log.d("Disable Reciever", SSID);

                if(Networks.getBoolean(SSID, false)) {
                    Log.d("Disable Reciever", "disable Network");
                    setMobileRadioEnabled(context, false);
                } else {
                    Log.d("Disable Reciever", "enable Network");
                    setMobileRadioEnabled(context, true);
                }
            } else {
                Log.d("Disable Reciever", "enable Network");
                setMobileRadioEnabled(context, true);
            }
        } else {
            Log.d("Disable Reciever", "enable Network");
            setMobileRadioEnabled(context, true);
        }
    }

    private void setMobileRadioEnabled(Context context, boolean enabled) {
        try {
            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setRadio = iConnectivityManagerClass.getDeclaredMethod("setRadio", Integer.TYPE ,  Boolean.TYPE);
            setRadio.setAccessible(true);
            for (NetworkInfo networkInfo : conman.getAllNetworkInfo()) {
                if(isNetworkTypeMobile(networkInfo.getType())) {
                    setRadio.invoke(iConnectivityManager, networkInfo.getType(), enabled);
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, R.string.error_toast, Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isNetworkTypeMobile(int networkType) {
        switch (networkType) {
            case ConnectivityManager.TYPE_MOBILE:
            case ConnectivityManager.TYPE_MOBILE_MMS:
            case ConnectivityManager.TYPE_MOBILE_SUPL:
            case ConnectivityManager.TYPE_MOBILE_DUN:
            case ConnectivityManager.TYPE_MOBILE_HIPRI:
            case 10:
            case 11:
            case 12:
            case 14:
                return true;
            default:
                return false;
        }
    }
}
