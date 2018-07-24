package library.itstar.wei.tbsx5;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import library.itstar.wei.tbsx5.def.SharedPreferencesKey;
import library.itstar.wei.tbsx5.local.AppConfig;
import library.itstar.wei.tbsx5.local.AyncUpdateListener;
import library.itstar.wei.tbsx5.local.CallBackListener;
import library.itstar.wei.tbsx5.local.SystemConfig;
import library.itstar.wei.tbsx5.local.ViewConfig;
import library.itstar.wei.tbsx5.local.log.LogRun;
import library.itstar.wei.tbsx5.model.CheckLinkModel;
import library.itstar.wei.tbsx5.model.ConfigAsyncTimeoutTask;
import library.itstar.wei.tbsx5.model.DownloadAsyncTask;
import library.itstar.wei.tbsx5.model.JSONModel;
import library.itstar.wei.tbsx5.model.ShowDialog;
import library.itstar.wei.tbsx5.utils.FileUtils;
import library.itstar.wei.tbsx5.utils.LibraryFileProvider;
import library.itstar.wei.tbsx5.utils.LogUtil;
import library.itstar.wei.tbsx5.utils.NetWorkUtil;
import library.itstar.wei.tbsx5.utils.StringUtils;
import library.itstar.wei.tbsx5.view.SpinKit.SpinKitView;
import library.itstar.wei.tbsx5.view.act.X5ViewActivity;
import library.itstar.wei.tbsx5.view.act.X5ViewToolbarActivity;
import library.itstar.wei.tbsx5.view.x5.X5WebView;

/**
 * Created by Ching Wei on 2018/7/13.
 */

public class X5Lib
{
    private static TextView           mLaunchLoading      = null;
    private static Activity           mActivity           = null;
    private static SpinKitView        mSpinKitView        = null;
    private static PingFinishListener mPingFinishListener = null;
    private static boolean            mStopHandler    = false;
    private static android.os.Handler mHandler        = new android.os.Handler();
    private static int mCount = 0;
    private static int urlConnTimes = 0;
    private static int urlRealCount = 0;
    private static int dbConnTimes = 0;
    private static int dbRealCount = 0;
    private static String browserCore = null;

    public enum BuglyStatus
    {
        RUNNING,FINISH,NO_USE
    }

    private static Runnable runnable = new Runnable()
    {
        @Override
        public void run ()
        {
            if ( !mStopHandler )
            {
               if( ++mCount > 2 )
                {
                    mActivity.runOnUiThread( new Runnable()
                    {
                        @Override
                        public void run ()
                        {
                            if( mActivity != null && !ShowDialog.instance().isShowing() )
                            {
                                ShowDialog.instance().showMessageErrorDialog( mActivity, mActivity.getString( R.string.dialog_system_error ), mActivity.getString( R.string.dialog_title_error ) );
                                if( mLaunchLoading != null )
                                {
                                    mLaunchLoading.setVisibility( View.VISIBLE );
                                    mLaunchLoading.setText( mActivity.getString( R.string.dialog_system_error ) );
                                }
                            }
                        }
                    } );

                }
                else
                {
                    if( mCount > 1 )
                        Toast.makeText( mActivity, mActivity.getString( R.string.dialog_system_retry ), Toast.LENGTH_SHORT ).show();
                    doTranfer( mActivity );
                }
            }
        }
    };

    public static void initX5( final Context context )
    {
        TbsDownloader.needDownload( context, false );

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
//                if( !arg0 ) AppConfig.setBuglyRun( true );
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };

        QbSdk.initX5Environment( context, cb );
        QbSdk.setDownloadWithoutWifi( false );

        if ( !QbSdk.isTbsCoreInited() )
        {
//            AppConfig.setBuglyRun( true );
            QbSdk.preInit( context, null );// 设置X5初始化完成的回调接口
        }

