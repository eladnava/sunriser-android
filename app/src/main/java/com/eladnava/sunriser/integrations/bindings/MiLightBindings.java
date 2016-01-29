package com.eladnava.sunriser.integrations.bindings;

public class MiLightBindings
{
    public static byte[] brightnessCommands = new byte[]
    {
        0x02, // 5% brightness
        0x03, // 10% brightness
        0x04, // 15% brightness
        0x05, // 20% brightness
        0x08, // 25% brightness
        0x09, // 30% brightness
        0x0A, // 35% brightness
        0x0B, // 40% brightness
        0x0D, // 45% brightness
        0x0E, // 50% brightness
        0x0F, // 55% brightness
        0x10, // 60% brightness
        0x11, // 65% brightness
        0x12, // 70% brightness
        0x13, // 75% brightness
        0x14, // 80% brightness
        0x15, // 85% brightness
        0x17, // 90% brightness
        0x18, // 95% brightness
        0x19  // 100% brightness
    };

    public static byte[] whiteCommandsByZone = new byte[]
    {
        (byte)0xC2, // Whiten All Zones
        (byte)0xC5, // Whiten Zone 1
        (byte)0XC7, // Whiten Zone 2
        (byte)0xC9, // Whiten Zone 3
        (byte)0xCB  // Whiten Zone 4
    };

    public static byte[] selectCommandsByZone = new byte[]
    {
        0x42, // Select All Zones
        0x45, // Select Zone 1
        0x47, // Select Zone 2
        0x49, // Select Zone 3
        0x4B  // Select Zone 4
    };

    public static byte[] killCommandsByZone = new byte[]
    {
        0x41, // Kill All Zones
        0x46, // Kill Zone 1
        0x48, // Kill Zone 2
        0x4A, // Kill Zone 3
        0x4C  // Kill Zone 4
    };
}
