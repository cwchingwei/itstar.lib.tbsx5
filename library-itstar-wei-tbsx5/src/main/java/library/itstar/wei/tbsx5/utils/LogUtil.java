package library.itstar.wei.tbsx5.utils;

import android.util.Log;


/**
 * Created by Ching Wei on 2018/3/7.
 */

public class LogUtil
{
    public static final String TAG = "X5.Lib";
    public static void println( String aStr )
    {
//        if( DebugSwitch.LOG_SHOW )
        {
            System.out.println( aStr );
        }
    }

    public static void logVerbose( String aTag, String aStr )
    {
//        if( DebugSwitch.LOG_SHOW )
        {
            Log.v( aTag, aStr );
        }
    }

    public static void logDebug( String aTag, String aStr )
    {
//        if( DebugSwitch.LOG_SHOW )
        {
            Log.d( aTag, aStr );
        }
    }

    public static void logInfo( String aTag, String aStr )
    {
//        if( DebugSwitch.LOG_SHOW )
        {
            Log.i( aTag, aStr );
        }
    }

    public static void logWarn( String aTag, String aStr )
    {
//        if( DebugSwitch.LOG_SHOW )
        {
            Log.w( aTag, aStr );
        }
    }

    public static void logError( String aTag, String aStr )
    {
//        if( DebugSwitch.LOG_SHOW )
        {
            Log.e( aTag, aStr );
        }
    }
}