        QbSdk.setTbsListener( new TbsListener()
        {
            @Override
            public void onDownloadFinish ( int i )
            {
                SystemConfig.getNowActivity().runOnUiThread( new Runnable()
                {
                    @Override
                    public void run ()
                    {
                        mLaunchLoading.setText( String.format( mActivity.getString( R.string.launch_browser_x5_text ) + "%s %%", 100 ) );
                    }
                } );
            }

            @Override
            public void onInstallFinish ( int i )
            {
                mActivity.runOnUiThread( new Runnable()
                {
                    @Override
                    public void run ()
                    {
                        ShowDialog.instance().showBuglyRestartMessageDialog( mActivity, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick ( DialogInterface dialogInterface, int i )
                            {
                                android.os.Process.killProcess( android.os.Process.myPid() );
                                System.exit( 0 );
                            }
                        } );
                    }
                } );
                AppConfig.setTbsX5Run( false );
            }

            @Override
            public void onDownloadProgress ( int i )
            {
                final int percent = i;
                SystemConfig.getNowActivity().runOnUiThread( new Runnable()
                {
                    @Override
                    public void run ()
                    {
                        mLaunchLoading.setText( String.format( mActivity.getString( R.string.launch_browser_x5_text ) + "%s %%", percent ) );
                    }
                } );

                AppConfig.setTbsX5Run( true );
            }
        });
    }

    public static void setStyleToolbar( int styleToolbar )
    {
        ViewConfig.setViewStyle( styleToolbar );
    }
    public static void init ( final Activity activity, String appVersion, String appApp, String isDev )
    {
        LogUtil.logInfo( LogUtil.TAG, "X5Lib init" );
        LogUtil.logInfo( LogUtil.TAG, "App Version:[" + appVersion + "]; APP:[" + appApp + "]; isDev:[" + isDev + "]");
        mActivity = activity;
        mCount = 0;

        AppConfig.setAppAPP( appApp );
        AppConfig.setAppVersion( appVersion );
        AppConfig.setIsDev( isDev );

        SystemConfig.setNowActivity( activity );
        SystemConfig.construct( mActivity );
//        LogAsyncTask.construct( mActivity );
        SystemConfig.instance().resetWebURL();
    }

    public static void initView ( TextView launchLoading, SpinKitView spinKitView, int lunchImage )
    {
        mLaunchLoading = launchLoading;
        mSpinKitView = spinKitView;
        AppConfig.setViewLunch( lunchImage );

        if( mLaunchLoading != null )
        {
            mLaunchLoading.setOnLongClickListener( new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick ( View view )
                {
                    ((TextView) view).setText( LogRun.print() );
                    return false;
                }
            } );
        }
        checkTbsX5();
