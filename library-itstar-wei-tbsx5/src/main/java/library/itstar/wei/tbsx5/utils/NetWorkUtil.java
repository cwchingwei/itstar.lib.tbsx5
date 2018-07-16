package library.itstar.wei.tbsx5.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by C.W. Yang on 2016/12/9.
 */
public class NetWorkUtil
{
    public static final boolean checkInternetConnection( Context aContext )
    {
        ConnectivityManager cm = ( ConnectivityManager ) aContext.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo         ni = cm.getActiveNetworkInfo();

        if( ni != null && ni.isConnected() )
        {
            return ni.isConnected();
        }
        else
        {
            return false;
        }
    }

    public static boolean checkMobileNetworkStatus
            (
                    Activity aActivity
            )
    {
        ConnectivityManager connMgr = ( ConnectivityManager ) aActivity.getSystemService( Context.CONNECTIVITY_SERVICE );

        NetworkInfo mobile = connMgr.getActiveNetworkInfo();

        if( mobile != null && mobile.getType() == ConnectivityManager.TYPE_MOBILE )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean checkWifiNetworkStatus
            (
                    Activity aActivity
            )
    {
        ConnectivityManager connMgr = ( ConnectivityManager ) aActivity.getSystemService( Context.CONNECTIVITY_SERVICE );

        NetworkInfo wifi   = connMgr.getActiveNetworkInfo();

        if( wifi != null && wifi.getType() == ConnectivityManager.TYPE_WIFI )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public static String getPublicIP()
    {
        return _current_ip;
    }

    public static void setPublicIP( String aIP )
    {
        _current_ip = aIP;
    }

    public static String getPhoneIPAddrs( int aSelect )
    {
        String sAddr = "";

        try
        {
            for ( Enumeration<NetworkInterface> enumInterfaces = NetworkInterface.getNetworkInterfaces(); enumInterfaces.hasMoreElements(); )
            {
                // Get next network interface
                NetworkInterface nInterface = enumInterfaces.nextElement();

                for ( Enumeration<InetAddress > enumIPAddrs = nInterface.getInetAddresses(); enumIPAddrs.hasMoreElements(); )
                {
                    // Get next IP address of this interface
                    InetAddress inetAddr = enumIPAddrs.nextElement();

                    // Exclude loopback address
                    if (!inetAddr.isLoopbackAddress())
                    {
                        if (sAddr != "")
                        {
                            sAddr += ", ";
                        }
                        sAddr += "(" + nInterface.getDisplayName() + ") " + inetAddr.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }

        return sAddr;
    }


    // Get current connected wifi SSID
    public static final String getSSID( Context aContext )
    {
        try
        {
            WifiManager manager = ( WifiManager ) aContext.getSystemService( Context.WIFI_SERVICE );

            String SSID = manager.getConnectionInfo().getSSID();

            if( null != SSID )
            {
                return SSID;
            }
            return "";
        }
        catch( Throwable t )
        {
            return "";
        }
    }


    private static String _current_ip = null;
}