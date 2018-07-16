package library.itstar.wei.tbsx5.model;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

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

import library.itstar.wei.tbsx5.def.WebServiceSet;
import library.itstar.wei.tbsx5.local.AyncUpdateListener;
import library.itstar.wei.tbsx5.local.CallBackListener;
import library.itstar.wei.tbsx5.utils.NetWorkUtil;

public class ConfigAsyncTimeoutTask
{
    public static ConfigAsyncTimeoutTask instance()
    {
        if( _instance == null )
        {
            _instance = new ConfigAsyncTimeoutTask();
        }

        return _instance;
    }
    public void getSystemConfig
            (
                    CallBackListener aCallBackListener,
                    AyncUpdateListener ayncUpdateListener,
                    HashMap<String, String> map,
                    Context aContext,
                    String url
            )
    {
        _context = aContext;
        JSONObject jsonObject = null;
        try
        {
            jsonObject = new JSONObject();
            jsonObject.put( "portalAccountEmail", "" );
        }
        catch ( JSONException e)
        {
            e.printStackTrace();
        }

        HttpAsync async = new HttpAsync( map );
        async.setCallBackListener( aCallBackListener );
        async.setAyncUpdateListener( ayncUpdateListener );
        async.executeOnExecutor( Executors.newCachedThreadPool(), url + WebServiceSet.APP_LOCAL_PATH_REDIRE );
//        async.execute( WebServiceSet.APP_SSL_CONFIG );
    }


    private class HttpAsync extends AsyncTask< String , Integer , String >
    {
        private HttpAsync ( HashMap< String, String> aPostParams )
        {
            _str_post_params = postParams( aPostParams );
        }
        private HttpAsync( JSONObject aObjectJSON, String aFilePath )
        {
            json_object = aObjectJSON;
        }
        private HttpAsync( JSONObject aObjectJSON )
        {
            json_object = aObjectJSON;
        }
        @Override
        protected String doInBackground( String... aUrl )
        {
            if( !NetWorkUtil.checkInternetConnection( _context ) )
            {
                error_code = -1;
//                return null;
            }

            int timeout_count = 0;
            while ( connecting && timeout_count < connectTimes )
            {
                timeout_count++;
                publishProgress( timeout_count );
                connSocketServer( aUrl[0] );
            }

            if( connecting )
            {
                error_code = -2;
//                return null;
            }

            return timeout_count >= 3? null : this.response;
        }

        @Override
        protected void onPostExecute( String result )
        {
            if( error_code < 0 && error_code == -2 )
            {
//                ShowDialog.showMessageErrorDialog( _context, _context.getString( R.string.dialog_please_check_connect_time_out ), _context.getString( R.string.dialog_title_error )  );
            }
            if( error_code < 0 && error_code == -1 )
            {
//                ShowDialog.showMessageErrorDialog( _context, _context.getString( R.string.dialog_please_check_your_wifi ), _context.getString( R.string.dialog_title_error )  );
            }
            response = result;
            callback.onTaskCompleted( result );
        }

        @Override
        protected void onProgressUpdate ( Integer... values )
        {
            updateback.onProgressUpdate( String.valueOf( values[0] ) );
//            _launch_loading.setText( String.valueOf( values[0] ) );
        }

        public void setCallBackListener( CallBackListener aCallBackListener )
        {
            callback = aCallBackListener;
        }
        public void setAyncUpdateListener( AyncUpdateListener aCallBackListener )
        {
            updateback = aCallBackListener;
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

                response        = new StringBuffer();
                url = new URL(aURL);
                connection = ( HttpURLConnection ) url.openConnection();
                connection.setReadTimeout(timeout_retry_times); //禁用Timeout
                connection.setConnectTimeout(timeout_retry_times); //禁用Timeout
                connection.setDoOutput(true);
                connection.setRequestMethod("POST"); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
                connection.connect();


//                System.out.println( "===============2==============" + _str_post_params );
                DataOutputStream wr = new DataOutputStream( connection.getOutputStream ());
                wr.writeBytes(""+ _str_post_params);

                wr.flush();
                wr.close ();
//                System.out.println( "===============3==============" );
                int code = connection.getResponseCode();

                if( code == 200 )
                {
//                    System.out.println( "===============4==============" );
                    is = connection.getInputStream();

                    bs   = new BufferedReader( new InputStreamReader( is ));

                    String line = null;
                    while( ( line = bs.readLine() ) != null )
                    {
                        line = line.trim();

                        response.append( line );
                    }
                    this.response = response.toString();
//                    System.out.println( "res_node = " + response.toString() );

                    if ( response == null || response.toString().trim().equalsIgnoreCase( "null" ) || !JSONModel.instance().isJSONValid( response.toString() ) )
                    {
                        return;
                    }
                    connecting = false;
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
                if( bs != null )
                {
                    try
                    {
                        bs.close();
                        bs = null;
                    }
                    catch( Throwable t2 )
                    {
                    }
                }
                return;

            }
            finally
            {
//                System.out.println( "===============finally==============" );
//                connecting = false;
                if(connection != null) {
                    connection.disconnect();
                }
                if( bs != null )
                {
                    try
                    {
                        bs.close();
                        bs = null;
                    }
                    catch( IOException e ){ };
                }

                if( is != null )
                {
                    try
                    {
                        is.close();
                        is = null;
                    }
                    catch( IOException e ){ };
                }

            }
        }


        protected String getReponse()
        {
            return response;
        }

        private String           response         = null;
        private CallBackListener callback         = null;
        private AyncUpdateListener updateback         = null;
        private JSONObject       json_object      = null;
        private String           _str_post_params = null;
        private boolean          connecting       = true; //是否正在連結的旗標
        private int              error_code       = 0; //是否正在連結的旗標
        private int              connectTimes       = 2; //數字減一
        private int              timeout_retry_times       = 8000; //timeout retry times m secound

    }

    private static       ConfigAsyncTimeoutTask _instance = null;
    private static       Context          _context  = null;
}