//        LogAsyncTask.instance().sendBasic( browserCore, null );
    }
    public static void start ()
    {
        checkTbsX5();
        construct( mActivity );
    }

    private static void construct( final Activity activity )
    {
        LogRun.append( "Activity initialize" );
//        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
//        {
//            int      PERMISSION_ALL = 1;
//            String[] PERMISSIONS    = { Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE };
//
//            if ( !hasPermissions( activity, PERMISSIONS ) )
//            {
//                ActivityCompat.requestPermissions( activity, PERMISSIONS, PERMISSION_ALL );
//            }
//        }

        if ( !NetWorkUtil.checkInternetConnection( activity ) )
        {
            ShowDialog.instance().showButtonRestartMessageDialog( activity, activity.getString( R.string.dialog_please_check_your_wifi ), activity.getString( R.string.dialog_title_error ), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick ( DialogInterface dialogInterface, int i )
                {
                    dialogInterface.dismiss();
                    start();
                }
            } );
            return;
        }

        if ( AppConfig.isTbsX5Run() )
        {
            return;
        }

        String webFilePath = activity.getFilesDir().toString() + File.separator + "web.txt";
        String resString = null;
        if( !FileUtils.isFileExist( webFilePath ) )
        {
            resString = load(activity,"defaultAppInfo");
            LogRun.append( "Load native config" );
        }
        else
        {
            resString = FileUtils.readFile( webFilePath, "UTF-8" ).toString();
            LogRun.append( "Load sdcard config" );
        }
        if( JSONModel.instance().isJSONValid( resString ) )
        {
            JSONModel.instance().setJSONObject( resString );
            if( JSONModel.instance().getDomainUrl(AppConfig.getIsDev().contains( "1" )) == null )
            {
                JSONModel.instance().setJSONObject( load(activity,"defaultAppInfo") );
            }
        }
        else
        {
            JSONModel.instance().setJSONObject( load(activity,"defaultAppInfo") );
        }
        LogUtil.logInfo( LogUtil.TAG, "App Info: " + resString );

        mActivity.runOnUiThread( new Runnable()
        {
            @Override
            public void run ()
            {
                if( mSpinKitView != null )
                    mSpinKitView.setVisibility( View.VISIBLE );
                if( mLaunchLoading != null )
                    mLaunchLoading.setText( activity.getString( R.string.launch_loading ) );
            }
        });

        if ( JSONModel.instance().getFeebackUpdate() )
        {
            LogRun.append( "Start Connection Server" );
            ArrayList list = JSONModel.instance().getDomainUrl(AppConfig.getIsDev().contains( "1" ));
            dbConnTimes = list.size();
            dbRealCount = 0;
            for ( Iterator i = list.iterator(); i.hasNext(); )
            {
                String dUrl = ( String ) i.next();
                if( !URLUtil.isValidUrl( dUrl ) )
                {
                    dbConnTimes--;
                    continue;
                }
//                doDBConnect(activity, dUrl, !i.hasNext());
                doDBConnect(activity, dUrl);
            }
        }
    }

    private static void doDBConnect( final Activity activity, final String url )
    {
        HashMap<String, String> map = new HashMap<>();
        map.put( "app_version", AppConfig.getAppVersion() );
        map.put( "OS", AppConfig.getAppOS() );
        map.put( "App", AppConfig.getAppAPP() );
        map.put( "IsDev",  AppConfig.getIsDev() );

        ConfigAsyncTimeoutTask.instance().getSystemConfig(
                new CallBackListener()
                {
                    @Override
                    public void onTaskCompleted ( String response )
                    {
                        dbRealCount++;
                        String tmpUrl = SystemConfig.instance().getSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_KEY_DOMAIN_URL, null );
                        LogRun.append( "End Connection Server" );

                        if ( response == null || response.trim().equalsIgnoreCase( "null" ) || !JSONModel.instance().isJSONValid( response ) || !JSONModel.instance().isJSONFooBar( response ) )
                        {
                            if( dbRealCount == dbConnTimes && tmpUrl == null )
                            {
                                if( mLaunchLoading != null )
                                {
                                    mActivity.runOnUiThread( new Runnable()
                                    {
                                        @Override
                                        public void run ()
                                        {
                                            mLaunchLoading.setVisibility( View.VISIBLE );
                                            mLaunchLoading.setText( activity.getString( R.string.dialog_config_format_error ) );
                                        }
                                    });
                                }
                                LogRun.append( "Connection Config Server Error" );
                                mHandler.postDelayed( runnable, 100 );
                            }
                            LogUtil.logInfo( LogUtil.TAG, "Error DB Domain: " + url );
                            return;
                        }
                        LogUtil.logInfo( LogUtil.TAG, response );

                        if( tmpUrl == null && JSONModel.instance().isJSONFooBar( response ) )
                        {
                            LogRun.append( "DB Domain URL: " + url );
                            SystemConfig.instance().putSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_KEY_DOMAIN_URL, url );
                            JSONModel.instance().setJSONObject( response );

                            if ( JSONModel.instance().getFeebackUpdate() )
                            {
                                LogRun.append( "Feeback is true" );
                                FileUtils.writeFile( activity.getFilesDir().toString() + File.separator + "web.txt", response );
                                if( runnable != null && mHandler != null ) mHandler.postDelayed( runnable, 100 );
                            }
                            else
                            {
                                final String url = JSONModel.instance().getAppUpDateUrl().replace( ".html", ".apk" );
                                LogUtil.logInfo( LogUtil.TAG, "download apk..." + url );
                                if( !URLUtil.isValidUrl( url ) )
                                {
                                    ShowDialog.instance().showMessageErrorDialog( activity, JSONModel.instance().getAppUpDateUrl(), "网页开启错误" );
                                }
                                else
                                {
                                    new Thread( new Runnable()
                                    {
                                        @Override
                                        public void run ()
                                        {
                                            try
                                            {
                                                DownloadAsyncTask.instance().apkDownload(
                                                        new CallBackListener()
                                                        {
                                                            @Override
                                                            public void onTaskCompleted ( String response )
                                                            {
                                                                Intent intent = new Intent( Intent.ACTION_VIEW );
                                                                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N )
                                                                {
                                                                    intent.setFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );
                                                                    Uri contentUri = LibraryFileProvider.getUriForFile( activity, activity.getApplicationContext().getPackageName() + ".fileProvider", new File( response ) );
                                                                    intent.setDataAndType( contentUri, "application/vnd.android.package-archive" );
                                                                }
                                                                else
                                                                {
                                                                    intent.setDataAndType( Uri.fromFile( new File( response ) ), "application/vnd.android.package-archive" );
                                                                    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                                                }
                                                                activity.startActivity( intent );
                                                                activity.finish();
                                                            }
                                                        },
                                                        new AyncUpdateListener()
                                                        {
                                                            @Override
                                                            public void onProgressUpdate ( String response )
                                                            {
                                                                if( mLaunchLoading != null )
                                                                    mLaunchLoading.setText( String.format( activity.getString( R.string.dialog_system_update ) + "%s %%", response ) );
                                                            }
                                                        },
                                                        url,
                                                        activity.getExternalCacheDir().getAbsolutePath()
                                                );
                                            }
                                            catch ( Exception e )
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    } ).start();
                                }
                            }
                        }
                        else
                        {
                            if( dbRealCount == dbConnTimes )
                            {
                                String tmpUrl1 = SystemConfig.instance().getSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_KEY_DOMAIN_URL, null );
                                if( tmpUrl1 == null )
                                {
                                    LogRun.append( "Connection Server Error" );
                                    mHandler.postDelayed( runnable, 100 );
                                }
                            }
                        }
                    }
                },
                new AyncUpdateListener()
                {
                    @Override
                    public void onProgressUpdate ( String response )
                    {}
                }, map, activity, url
        );
    }

    private static void doTranfer(final Activity activity)
    {
        mActivity.runOnUiThread( new Runnable()
        {
            @Override
            public void run ()
            {
                if( mLaunchLoading != null )
                    mLaunchLoading.setText( activity.getString( R.string.launch_check_link ) );
            }
        });

        new Thread( new Runnable()
        {
            @Override
            public void run ()
            {
                mStopHandler = true;
                LogRun.append( "==Ping START==" );
                ArrayList list = JSONModel.instance().getNewWebUrl();
                urlConnTimes = list.size();
                urlRealCount = 0;
                for ( Iterator i = JSONModel.instance().getNewWebUrl().iterator(); i.hasNext(); )
                {
                    final String cUrl = ( String ) i.next();
                    try
                    {
                        LogRun.append( "Ping: " + new URL( cUrl ).getHost() );
                    }
                    catch ( MalformedURLException e )
                    {
                        LogRun.append( "URL transfer host exception" );
                        urlConnTimes --;
//                        if ( !i.hasNext() )
//                        {
//                            mHandler.postDelayed( runnable, 8000 );
//                        }
                        continue;
                    }

                    CheckLinkModel.instance().doPing(
                            activity,
                            cUrl,
                            new CheckLinkModel.ResponseListener()
                            {
                                @Override
                                public void onTaskCompleted ( String domain, String response )
                                {
                                    LogRun.append( "domain: " + domain + "   cUrl: " + cUrl );
                                    LogRun.append( "response: " + response );
                                    urlRealCount++;

                                    String tmpUrl = SystemConfig.instance().getSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_KEY_WEB_URL, null );
                                    if ( response != null && StringUtils.containFooBar( response ) && tmpUrl == null )
                                    {
                                        SystemConfig.instance().putSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_KEY_WEB_URL, cUrl );

                                        mStopHandler = true;
                                        final Handler checkBuglyHandler = new Handler();
                                        checkBuglyHandler.postDelayed( new Runnable()
                                        {
                                            @Override
                                            public void run ()
                                            {
                                                if ( !AppConfig.isTbsX5Run() )
                                                {
                                                    mActivity.runOnUiThread( new Runnable()
                                                    {
                                                        @Override
                                                        public void run ()
                                                        {
                                                            String tmpUrl = SystemConfig.instance().getSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_KEY_WEB_URL, null );
                                                            LogRun.append( "finish" );
                                                            LogRun.append( "redirect: " + tmpUrl);

                                                            if( mPingFinishListener != null )
                                                                mPingFinishListener.callBeta();
                                                            if( mSpinKitView != null )
                                                                mSpinKitView.setVisibility( View.INVISIBLE );
                                                            if( mLaunchLoading != null )
                                                            {
                                                                mLaunchLoading.setVisibility( View.VISIBLE );
                                                                mLaunchLoading.setText( activity.getString( R.string.launch_finish ) );
                                                            }
                                                        }
                                                    });

                                                    Intent intent = null;
                                                    if( ViewConfig.getViewStyle() == 1 )
                                                    {
                                                        intent = new Intent( activity, X5ViewToolbarActivity.class );
                                                    }
                                                    else
                                                    {
                                                        intent = new Intent( activity, X5ViewActivity.class );
                                                    }
                                                    activity.startActivity( intent );
                                                }
                                                else
                                                {
//                                                    checkBuglyHandler.postDelayed( this, 1000 );
                                                }
                                            }
                                        }, 100 );
                                    }
                                    else
                                    {
                                        mStopHandler = false;
                                        if ( urlRealCount == urlConnTimes )
                                        {
                                            String tmpUrl1 = SystemConfig.instance().getSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_KEY_WEB_URL, null );
                                            if( tmpUrl1 == null ) mHandler.postDelayed( runnable, 8000 );
                                        }
                                    }
                                }
                            } );
                }
            }
        } ).start();
    }

    public static void checkTbsX5()
    {
        X5WebView webView = new X5WebView( mActivity );
        if( webView.getX5WebViewExtension() == null )
        {
            LogUtil.logInfo( LogUtil.TAG, "X5 Web View init Fail" );
            browserCore = "Android Core";
            AppConfig.setTbsX5Run( true );
        }
        else
        {
            LogUtil.logInfo( LogUtil.TAG, "X5 Web View init Success" );
            browserCore = "X5 Core";
            AppConfig.setTbsX5Run( false );
        }
        webView.destroy();
    }

    public static void releaseView ()
    {
        mLaunchLoading = null;
        mSpinKitView = null;
        ShowDialog.realse();
    }

    public static void release()
    {
        AppConfig.setTbsX5Run( false );
        if(mHandler != null)
        {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        runnable = null;
        urlRealCount = 0;
        urlConnTimes = 0;
        dbRealCount = 0;
        dbConnTimes = 0;
        mActivity = null;
    }

    private static boolean hasPermissions ( Context context, String... permissions )
    {
        if ( context != null && permissions != null )
        {
            for ( String permission : permissions )
            {
                if ( ActivityCompat.checkSelfPermission( context, permission ) != PackageManager.PERMISSION_GRANTED )
                {
                    return false;
                }
            }
        }
        return true;
    }

    private static String load ( Context context, String key )
    {
        String     value      = null;
        Properties properties = new Properties();
        try
        {
            properties.load( context.getAssets().open( "publish.properties" ) );
            value = properties.getProperty( key );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        System.out.println( "value: " + value );
        return value;
    }

    public interface PingFinishListener
    {
        void callBeta();
    }
}
