package library.itstar.wei.tbsx5.model;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import library.itstar.wei.tbsx5.def.WebServiceSet;
import library.itstar.wei.tbsx5.utils.NetWorkUtil;


/**
 * Created by Ching Wei on 2017/11/9.
 */

public class IPAsyncTask
{
    private static       IPAsyncTask _instance = null;

    public static void construct( Context aContext )
    {
        if( _instance == null )
        {
            _instance = new IPAsyncTask( aContext );
        }

        try
        {
            NetWorkUtil.setPublicIP( null );
            _instance.getCurrentIP( aContext );
        }
        catch ( ExecutionException e )
        {
            e.printStackTrace();
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
    }
    protected IPAsyncTask ( Context aContext )
    {
        _context = aContext;
    }
    protected IPAsyncTask (){}

    public static IPAsyncTask instance()
    {
        if( _instance == null )
        {
            _instance = new IPAsyncTask();
        }

        return _instance;
    }

    public String doQueryCurrentIP()
    {
        try
        {
            NetWorkUtil.setPublicIP( null );
            return getCurrentIP( _context );
        }
        catch ( ExecutionException e )
        {
            e.printStackTrace();
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        return null;
    }
    private String getCurrentIP ( Context aContent ) throws ExecutionException, InterruptedException
    {
        HttpAsync async = new HttpAsync( aContent );
        return async.execute( ).get();
    }
    private class HttpAsync extends AsyncTask< String , Void , String >
    {
        String extIP;
        private final Context context;
        // android IP
        public HttpAsync(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground ( String... strings )
        {
            extIP = getCurrentIP( WebServiceSet.APP_SSL_GETIP );
            try
            {
                if ( extIP != null && IP_ADDRESS.matcher( extIP ).matches() )
                {
                    return extIP;
                }
                else
                {
                    return null;
                }
            }
            catch ( Exception e )
            {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            return;
        }

        @Override
        protected void onPostExecute( String arg )
        {
            NetWorkUtil.setPublicIP( arg );
            return;
        }

        private final Pattern IP_ADDRESS
                = Pattern.compile(
                "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                        + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                        + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                        + "|[1-9][0-9]|[0-9]))");
    }



    private String getCurrentIP( String aURL )
    {
        BufferedReader    bs         = null;
        StringBuffer      response   = null;
        URL               url        = null;
        InputStream       is         = null;
        HttpURLConnection connection = null;
        try
        {

            response        = new StringBuffer();
            url = new URL(aURL);
            connection = ( HttpURLConnection ) url.openConnection();
            connection.setReadTimeout(1000);
            connection.setDoOutput(true);
            connection.setRequestMethod("GET"); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            connection.connect();


            DataOutputStream wr = new DataOutputStream( connection.getOutputStream ());
            wr.writeBytes("");

            wr.flush();
            wr.close ();
            int code = connection.getResponseCode();

            if( code == 200 )
            {
                is = connection.getInputStream();

                bs   = new BufferedReader( new InputStreamReader( is ));

                String line = null;
                while( ( line = bs.readLine() ) != null )
                {
                    line = line.trim();

                    response.append( line );
                }
                return response.toString();
            }
            else
            {
                is = connection.getErrorStream();
                is.toString();
                bs   = new BufferedReader( new InputStreamReader( is ));

                String line = null;
                while( ( line = bs.readLine() ) != null )
                {
                    line = line.trim();

                    response.append( line );
                }
                return null;
            }
        }
        catch ( SocketTimeoutException e )
        {
            e.printStackTrace();
//                connSocketServer( aURL );
        }
        catch (Exception e)
        {
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
            return null;

        }
        finally
        {
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
        return null;
    }


    private static Context           _context     = null;
}
