package com.eladnava.sunriser.integrations;

import android.content.Context;
import android.util.Log;

import com.eladnava.sunriser.config.Logging;
import com.eladnava.sunriser.integrations.bindings.MiLightBindings;
import com.eladnava.sunriser.utils.AppPreferences;
import com.eladnava.sunriser.utils.Networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MiLightIntegration {
    // Amount of time it takes for the zone selection to register (in millis)
    public static final int SELECT_ZONE_DELAY_MS = 200;

    // Amount of time it takes for the bulb to fade out (in millis)
    public static final int FADE_OUT_DURATION_MS = 1000;

    // Amount of time it takes for the bulb to whiten (in millis)
    public static final int WHITEN_DURATION_MS = 1000;

    public static void turnOnAndSelectZone(int zone, Context context) throws Exception {
        // Zone validation
        validateZone(zone);

        // Construct the zone selection command
        byte[] zoneSelectionCommand = new byte[]{MiLightBindings.selectCommandsByZone[zone], 0x00, 0x55};

        // Broadcast it
        broadcastCommand(zoneSelectionCommand, context);

        // Wait a bit before sending other commands (so that the command registers successfully)
        Thread.sleep(SELECT_ZONE_DELAY_MS);
    }

    private static void validateZone(int zone) throws Exception {
        // Sanitize zone (can only range from 0 to 4, inclusive)
        if (zone < 0 || zone > 4) {
            throw new Exception("The provided zone must range from 0 to 4, inclusive.");
        }
    }

    public static void killLightByZone(int zone, Context context) throws Exception {
        // Zone validation
        validateZone(zone);

        // Construct the kill command
        byte[] zoneOffPacket = new byte[]{MiLightBindings.killCommandsByZone[zone], 0x00, 0x55};

        // Broadcast it
        broadcastCommand(zoneOffPacket, context);

        // Log it
        Log.d(Logging.TAG, "Killed light zone " + zone);
    }

    public static void fadeOutLightByZone(int zone, Context context) throws Exception {
        // Modify bulb brightness level to lowest percent value for next time
        MiLightIntegration.setBrightnessByZone(1, zone, context);

        // Wait X amount of seconds before turning off the light
        Thread.sleep(FADE_OUT_DURATION_MS);

        // Turn off the bulb since we should have woken up by now
        MiLightIntegration.killLightByZone(zone, context);
    }

    public static void setWhiteModeByZone(int zone, Context context) throws Exception {
        // Zone validation
        validateZone(zone);

        // Prepare the white mode command
        byte[] whiteModeCommand = new byte[]{MiLightBindings.whiteCommandsByZone[zone], 0x00, 0x55};

        // Send it
        broadcastCommand(whiteModeCommand, context);

        // Wait a bit before sending other commands so it's registered
        Thread.sleep(WHITEN_DURATION_MS);

        // Log it
        Log.d(Logging.TAG, "Set white mode for zone " + zone);
    }

    public static void setBrightnessByZone(int percent, int zone, Context context) throws Exception {
        // Validate zone + send zone selection command (and wait a bit for it to register) before sending the brightness command
        turnOnAndSelectZone(zone, context);

        // Sanitize brightness level input
        if (percent < 0 || percent > 100) {
            throw new Exception("Please specify a brightness percentage from 0 to 100.");
        }

        // Determine the maximum index of brightness commands (the higher the index, the brighter it is)
        int maxBrightnessIndex = MiLightBindings.brightnessCommands.length - 1;

        // Convert desired brightness percentage to the appropriate brightness byte
        byte brightnessByte = MiLightBindings.brightnessCommands[(int) (percent / 100.0 * maxBrightnessIndex)];

        // Prepare the brightness command
        byte[] brightnessCommand = new byte[]{0x4E, brightnessByte, 0x55};

        // Send it
        broadcastCommand(brightnessCommand, context);

        // Log it
        Log.d(Logging.TAG, "Setting brightness to " + percent + "% for zone " + zone);
    }

    private static void broadcastCommand(byte[] buffer, Context context) throws Exception {
        // Verify Wi-Fi network connectivity before broadcasting
        if (!Networking.isWiFiConnected(context)) {
            return;
        }

        // MiLight router host address
        InetAddress address = InetAddress.getByName(AppPreferences.getMiLightHost(context));

        // Prepare new UDP broadcast socket
        DatagramSocket socket = new DatagramSocket();

        // Prepare a packet with the buffer content, length, server address, and port
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, AppPreferences.getMiLightPort(context));

        // Broadcast UDP packet
        socket.send(packet);

        // Close socket
        socket.close();
    }
}
