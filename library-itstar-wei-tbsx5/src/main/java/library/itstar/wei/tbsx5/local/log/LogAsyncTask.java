package library.itstar.wei.tbsx5.local.log;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;

import library.itstar.wei.tbsx5.def.SharedPreferencesKey;
import library.itstar.wei.tbsx5.local.AppConfig;
import library.itstar.wei.tbsx5.local.SystemConfig;
import library.itstar.wei.tbsx5.model.IPAsyncTask;
import library.itstar.wei.tbsx5.model.JSONModel;
import library.itstar.wei.tbsx5.utils.AndroidUtil;
import library.itstar.wei.tbsx5.utils.FastJsonUtils;
import library.itstar.wei.tbsx5.utils.LogUtil;
import library.itstar.wei.tbsx5.utils.NetWorkUtil;


/**
 * Created by Ching Wei on 2018/7/11.
 */

public class LogAsyncTask
{

    public static void construct( Context aContext )
    {
        if( _instance == null )
        {
            _instance = new LogAsyncTask();
        }
        if( aContext != null )_context = aContext;
    }

    public static LogAsyncTask instance ()
    {
        if ( _instance == null )
        {
            _instance = new LogAsyncTask();
        }
        return _instance;
    }

    public void sendLog( String requestURL, String account, String userAgent, int status, String response )
    {
        initSendAccount ();
        LogDatasState state = new LogDatasState();
        state.setAccount( account == null? sendAccount : account );
        state.setDevice_id( AndroidUtil.getAndroidId( _context ) );
        state.setAppapp( AppConfig.getAppAPP() );
        state.setRequest_url( requestURL );
        state.setUesr_agent( userAgent );
        state.setStatus( String.valueOf( status ) );
        state.setResponse( response );
        if( status == 0 )
        {
            state.setPhone_start_time( String.valueOf( System.currentTimeMillis() ) );
        }
        else
        {
            state.setPhone_end_time( String.valueOf( System.currentTimeMillis() ) );
        }
        LogDatasState state_datas = new LogDatasState();
        state_datas.setDatas( state );
//        state_basic.setBasic( state );

        HashMap<String, String> map = new HashMap<>();
        map.put( "aplAcc", sendAccount );
        map.put( "crlMode", "app_log" );
        map.put( "content", FastJsonUtils.getJsonString( state_datas ) );
//        map.put( "isDebug",  "0" );

        HttpAsync async = new HttpAsync( map );
        async.executeOnExecutor( Executors.newCachedThreadPool(), "https://appctl.bckappgs.info/app_log/apl_insertweilog.php" );
    }

    public void sendBasic( String browserCore, String account )
    {
        initSendAccount ();
        LogBasicState state = new LogBasicState();
        state.setAccount( account == null? sendAccount : account );
        state.setDevice_id( AndroidUtil.getAndroidId( _context ) );
        state.setDevice_os( "Android" );
        state.setDevice_branch( Build.VERSION.RELEASE );
        state.setDevice_name( Build.BRAND + "," + Build.MODEL );
        state.setApp_version( AppConfig.getAppVersion() );
        state.setDevice_version( Build.DEVICE );
        state.setSdk_version( AppConfig.getAppVersion() );
        state.setDevice_browser_core( browserCore );
        LogBasicState state_basic = new LogBasicState();
        state_basic.setBasic( state );

        HashMap<String, String> map = new HashMap<>();
        map.put( "aplAcc", sendAccount );
        map.put( "crlMode", "device_inf" );
        map.put( "content",FastJsonUtils.getJsonString( state_basic ) );
//        map.put( "isDebug",  "0" );

        HttpAsync async = new HttpAsync( map );
        async.executeOnExecutor( Executors.newCachedThreadPool(), "https://appctl.bckappgs.info/app_log/apl_insertweilog.php" );
    }

