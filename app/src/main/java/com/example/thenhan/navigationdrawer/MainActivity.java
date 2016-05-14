package com.example.thenhan.navigationdrawer;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

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
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(),
                    "Je clique sur ActionSettings", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_refresh) {
            Toast.makeText(getApplicationContext(),
                    "Je clique sur ActionRefesh", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_search) {
            Toast.makeText(getApplicationContext(),
                    "Je clique sur ActionSearch", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_about) {
            Toast.makeText(getApplicationContext(),
                    "Je clique sur ActionAbout", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
