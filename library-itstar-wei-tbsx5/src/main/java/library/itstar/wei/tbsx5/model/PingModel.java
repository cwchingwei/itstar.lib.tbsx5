package library.itstar.wei.tbsx5.model;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import library.itstar.wei.tbsx5.def.SharedPreferencesKey;
import library.itstar.wei.tbsx5.local.SystemConfig;
import library.itstar.wei.tbsx5.state.ErrorPingState;
import library.itstar.wei.tbsx5.utils.AndroidUtil;
import library.itstar.wei.tbsx5.utils.LogUtil;
import library.itstar.wei.tbsx5.utils.NetWorkUtil;

/**
 * Created by Ching Wei on 2018/2/21.
 */

public class PingModel
{
    public static PingModel instance()
    {

        if( _instance == null )
        {
            _instance = new PingModel();
        }

        return _instance;
    }

    public void createError()
    {
        errorPingState = new ErrorPingState();
        stringBuffer = new StringBuffer();
    }
    public String errorMsg( final Activity aActivity, String aErrorMessage )
    {

        String network = !NetWorkUtil.checkMobileNetworkStatus( aActivity ) ? !NetWorkUtil.checkWifiNetworkStatus( aActivity ) ? "Unable connect" : "Wifi Network" : "3G/4G Network";
        appendResultsText( "Error Message: " + aErrorMessage );
        appendResultsText( "Device OS: " + AndroidUtil.getDeviceOsVersion() );
        appendResultsText( "Device IP: " + NetWorkUtil.getPublicIP() );
//        appendResultsText( "HostName: " + hostName );
        appendResultsText( "Device Model: " + AndroidUtil.getDeviceModel() );
        appendResultsText( "Connection Mode: " + network );
        for( int i = 0; i < PingModel.this.errorPingState.getWebUrls().size(); i++ )
        {
            appendResultsText( String.format( "%s : %.2f ms", errorPingState.getWebUrls().get( i ), errorPingState.getWebUrlsTime().get( i ) ) );
        }
        return stringBuffer.toString();
    }

    public String doPing( final String aUrl, final CallBackListener callBackListener )
    {
        try
        {
            HttpAsync async = new HttpAsync( aUrl, callBackListener );
            return async.execute( ).get();
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        catch ( ExecutionException e )
        {
            e.printStackTrace();
        }

        return aUrl;
    }

    private void appendResultsText ( final String text )
    {
        stringBuffer.append( text + "\n" );
    }

    private class HttpAsync extends AsyncTask< String , Void , String >
    {
        private String mUrl = null;
        private Activity mActivity = null;
        private CallBackListener mListener = null;
        private HttpAsync(  String aUrl, CallBackListener callBackListener )
        {
            this.mUrl = aUrl;
            this.mListener = callBackListener;
        }

        @Override
        protected String doInBackground ( String... strings )
        {
            URL    url      = null;
            try
            {
                url = new URL( mUrl );

                String hostName = url.getHost();

                if ( TextUtils.isEmpty( hostName ) )
                {
                    appendResultsText( "Invalid Ip Address" );
                    return null;
                }

                // Perform an asynchronous ping

                Ping ping = Ping.onAddress( hostName ).setTimeOutMillis( 3000 ).setTimes( pingTimes ).doPing( new Ping.PingListener()
                {
                    @Override
                    public void onResult ( PingResult pingResult )
                    {
//                        appendResultsText( String.format( "%.2f ms", pingResult.getTimeTaken() ) );
//                        Log.e( "PingStats", "onResult: "+  String.format( "%.2f ms", pingResult.getTimeTaken() ) );
                    }

                    @Override
                    public void onFinished ( PingStats pingStats )
                    {
//                        appendResultsText( String.format( "Pings: %d, Packets lost: %d",
//                                pingStats.getNoPings(), pingStats.getPacketsLost()
//                        ) );
//                        appendResultsText( String.format( "Min/Avg/Max Time: %.2f/%.2f/%.2f ms",
//                                pingStats.getMinTimeTaken(), pingStats.getAverageTimeTaken(), pingStats.getMaxTimeTaken()
//                        ) );

                        ErrorPingState errorPingState = new ErrorPingState();
                        LogUtil.logInfo( LogUtil.TAG, "ping result==" + pingStats.toString() );

//                        errorPingState.setUrlTime( pingStats.getAverageTimeTaken() / 100.0f );
//                        errorPingState.setFastDomain( mUrl );
                        if( pingStats.getPacketsLost() == 0 )
                        {
                            if( (pingStats.getAverageTimeTaken() / 100.0f > 0.0f) && (pingStats.getAverageTimeTaken() / 100.0f < 1000.0f) )
                            {
                                float tmpTimes =  SystemConfig.instance().getSharedPreFloat( SharedPreferencesKey.SHARED_PRERENCES_KEY_URL_TIMES, 0.0f );
                                boolean tmpInit =  SystemConfig.instance().getSharedPreBoolean( SharedPreferencesKey.SHARED_PRERENCES_KEY_PING_INIT, false );
//                                String tmpUrl =  SystemConfig.instance().getSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_KEY_WEB_URL, null );

//                                Log.e( "PingStats", "pingState getUrlTime: " + pingStats.getAverageTimeTaken() / 100.0f );
//                                Log.e( "PingStats", "vPingState getUrlTime: " + tmpTimes );

                                if( !tmpInit )
                                {
                                    SystemConfig.instance().putSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_KEY_WEB_URL, mUrl );
                                    SystemConfig.instance().putSharedPreBoolean( SharedPreferencesKey.SHARED_PRERENCES_KEY_PING_INIT, true );
                                    SystemConfig.instance().putSharedPreFloat( SharedPreferencesKey.SHARED_PRERENCES_KEY_URL_TIMES, pingStats.getAverageTimeTaken() / 100.0f );
                                }

                                if( ( tmpInit && (tmpTimes > (pingStats.getAverageTimeTaken() / 100.0f)) ) )
                                {
                                    SystemConfig.instance().putSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_KEY_WEB_URL, mUrl );
                                    SystemConfig.instance().putSharedPreFloat( SharedPreferencesKey.SHARED_PRERENCES_KEY_URL_TIMES, pingStats.getAverageTimeTaken() / 100.0f );
                                }

                            }
                        }
                        else
                        {
//                            appendResultsText( String.format( "Host:%s %.2f ms", pingStats.getIa(), pingStats.getAverageTimeTaken()  / 100.0f ) );
                            if( PingModel.this.errorPingState != null )
                            {
                                PingModel.this.errorPingState.getWebUrlsTime().add( pingStats.getAverageTimeTaken()  / 100.0f );
                                PingModel.this.errorPingState.getWebUrls().add( pingStats.getAddress().getHostName() );
                            }
                        }

                        mListener.onTaskCompleted( errorPingState );
                    }
                } );
                if( ping.getPingStatsResult().getPacketsLost() > 0 )
                {
//                    mListener.onTaskCompleted( null );
                    return null;
                }
                else
                {
                    return "YES";
                }
            }
            catch ( UnknownHostException e )
            {
                e.printStackTrace();
            }
            catch ( MalformedURLException e )
            {
                e.printStackTrace();
            }
            return null;
        }
    }

    public interface CallBackListener
    {
        public void onTaskCompleted ( ErrorPingState errorPingState );
    }

    public void release()
    {
        _instance = null;
    }

    private        StringBuffer   stringBuffer   = null;
    private        ErrorPingState errorPingState = null;
    private static PingModel      _instance      = null;
    private        int            pingTimes      = 2;
}
