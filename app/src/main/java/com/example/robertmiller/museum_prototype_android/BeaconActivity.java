package com.example.robertmiller.museum_prototype_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;
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
import java.util.List;


public class BeaconActivity extends FragmentActivity implements BeaconConsumer {

    protected static final String TAG = "BeaconActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private boolean entryFlag = true;
    private int currMajor;
    private int currMinor;
    BeaconFragmentAdapter fragmentPagerAdapter;
    private FragmentActivity context;
    private BeaconActivity thisBeaconActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        Log.d(TAG,"In the onCreate");

        //context = (FragmentActivity)Activity.this.getCallingActivity();
        //context = (FragmentActivity)this.getCallingActivity();
        ViewPager viewPager = (ViewPager)findViewById(R.id.ViewPager);
        //fragmentPagerAdapter = new BeaconFragmentAdapter(context.getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(4);
        fragmentPagerAdapter = new BeaconFragmentAdapter(this.getSupportFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);

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

       /* final  AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("up up");
        alertDialog.setMessage("does this shit work at all?");
        alertDialog.setButton(0, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
*/
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
                            //ImageView iv = (ImageView)findViewById(R.id.imageFragImageView);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            //iv.setImageBitmap(bitmap);
                            //fragmentPagerAdapter
                            fragmentPagerAdapter.artworkImagePage.setImageView(bitmap);
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
                        if ((tempBeacon.getId2().toInt() != currMajor) && (tempBeacon.getId3().toInt() != currMinor) && (entryFlag == true) && (tempBeacon.getTxPower() > -65)) {
                            currMajor = tempBeacon.getId2().toInt();
                            currMinor = tempBeacon.getId3().toInt();
                            Toast.makeText(getApplicationContext(), "Found a new beacon!", Toast.LENGTH_LONG).show();

                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Artwork");
                            query.whereEqualTo("major", tempBeacon.getId2().toInt());
                            query.whereEqualTo("minor", tempBeacon.getId3().toInt());
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                public void done(ParseObject object, ParseException e) {
                                    if (e == null) {
                                        // object will be your game score
                                        TextView tv = (TextView)findViewById(R.id.Artwork);
                                        String MuseumName = object.getString("artWork");
                                        tv.setText(MuseumName);

                                        TextView artistNameView = (TextView)findViewById(R.id.ArtistName);
                                        String ArtistName = object.getString("artistName");
                                        artistNameView.setText(ArtistName);

                                        ParseFile artwortImageFile = object.getParseFile("piecePicture");
                                        artwortImageFile.getDataInBackground(new GetDataCallback() {
                                            @Override
                                            public void done(byte[] bytes, ParseException e) {
                                                //ImageView iv = (ImageView)findViewById(R.id.imageFragImageView);
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                                //iv.setImageBitmap(bitmap);
                                                //fragmentPagerAdapter
                                                fragmentPagerAdapter.artworkImagePage.setImageView(bitmap);
                                            }
                                        }, new ProgressCallback() {
                                            @Override
                                            public void done(Integer integer) {

                                            }
                                        });
                                        ParseFile artistImageFile = object.getParseFile("artistPicture");
                                        artistImageFile.getDataInBackground(new GetDataCallback() {
                                            @Override
                                            public void done(byte[] bytes, ParseException e) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                fragmentPagerAdapter.artistImagePage.setImageView(bitmap);
                                            }
                                        });
                                    } else {
                                        // something went wrong
                                        Log.d(TAG, "something's gone wrong with the parse download...");
                                    }
                                }
                            });

                           /* AlertDialog.Builder builder = new AlertDialog.Builder(thisBeaconActivity);
                            builder.setTitle("New Beacon!");
                            builder.setMessage("Do you want to see the new beacon??");
                            builder.setPositiveButton("Yeah!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Query Parse for the Major and Minor
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.setNegativeButton("Nah..", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Do nothing
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show(); */
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

    public class BeaconFragmentAdapter extends android.support.v4.app.FragmentPagerAdapter {
        private int NUM_ITEMS = 4;
        public ImageFragment artworkImagePage;
        private TextFragment artworkDescPage;
        private ImageFragment artistImagePage;
        private TextFragment artistDescPage;

        public BeaconFragmentAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    artworkImagePage = ArtworkImageFragment.newInstance(0, "Artwork");
                    return artworkImagePage;
                case 1:
                    artworkDescPage = ArtworkDescFragment.newInstance(1, "Artwork Description");
                    return artworkDescPage;
                case 2:
                    artistImagePage = ArtistImageFragment.newInstance(2, "Artist");
                    return artistImagePage;
                case 3:
                    artistDescPage = ArtistDescFragment.newInstance(3, "Artist Description");
                    return artistDescPage;
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Artwork";
                case 1:
                    return "Artwork Description";
                case 2:
                    return "Artist";
                case 3:
                    return "Artist Description";
                default:
                    return null;
            }
        }

        public void setArtworkImage(Bitmap bmp) {
            artworkImagePage.setImageView(bmp);
        }
    }
}

