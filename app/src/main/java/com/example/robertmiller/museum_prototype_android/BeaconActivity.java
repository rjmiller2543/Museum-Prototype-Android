package com.example.robertmiller.museum_prototype_android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseFile;
import com.parse.ParseException;
import com.parse.GetCallback;
import com.parse.ProgressCallback;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.os.RemoteException;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.service.BeaconService;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.AltBeacon;

import java.util.Collection;


public class BeaconActivity extends Activity implements BeaconConsumer {

    protected static final String TAG = "BeaconActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private boolean entryFlag = true;
    private int currMajor;
    private int currMinor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        Log.d(TAG,"In the onCreate");
        Parse.initialize(this, "dt8scpzoFbw0fcPKnBdm8dodp0fSrf0yKHdparxi", "evAg7d6eqnq6ttYLHeH868OUsEdQQVkUWfRPjEMI");
        //beaconManager = BeaconManager.getInstanceForApplication(this);
        //beaconManager.bind(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        //beaconManager.bind(this);
        beaconManager.debug = true;
        beaconManager.bind(this);
        Region region = new Region("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0", null, null, null);
        //beaconManager.startMonitoringBeaconsInRegion(region);
        try {
            //beaconManager.startMonitoringBeaconsInRegion(region);
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {   }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Artwork");
        query.getInBackground("iTJRCi84yA", new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be your game score
                    TextView tv = (TextView)findViewById(R.id.Artwork);
                    String MuseumName = object.getString("artWork");
                    tv.setText(MuseumName);

                    ParseFile artwortImageFile = object.getParseFile("piecePicture");
                    artwortImageFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {
                            ImageView iv = (ImageView)findViewById(R.id.ArtworkImage);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            iv.setImageBitmap(bitmap);
                        }
                    }, new ProgressCallback() {
                        @Override
                        public void done(Integer integer) {

                        }
                    });
                } else {
                    // something went wrong
                    Log.d(TAG, "something's gone wrong with the parse download...");
                }
            }
        });
    }

    //@Override
 /*   public void onBeaconServiceConnect() {
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an iBeacon for the firt time!");
                Log.i(TAG, "It has an id");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0", null, null, null));
        } catch (RemoteException e) {   }
    }
*/
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    for (int i = 0; i < beacons.size(); i++) {
                        Beacon tempBeacon = beacons.iterator().next();
                        String message = "Beacon at index: "+i;
                        message += " has a tx power: "+tempBeacon.getTxPower();
                        message += " and a major: "+tempBeacon.getId2();
                        message += " and a minor: "+tempBeacon.getId3();
                        Log.d(TAG, message);
                        if ((tempBeacon.getId2() != currMajor) && (tempBeacon.getId3() != currMinor) && (entryFlag == true) && (tempBeacon.getTxPower() > -65)) {
                            Toast.makeText(getApplicationContext(), "Found a new beacon!", Toast.LENGTH_LONG).show();
                        }
                    }
                    //Log.i(TAG, "The first beacon I see is about " + beacons.iterator().next().getDistance() + " meters away.");
                    //Log.i(TAG, "It has an id major: " + beacons.iterator().next().getId2());
                    //Log.d(TAG, "It has an id minor: "+ beacons.iterator().next().getId3());
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0", null, null, null));
        } catch (RemoteException e) {   }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.beacon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

