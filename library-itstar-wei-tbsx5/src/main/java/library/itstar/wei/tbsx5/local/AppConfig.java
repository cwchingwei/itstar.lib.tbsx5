package library.itstar.wei.tbsx5.local;

import android.support.annotation.NonNull;

/**
 * Created by Ching Wei on 2018/5/7.
 */

public class AppConfig
{
    private static String appVersion = null;
    private static String appOS = null;
    private static String appAPP = null;

    public static boolean isBuglyRun ()
    {
        return buglyRun;
    }

    public static void setBuglyRun ( boolean buglyRun )
    {
        AppConfig.buglyRun = buglyRun;
    }

    private static boolean buglyRun = false;

    public static int getViewLunch ()
    {
        return viewLunch;
    }

    public static void setViewLunch ( int viewLunch )
    {
        AppConfig.viewLunch = viewLunch;
    }

    private static int viewLunch = -1;

    public static String getIsDev ()
    {
        return isDev;
    }

    public static Boolean isDev ()
    {
        return isDev.equalsIgnoreCase( "1" );
    }

    public static void setIsDev ( String isDev )
    {
        AppConfig.isDev = isDev;
    }

    private static String isDev = null;

    public static void setAppVersion ( String appVersion )
    {
        AppConfig.appVersion = appVersion;
    }

    public static void setAppOS ( String appOS )
    {
        AppConfig.appOS = appOS;
    }

    public static void setAppAPP ( String appAPP )
    {
        AppConfig.appAPP = appAPP;
    }

    @NonNull
    public static String getAppVersion()
    {
        return appVersion != null? appVersion : "1.0.1";
    }

    @NonNull
    public static String getAppOS()
    {
        return appOS != null? appOS : "2";
    }

    @NonNull
    public static String getAppAPP()
    {
        return appAPP;
    }
}
