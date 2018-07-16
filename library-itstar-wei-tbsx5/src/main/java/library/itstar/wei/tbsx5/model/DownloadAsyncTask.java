package library.itstar.wei.tbsx5.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import library.itstar.wei.tbsx5.local.AyncUpdateListener;
import library.itstar.wei.tbsx5.local.CallBackListener;


/**
 * Created by Ching Wei on 2018/2/26.
 */

public class DownloadAsyncTask
{
    private static final String             DATA       = "12345678";
    private static       DownloadAsyncTask  _instance  = null;
    private static       Context            _context   = null;

    public static DownloadAsyncTask instance()
    {
        if( _instance == null )
        {
            _instance = new DownloadAsyncTask();
        }

        return _instance;
    }

    public String apkDownload( CallBackListener aCallBackListener, AyncUpdateListener ayncUpdateListener, String aSorucePath, String aDesDire )
    {
        int    idx = aSorucePath.lastIndexOf( "." );
        String ext = aSorucePath.substring( idx );

        HttpAsync async = new HttpAsync( aSorucePath, aDesDire + File.separator + ext );
        async.setCallBackListener( aCallBackListener );
        async.setAyncUpdateListener( ayncUpdateListener );
        try
        {
            return async.execute().get();
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        catch ( ExecutionException e )
        {
            e.printStackTrace();
        }
        return null;
    }

    public String imgDownload( CallBackListener aCallBackListener, AyncUpdateListener ayncUpdateListener, String aSorucePath, String aDesDire )
    {
        HttpAsync async = new HttpAsync( aSorucePath, aDesDire );
        async.setCallBackListener( aCallBackListener );
        async.setAyncUpdateListener( ayncUpdateListener );
        try
        {
            return async.execute().get();
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        catch ( ExecutionException e )
        {
            e.printStackTrace();
        }
        return null;
    }

    private class HttpAsync extends AsyncTask<String, Integer, String>
    {
        private String             mURL       = null;
        private String             desDire    = null;
        private CallBackListener   callback   = null;
        private AyncUpdateListener updateback = null;
        private HttpAsync ( String aURL, String aDesDire )
        {
            mURL = aURL;
            desDire = aDesDire;
        }

        public void setCallBackListener( CallBackListener aCallBackListener )
        {
            callback = aCallBackListener;
        }
        public void setAyncUpdateListener( AyncUpdateListener aCallBackListener )
        {
            updateback = aCallBackListener;
        }

        protected String doInBackground(String... urls)
        {
            URL               url        = null;
            InputStream       is         = null;
            HttpURLConnection connection = null;
            String            path       = mURL;
            String            tmpAPKPath = desDire;
            String ext      = mURL.substring( mURL.lastIndexOf( "." ) );
            String filename = mURL.substring( mURL.lastIndexOf( '/' ) + 1, mURL.lastIndexOf( "." ) );
            int               file_length;

            Log.i("Info: path", path);
            try {
                url = new URL( path );
                connection = ( HttpURLConnection ) url.openConnection();
                connection.setDoOutput( true );
                connection.connect();
                file_length = connection.getContentLength();

                File new_folder = new File( tmpAPKPath );
                if (!new_folder.exists()) {
                    if (new_folder.mkdir()) {
                        Log.i("Info", "Folder succesfully created");
                    } else {
                        Log.i("Info", "Failed to create folder");
                    }
                } else {
                    Log.i("Info", "Folder already exists");
                }

                /**
                 * Create an output file to store the image for download
                 */
                String output_filename;
                if( ext.equalsIgnoreCase( ".apk" ) )
                {
                    output_filename = "tmpNew.apk";
                }
                else
                {
                    output_filename = new Date().getTime() + ext;
                }
                File         output_file  = new File( new_folder, output_filename );
                Log.i("Info: path", output_file.getAbsolutePath());
                OutputStream outputStream = new FileOutputStream( output_file );

                InputStream inputStream = new BufferedInputStream( url.openStream(), 8192 );
                byte[]      data        = new byte[ 4096 ];
                long         total       = 0;
                int         count;
                while ( ( count = inputStream.read( data ) ) != -1 )
                {
                    total += count;

                    outputStream.write(data, 0, count);
                    int progress = (int) (total * 100 / file_length);
                    publishProgress(progress);
                }
                inputStream.close();
                outputStream.close();

                Log.i("Info", "file_length: " + Integer.toString(file_length));

                return output_file.getAbsolutePath();
//                Log.i("Info: des path", output_file.getAbsolutePath());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Download complete.";
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            updateback.onProgressUpdate( String.valueOf( values[ 0 ] ) );
        }

        @Override
        protected void onPostExecute ( String sResult )
        {
            callback.onTaskCompleted( sResult );
        }

    }
}