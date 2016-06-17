package com.eladnava.sunriser.activities;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.eladnava.sunriser.services.CheckSystemAlarm;
import com.eladnava.sunriser.utils.AppPreferences;
import com.eladnava.sunriser.utils.compatibility.AppCompatPreferenceActivity;
import com.eladnava.sunriser.R;

public class Settings extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Set-up activity UI
        initializeUI();
    }

    void displayBackButton()
    {
        // Support action bar for older devices
        ActionBar actionBar = getSupportActionBar();

        // API is available?
        if (actionBar != null)
        {
            // Show the back button in the action bar
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    void initializeUI()
    {
        // Display home as back button
        displayBackButton();

        // Load settings from XML (find a better, non-deprecated way to do this)
        addPreferencesFromResource(R.xml.settings);

        // Register handler to catch changes to preferences
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    public boolean onOptionsItemSelected(final MenuItem Item)
    {
        // Handle item ID cases
        switch (Item.getItemId())
        {
            // Home button?
            case android.R.id.home: {
                onBackPressed();
            }
        }

        // Don't consume the event
        return super.onOptionsItemSelected(Item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        // For API<21: Schedule CheckSystemAlarm
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if(key.equals("enable"))
            {
                if(AppPreferences.isAppEnabled(this)) {
                    // Enable CheckSystemAlarm task
                    CheckSystemAlarm.scheduleCheckSystemAlarm(this);
                }
                else
                {
                    // Disable CheckSystemAlarm task
                    CheckSystemAlarm.stopCheckSystemAlarm(this);
                }
            }
        }

    }

}
