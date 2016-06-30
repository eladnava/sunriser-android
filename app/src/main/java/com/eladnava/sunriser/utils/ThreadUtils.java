package com.eladnava.sunriser.utils;

public class ThreadUtils
{
    public static void sleepExact(int millis) throws Exception {

        Thread.sleep(millis);
    }

    /*
    public static void sleepExact(int millis) throws Exception
    {
        // Get current system time in milliseconds
        long now = System.currentTimeMillis();

        // Calculate sleep end time
        long end = now + millis;

        // Wait for the desired time to pass
        while(now < end)
        {
            // Handle interrupts
            if (Thread.currentThread().isInterrupted())
            {
                // Stop execution
                throw new InterruptedException();
            }

            // Update the current time
            now = System.currentTimeMillis();
        }
    }
    */
}