    private void initSendAccount ()
    {
        NetWorkUtil.setPublicIP( IPAsyncTask.instance().doQueryCurrentIP() );
        String account = SystemConfig.instance().getSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_WEBVIEW_ACCOUNT_COOKIES, null );
        if( account == null )
        {
            if ( NetWorkUtil.getPublicIP() != null )
            {
                sendAccount = NetWorkUtil.getPublicIP();
                LogUtil.logError( LogUtil.TAG, "Public IP : " + NetWorkUtil.getPublicIP() );
            }
            else
            {
                sendAccount = NetWorkUtil.getPhoneIPAddrs( 1 );
                LogUtil.logError( LogUtil.TAG, "Phone IP : " + NetWorkUtil.getPhoneIPAddrs( 1 ) );
            }
        }
        else
        {
            sendAccount = account;
            LogUtil.logError( LogUtil.TAG, "account : " + account );
        }
    }

    private class HttpAsync extends AsyncTask< String, Integer, String >
    {
        private int    timeout_retry_times = 8000; //timeout retry times m secound
        private String response            = null;
        private String _str_post_params    = null;

        private HttpAsync ( HashMap< String, String>  aPostParams )
        {
            _str_post_params = postParams( aPostParams );
        }

        @Override
        protected String doInBackground ( String... aUrl )
        {
            connSocketServer( aUrl[0] );
            return null;
        }

        private void connSocketServer( String aURL )
        {
            BufferedReader    bs         = null;
            StringBuffer      response   = null;
            URL               url        = null;
            InputStream       is         = null;
            HttpURLConnection connection = null;
            try
            {
//                System.out.println( "===============1==============" );

                response = new StringBuffer();
                url = new URL( aURL );
                connection = ( HttpURLConnection ) url.openConnection();
                connection.setReadTimeout( timeout_retry_times ); //禁用Timeout
                connection.setConnectTimeout( timeout_retry_times ); //禁用Timeout
                connection.setDoOutput( true );
                connection.setRequestMethod( "POST" ); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8" ); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
                connection.connect();


//                System.out.println( "===============2==============" + _str_post_params );
                DataOutputStream wr = new DataOutputStream( connection.getOutputStream() );
                wr.writeBytes( "" + _str_post_params );

                wr.flush();
                wr.close();
//                System.out.println( "===============3==============" );
                int code = connection.getResponseCode();

                if ( code == 200 )
                {
//                    System.out.println( "===============4==============" );
                    is = connection.getInputStream();

                    bs = new BufferedReader( new InputStreamReader( is ) );

                    String line = null;
                    while ( ( line = bs.readLine() ) != null )
                    {
                        line = line.trim();

                        response.append( line );
                    }
                    this.response = response.toString();
                    System.out.println( "res_node = " + response.toString() );

                    if ( response == null || response.toString().trim().equalsIgnoreCase( "null" ) || !JSONModel.instance().isJSONValid( response.toString() ) )
                    {
                        return;
                    }
                    return;
                }
                else
                {
//                    System.out.println( "===============5==============" );
//                    is = connection.getErrorStream();
//                    is.toString();
//                    bs   = new BufferedReader( new InputStreamReader( is ));
//
//                    String line = null;
//                    while( ( line = bs.readLine() ) != null )
//                    {
//                        line = line.trim();
//
//                        response.append( line );
//                    }
//                    this.response = response.toString();
//                    System.out.println( "res_node = " + response.toString() );
                    return;
                }
            }
            catch ( SocketTimeoutException e )
            {
//                e.printStackTrace();
//                connSocketServer( aURL );
            }
            catch ( Exception e )
            {
                try
                {
                    Thread.sleep( timeout_retry_times );
                }
                catch ( InterruptedException e1 )
                {
                    e1.printStackTrace();
                }
                e.printStackTrace();
                if ( bs != null )
                {
                    try
                    {
                        bs.close();
                        bs = null;
                    }
                    catch ( Throwable t2 )
                    {
                    }
                }
                return;

            }
            finally
            {
//                System.out.println( "===============finally==============" );
//                connecting = false;
                if ( connection != null )
                {
                    connection.disconnect();
                }
                if ( bs != null )
                {
                    try
                    {
                        bs.close();
                        bs = null;
                    }
                    catch ( IOException e )
                    {
                    }
                }

                if ( is != null )
                {
                    try
                    {
                        is.close();
                        is = null;
                    }
                    catch ( IOException e )
                    {
                    }
                }

            }
        }

        private String postParams( HashMap< String, String> aPostParams )
        {
            StringBuilder postParams = new StringBuilder();
//            postParams.append( "?" );
            for ( Iterator<Map.Entry<String, String>> entries = aPostParams.entrySet().iterator(); entries.hasNext(); )
            {
                Map.Entry<String, String> entry = entries.next();
                postParams.append( entry.getKey() + "=" + entry.getValue() );
//                Log.i( "Iterator", entry.getKey() + "=" + entry.getValue() );

                if( entries.hasNext() )
                {
                    postParams.append( "&" );
                }
            }
            return postParams.toString();
        }
    }

    private static LogAsyncTask _instance = null;
    private static Context      _context  = null;
    private String sendAccount = null;
}
