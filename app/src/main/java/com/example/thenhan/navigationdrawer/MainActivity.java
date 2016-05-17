package com.example.thenhan.navigationdrawer;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SearchView searchView;
    TextView txtRequestUrl, txtResponseJson, txtResponseJava;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // enable the home button
        //ActionBar actionBar = getActionBar();
        //actionBar.setHomeButtonEnabled(true);
        //actionBar.setDisplayUseLogoEnabled(false);
        //actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);

            // make rounded image
            Resources res = getResources();
            Bitmap src = BitmapFactory.decodeResource(res, R.drawable.songoku);
            RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, src);
            dr.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);
            ImageView imageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
            imageView.setImageDrawable(dr);

            TextView view = (TextView) navigationView.getMenu()
                    .findItem(R.id.nav_favorite).getActionView();
            view.setText("2");
        }


        // Web service
        txtRequestUrl = (TextView) findViewById(R.id.txtRequestUrl);
        txtResponseJson = (TextView) findViewById(R.id.txtResponseJson);
        txtResponseJava = (TextView) findViewById(R.id.txtResponseJava);
        String SERVER_URL =
                "http://api.openweathermap.org/data/2.5/weather?q=HoChiMinh,vn&units=metric&appid=8b62177ed538309f1fe0756026559a29";

        txtRequestUrl.setText(new Date() + "\n" + SERVER_URL);
        // Use AsyncTask to execute potential slow task without freezing GUI
        new LongOperation().execute(SERVER_URL);
    }

    private class LongOperation extends AsyncTask<String, Void, Void> {
        private String jsonResponse;
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        protected void onPreExecute() {
            dialog.setMessage("Please wait..");
            dialog.show();
        }

        protected Void doInBackground(String... urls) {
            try {
                // solution uses Java.Net class (Apache.HttpClient is now deprecated)
                // STEP1. Create a HttpURLConnection object releasing REQUEST to given site
                URL url = new URL(urls[0]); //argument supplied in the call to AsyncTask
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("User-Agent", "");
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();

                // STEP2. wait for incoming RESPONSE stream, place data in a buffer
                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new
                        InputStreamReader(isResponse));

                // STEP3. Arriving JSON fragments are concatenate into a StringBuilder
                String myLine = "";
                StringBuilder strBuilder = new StringBuilder();
                while ((myLine = responseBuffer.readLine()) != null) {
                    strBuilder.append(myLine);
                }
                //show response (JSON encoded data)
                jsonResponse = strBuilder.toString();
                Log.e("RESPONSE", jsonResponse);
            } catch (Exception e) {
                Log.e("RESPONSE Error", e.getMessage());
            }
            return null; // needed to gracefully terminate Void method
        }

        protected void onPostExecute(Void unused) {
            try {
                dialog.dismiss();

                // update GUI with JSON Response
                txtResponseJson.setText(jsonResponse);

                // Step4. Convert JSON list into a Java collection of Person objects
                // prepare to decode JSON response and create Java list
                Gson gson = new Gson();
                String result = "";
                try {
                    JsonElement jelement = new JsonParser().parse(jsonResponse);
                    JsonObject jobject = (JsonObject) jelement;
                    result += jobject.get("name").toString() + "\n";
                } catch (Exception e) {
                    Log.e("PARSING", e.getMessage());
                }
                txtResponseJava.setText(result);
            } catch (JsonSyntaxException e) {
                Log.e("POST-Execute", e.getMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Spinner on Action Bar
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_data, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        // get access to the collapsible SearchView
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        // set SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getApplicationContext(),
                        "Search " + query, Toast.LENGTH_LONG).show();

                // recreate the original ActionBar Search
                invalidateOptionsMenu();

                // clear query string
                searchView.setQuery("", false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Locate MenuItem holding ShareActionProvider
        MenuItem easySharedItem = menu.findItem(R.id.action_share);
        // prepare EASY SHARE action tile:
        // Fetch and store a ShareActionProvider for future usage
        // an intent assembles the email(or SMS), you need only to select carrier
        ShareActionProvider shareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(easySharedItem);
        // prepare an EMAIL
        shareActionProvider.setShareIntent(emailIntent());
        // prepare an SMS - try this later...
        //shareActionProvider.setShareIntent(smsIntent());

        return true;
    }

    // return a SHARED intent to deliver an email
    private Intent emailIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
        intent.putExtra(Intent.EXTRA_TEXT, "this is the email-text to be sent...");
        return intent;
    }

    // return a SHARED intent to deliver an SMS text-message
    private Intent smsIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String yourNumber = "+84917024639";
        intent.setData(Uri.parse("sms:" + yourNumber));
        intent.putExtra("sms_body", "Here goes my msg");
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Toast.makeText(getApplicationContext(),
                    "Je clique sur ActionSearch", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_download) {
            Toast.makeText(getApplicationContext(),
                    "Je clique sur ActionDownload", Toast.LENGTH_LONG).show();
            // Temporarily replace the download action-tile with circular progress bar
            performSlowOperation(item);
            return true;
        } else if (id == R.id.action_refresh) {
            Toast.makeText(getApplicationContext(),
                    "Je clique sur ActionRefesh", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(),
                    "Je clique sur ActionSettings", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_about) {
            Toast.makeText(getApplicationContext(),
                    "Je clique sur ActionAbout", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void performSlowOperation(MenuItem item) {
        // temporally replace Download action-icon with a progress-bar
        final MenuItem downloadActionItem = item;
        downloadActionItem.setActionView(R.layout.custom_view_download);
        downloadActionItem.expandActionView();
        // define an Android-Handler control to receive messages from a
        // background thread were the slow work is to be done
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Toast.makeText(getApplicationContext(),
                        "Download complete!", Toast.LENGTH_LONG).show(); // announce completion of slow job
                downloadActionItem.collapseActionView(); // dismiss progress-bar
                downloadActionItem.setActionView(null);
            }
        };
        // a parallel Thread runs the slow-job and signals its termination
        new Thread() {
            @Override
            public void run() {
                // real 'BUSY_WORK' goes here...(we fake 5 seconds)
                long endTime = SystemClock.uptimeMillis() + 5000; //now + 2 seconds
                handler.sendMessageAtTime(handler.obtainMessage(), endTime);
                super.run();
            }
        }.start();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            Toast.makeText(getApplicationContext(),
                    "Je clique sur Home", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_favorite) {
            Toast.makeText(getApplicationContext(),
                    "Je clique sur Favorites", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_search) {
            Toast.makeText(getApplicationContext(),
                    "Je clique sur Search", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_notification) {
            Toast.makeText(getApplicationContext(),
                    "Je clique sur Notification", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(getApplicationContext(),
                    "Je clique sur Settings", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_about) {
            Toast.makeText(getApplicationContext(),
                    "Je clique sur About", Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }
}
