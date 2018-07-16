package library.itstar.wei.tbsx5.model;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;

import library.itstar.wei.tbsx5.state.ErrorPingState;
import library.itstar.wei.tbsx5.utils.AndroidUtil;
import library.itstar.wei.tbsx5.utils.NetWorkUtil;
import library.itstar.wei.tbsx5.utils.StringUtils;

import static android.content.ContentValues.TAG;

/**
 * Created by Ching Wei on 2018/2/21.
 */

public class CheckLinkModel
{
    public static CheckLinkModel instance()
    {

        if( _instance == null )
        {
            _instance = new CheckLinkModel();
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
        for( int i = 0; i < errorPingState.getWebUrls().size(); i++ )
        {
            appendResultsText( String.format( "Host:%s %.2f ms", errorPingState.getWebUrls().get( i ), errorPingState.getWebUrlsTime().get( i ) ) );
        }
        return stringBuffer.toString();
    }

    public AsyncTask doPing( final Activity aActivity, final String aUrl, final ResponseListener callBackListener )
    {
        String cUrl = StringUtils.complicURL( aUrl );
        HttpAsync async = new HttpAsync( aActivity, callBackListener );
        return async.executeOnExecutor( Executors.newCachedThreadPool(), cUrl, aUrl );
    }

    private void appendResultsText ( final String text )
    {
        stringBuffer.append( text + "\n" );
    }

    private class HttpAsync extends AsyncTask< String , Void , String >
    {
        private String mUrl = null;
        private String redirectUrl = null;
        private Activity mActivity = null;
        private ResponseListener mListener = null;
        private HttpAsync( Activity aActivity, ResponseListener callBackListener )
        {
            this.mActivity = aActivity;
            this.mListener = callBackListener;
        }

        @Override
        protected String doInBackground ( String... strings )
        {
            Log.e( "Ping ", "doPing.. " + strings[0] );

            HttpURLConnection connection = null;
            InputStream input = null;
            try
            {
                mUrl = strings[0];
                redirectUrl = strings[1];
                connection = recursiveRequest(strings[0]);
                if( connection == null ) return null;
                input = connection.getInputStream();

                BufferedReader bs       = new BufferedReader( new InputStreamReader( input ) );
                StringBuffer   response = new StringBuffer();
                String         line     = null;

                while( ( line = bs.readLine() ) != null )
                {
                    line = line.trim();
                    response.append( line );
                }
                bs.close();
                input.close();
                System.out.println( "res= " + response.toString() );
                return response.toString();
            }
            catch ( IOException e )
            {
                // Log exception
                e.printStackTrace();
            }
            return null;
        }

        private HttpURLConnection recursiveRequest( String path )
        {
            HttpURLConnection connection;
            URL url = null;
            try
            {
                url = new URL(path);
                connection = ( HttpURLConnection ) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setDoOutput(true);
                connection.setInstanceFollowRedirects( true );
                connection.setRequestMethod("GET"); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
                connection.connect();
//                int code = connection.getResponseCode();// Network block
//                if (code >= 300 && code < 400) {
                if (false) {
                    String location = connection.getHeaderField("Location");
                    if (location == null) {
                        location = connection.getHeaderField("location");
                    }
                    Log.i( "Ping ", "recursiveRequest url " + location );

                    if (location != null) {
                        if (!(location.startsWith("http://") || location
                                .startsWith("https://"))) {
                            //某些时候会省略host，只返回后面的path，所以需要补全url
                            URL originalUrl = new URL(path);
                            location = originalUrl.getProtocol() + "://"
                                    + originalUrl.getHost() + location;
                        }
                        return recursiveRequest(location);
                    } else {
                        // 无法获取location信息，让浏览器获取
                        return null;
                    }
                }else
                {
                    return connection;
                }
            }
            catch ( MalformedURLException e )
            {
                Log.w( TAG, "recursiveRequest MalformedURLException" );
            }
            catch ( IOException e )
            {
                Log.w( TAG, "recursiveRequest IOException" );
            }
            catch ( Exception e )
            {
                Log.w( TAG, "unknow exception" );
            }
            return null;
        }

        @Override
        protected void onPostExecute ( String s )
        {
            try
            {
                String domain = new URL(mUrl).getHost();
                mListener.onTaskCompleted( domain, s );
            }
            catch ( MalformedURLException e )
            {
                e.printStackTrace();
            }
        }
    }

    public interface CallBackListener
    {
        public void onTaskCompleted ( ErrorPingState errorPingState );
    }
    public interface ResponseListener
    {
        public void onTaskCompleted ( String domain, String response );
    }

    public void release()
    {
        _instance = null;
    }

    private        StringBuffer   stringBuffer   = null;
    private        ErrorPingState errorPingState = null;
    private static CheckLinkModel      _instance      = null;
    private        int            pingTimes      = 2;
}
