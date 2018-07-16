package library.itstar.wei.tbsx5.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.webkit.WebView;

/**
 * Created by C.W. Yang on 2016/10/28.
 */
public class AndroidUtil
{
    public static String getUniqueDeviceId( Context aContext )
    {
        String device_id  = getIMEI( aContext );
        String android_id = getAndroidId( aContext );

        return "DID=" + device_id + "_AID=" + android_id;
    }

    public static String getAndroidId( Context aContext )
    {
        return Settings.Secure.getString( aContext.getContentResolver(), Settings.Secure.ANDROID_ID );
    }

    public static String getIMEI( Context aContext )
    {
        String imei = "";

        final TelephonyManager tm = (TelephonyManager ) aContext.getSystemService( Context.TELEPHONY_SERVICE);
        if( tm != null && tm.getDeviceId() != null )
        {
            imei = tm.getDeviceId();
        }

        return imei;
    }


    public static String getWebviewVersionInfo( WebView aWebView ) {
        // Overridden UA string
        String alreadySetUA = aWebView.getSettings().getUserAgentString();

        // Next call to getUserAgentString() will get us the default
        aWebView.getSettings().setUserAgentString(null);

        // Devise a method for parsing the UA string
        String webViewVersion = (aWebView.getSettings().getUserAgentString());

        // Revert to overriden UA string
        aWebView.getSettings().setUserAgentString(alreadySetUA);

        return webViewVersion;
    }

    public static String getDeviceOsVersion()
    {
        return ( "Android " + Build.VERSION.RELEASE );
    }

    public static String getDeviceModel()
    {
        return Build.MODEL;
    }

    public static String getDeviceVendor()
    {
        return Build.MANUFACTURER;
    }

    public static String getDeviceOS()
    {
        return "ANDROID";
    }

    public static int getDeviceType()
    {
        return 1;
    }


}
