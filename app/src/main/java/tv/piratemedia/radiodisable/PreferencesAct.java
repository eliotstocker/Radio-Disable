package tv.piratemedia.radiodisable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class PreferencesAct extends ActionBarActivity {
    private Toolbar mActionBar;
    public static String NETWORKS_PREFS = "tv.piratemedia.radiodisable.networks";
    public static String UPDATE_PREFERENCES = "tv.piratemedia.radiodisable.UPDATE_PREFERENCES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        mActionBar = (Toolbar) findViewById(R.id.ab);
        setSupportActionBar(mActionBar);

        ListView List = (ListView)findViewById(R.id.network_list);
        listWifiNetworks(List);

        final SharedPreferences prefs = getSharedPreferences(this.getPackageName(), Context.MODE_PRIVATE);

        SwitchCompat Enable = (SwitchCompat) findViewById(R.id.enable_service);
        Enable.setChecked(prefs.getBoolean("enabled", false));

        Enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean("enabled", isChecked).commit();
                Intent intent = new Intent();
                intent.setAction(UPDATE_PREFERENCES);
                sendBroadcast(intent);
            }
        });
    }

    private void listWifiNetworks(ListView lv) {
        WifiManager wm = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        final List<WifiConfiguration> networks = wm.getConfiguredNetworks();
        final SharedPreferences prefs = getSharedPreferences(NETWORKS_PREFS, Context.MODE_PRIVATE);

        ListAdapter la = new ListAdapter() {
            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public boolean isEnabled(int position) {
                return false;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public int getCount() {
                return networks.size();
            }

            @Override
            public Object getItem(int position) {
                return networks.get(position);
            }

            @Override
            public long getItemId(int position) {
                return ((WifiConfiguration)getItem(position)).networkId;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                WifiConfiguration net = (WifiConfiguration) getItem(position);
                convertView = getLayoutInflater().inflate(R.layout.wifi_network, null);
                TextView Name = (TextView)convertView.findViewById(R.id.net_name);
                final String ssid = net.SSID.substring(1, net.SSID.length() - 1);
                Name.setText(ssid);
                SwitchCompat on = (SwitchCompat)convertView.findViewById(R.id.network_on);
                Boolean isEnabled = prefs.getBoolean(net.SSID.substring(1, net.SSID.length() - 1), false);
                on.setChecked(isEnabled);
                on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            prefs.edit().putBoolean(ssid, true).commit();
                        } else {
                            prefs.edit().remove(ssid).commit();
                        }
                        Intent intent = new Intent();
                        intent.setAction(UPDATE_PREFERENCES);
                        sendBroadcast(intent);
                    }
                });

                return convertView;
            }

            @Override
            public int getItemViewType(int position) {
                return 1;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };
        lv.setAdapter(la);
    }
}
