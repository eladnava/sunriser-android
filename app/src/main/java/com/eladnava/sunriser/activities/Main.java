package com.eladnava.sunriser.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.eladnava.sunriser.R;
import com.eladnava.sunriser.alarms.SystemClock;
import com.eladnava.sunriser.config.Logging;
import com.eladnava.sunriser.services.CheckSystemAlarm;
import com.eladnava.sunriser.services.SunriseAlarm;
import com.eladnava.sunriser.utils.Networking;
import com.eladnava.sunriser.utils.ThreadUtils;
import com.eladnava.sunriser.utils.intents.IntentExtras;
import com.eladnava.sunriser.integrations.MiLightIntegration;
import com.eladnava.sunriser.scheduler.SunriseScheduler;
import com.eladnava.sunriser.utils.AppPreferences;

public class Main extends AppCompatActivity
{
    ImageView mIcon;
    Thread mMoonlightThread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Set up activity UI
        initializeUI();

        // For API<21: Schedule CheckSystemAlarm
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CheckSystemAlarm.scheduleCheckSystemAlarm(this);
        }

    }

    /*
    @Override
    protected void onStart()
    {
        super.onStart();
    }
    */

    @Override
    protected void onResume()
    {
        super.onResume();

        // Force Wi-Fi connection to use the app
        requireWiFiConnectivity();

        // Check for a scheduled system alarm
        requireScheduledSystemAlarm();

        // Schedule sunrise alarm based on system alarm clock (cancel any previously-scheduled sunrise alarms as well)
        SunriseScheduler.rescheduleSunriseAlarm(this, true);

    }

    @Override
    protected void  onDestroy()
    {
        if(isFinishing()) {
            // kill all alarms
            CheckSystemAlarm.stopCheckSystemAlarm(this);
        }

        super.onDestroy();
    }



    void initializeUI()
    {
        // Inflate main activity layout
        setContentView(R.layout.activity_main);

        // Support action bar for older devices
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set up a toolbar for old devices
        setSupportActionBar(toolbar);

        // Fetch view by ID
        mIcon = (ImageView) findViewById(R.id.icon);

        // Handle clicks on image
        mIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Reschedule sunrise alarm (and display a toast)
                SunriseScheduler.rescheduleSunriseAlarm(Main.this, true);
            }
        });
    }

    private void testSunriseAlarm()
    {
        // Prepare sunrise alarm service intent
        Intent testSunrise = new Intent(Main.this, SunriseAlarm.class);

        // Set to "test" mode
        testSunrise.putExtra(IntentExtras.SUNRISE_ALARM_TEST, true);

        // Start the service for testing
        startService(testSunrise);
    }

    private void requireWiFiConnectivity()
    {
        // No Wi-Fi connection?
        if (!Networking.isWiFiConnected(this))
        {
            // Show error dialog
            new AlertDialog.Builder(this)
                    .setTitle(R.string.connect_wifi)
                    .setMessage(R.string.connect_wifi_desc)
                    .setPositiveButton(R.string.ok, null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            // Still no Wi-Fi connection?
                            if (!Networking.isWiFiConnected(Main.this))
                            {
                                // Goodbye
                                finish();
                            }
                        }
                    })
                    .create().show();
        }
    }

    private void requireScheduledSystemAlarm()
    {
        // No scheduled alarm?
        if (SystemClock.getNextAlarmTriggerTimestamp(this) == 0)
        {
            // Show error dialog
            new AlertDialog.Builder(this)
                    .setTitle(R.string.no_alarms)
                    .setMessage(R.string.no_alarms_desc)
                    .setPositiveButton(R.string.ok, null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            // Goodbye
                            finish();
                        }
                    })
                    .create().show();
        }
    }

    private void viewSettings()
    {
        // Start the settings activity
        startActivity(new Intent(Main.this, Settings.class));
    }

    private void enableMoonlightMode()
    {
        // Kill the sunrise alarm service (in case it's running)
        stopService(new Intent(this, SunriseAlarm.class));

        // Execute the light commands in a new thread
        mMoonlightThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    // Get selected zone in settings
                    int zone = AppPreferences.getMiLightZone(Main.this);

                    // Get the amount of milliseconds to sleep after the sunrise alarm reaches 100%
                    int moonlightDuration = (AppPreferences.getMoonlightDurationMinutes(Main.this) * 60 * 1000);

                    // Turn on white light for the specified zone (in case the mode was set to RGB)
                    MiLightIntegration.setWhiteModeByZone(zone, Main.this);

                    // First, set brightness level to 30% (should be enough for a night light) -- think about making this configurable via settings
                    MiLightIntegration.setBrightnessByZone(30, zone, Main.this);

                    // Wait X amount of seconds before sending the white mode command
                    ThreadUtils.sleepExact(MiLightIntegration.FADE_OUT_DURATION_MS);

                    // Write to log
                    Log.d(Logging.TAG, "Entering moonlight mode for " + moonlightDuration + "ms");

                    // Wait X amount of milliseconds before turning the bulb off
                    ThreadUtils.sleepExact(moonlightDuration);

                    // Turn off the bulb after moonlight mode ends
                    MiLightIntegration.fadeOutLightByZone(zone, Main.this);
                }
                catch (Exception exc)
                {
                    // Log errors to logcat
                    Log.e(Logging.TAG, "Moonlight error", exc);
                }
            }
        });

        // Start it
        mMoonlightThread.start();
    }

    private void killLight()
    {
        // Kill the sunrise alarm service (in case it's running)
        stopService(new Intent(this, SunriseAlarm.class));

        // Moonlight mode is active?
        if (mMoonlightThread != null && mMoonlightThread.isAlive() && !mMoonlightThread.isInterrupted())
        {
            // Interrupt the thread and end it
            mMoonlightThread.interrupt();
        }

        // Actually turn off the light (by zone)
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    // Get selected zone in settings
                    int zone = AppPreferences.getMiLightZone(Main.this);

                    // Turn off the light for the selected zone
                    MiLightIntegration.fadeOutLightByZone(zone, Main.this);
                }
                catch (Exception exc)
                {
                    // Log errors to logcat
                    Log.e(Logging.TAG, "Kill error", exc);
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu - this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks by ID
        switch (item.getItemId())
        {
            // Test
            case R.id.action_test:
                testSunriseAlarm();
                return true;
            // Kill
            case R.id.action_kill_light:
                killLight();
                return true;
            // Moonlight
            case R.id.action_moonlight:
                enableMoonlightMode();
                return true;
            // Settings
            case R.id.action_settings:
                viewSettings();
                return true;
        }

        // Don't consume the event
        return super.onOptionsItemSelected(item);
    }
}
