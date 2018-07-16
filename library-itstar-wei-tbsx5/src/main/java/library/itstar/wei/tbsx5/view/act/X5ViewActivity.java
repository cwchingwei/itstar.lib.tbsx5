package library.itstar.wei.tbsx5.view.act;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import library.itstar.wei.tbsx5.R;
import library.itstar.wei.tbsx5.def.SharedPreferencesKey;
import library.itstar.wei.tbsx5.local.AppConfig;
import library.itstar.wei.tbsx5.local.SystemConfig;
import library.itstar.wei.tbsx5.local.WebAppInterface;
import library.itstar.wei.tbsx5.local.log.LogRun;
import library.itstar.wei.tbsx5.model.CheckLinkModel;
import library.itstar.wei.tbsx5.model.CheckWebURL;
import library.itstar.wei.tbsx5.model.IPAsyncTask;
import library.itstar.wei.tbsx5.model.JSONModel;
import library.itstar.wei.tbsx5.model.ShowDialog;
import library.itstar.wei.tbsx5.utils.LogUtil;
import library.itstar.wei.tbsx5.utils.ScreenUtil;
import library.itstar.wei.tbsx5.utils.StringUtils;
import library.itstar.wei.tbsx5.view.x5.X5WebView;

/**
 * Created by Ching Wei on 2018/7/13.
 */

public class X5ViewActivity extends AppCompatActivity
{
    private       X5WebView            webView                         = null;
    private       ProgressBar          mProgressBar                    = null;
    private       ImageView            mClose                          = null;
    private       RelativeLayout       mViewLunch                      = null;
    private       TextView             mTextLunch                      = null;
    private       boolean              isFirst                         = true;
    private       String               _url                            = null;
    private       Thread               _thread_timeout                 = null;
    private       boolean              timeout                         = true;
    private       boolean              webViewReady                    = false;
    private       ArrayList< String >  historyOverrideURL              = null;
    private       String               TAG                             = X5ViewActivity.class.getName();
    public static String               KEY_BUNDLE_WEB_URL              = "key_bundle_web_url";
    public static String               KEY_BUNDLE_CAN_BACK             = "key_bundle_can_back";
    public static String               KEY_BUNDLE_CAN_FINISH           = "key_bundle_can_finish";
    public static String               KEY_BUNDLE_HISTORY_URL          = "key_bundle_history_url";
    public static String               KEY_BUNDLE_SUPPORT_MUTI_WINDOWS = "key_bundle_support_muti_windows";
    private       boolean              WEB_CAN_BACK                    = false;
    private       boolean              WEB_CAN_FINISH                  = false;
    private       boolean              isCloseEnable                   = false;
    private       ValueCallback< Uri > uploadMessage                   = null;

    private float startX = 0;
    private float startY = 0;

    int mMotionDownX, mMotionDownY;

    private static boolean            mStopHandler = false;
    private static android.os.Handler mHandler     = new android.os.Handler();
    private static int                mCount       = 0;

    private Runnable runnable = new Runnable()
    {
        @Override
        public void run ()
        {
            if ( !mStopHandler )
            {
                if( ++mCount > 3 )
                {
                    X5ViewActivity.this.runOnUiThread( new Runnable()
                    {
                        @Override
                        public void run ()
                        {
                            if(  !ShowDialog.instance().isShowing() )
                            {
                                ShowDialog.instance().showMessageErrorDialog( X5ViewActivity.this, X5ViewActivity.this.getString( R.string.dialog_system_error ), X5ViewActivity.this.getString( R.string.dialog_title_error ) );
                            }
                        }
                    } );
                }
                else
                {
                    doTranfer( X5ViewActivity.this );
                }
            }
        }
    };

    @Override
    protected void onCreate ( @Nullable Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        getWindow().setFormat( PixelFormat.TRANSLUCENT);
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
        }

        LogRun.clear();
        setContentView( R.layout.app_web_x5 );
        SystemConfig.setNowActivity( X5ViewActivity.this );

        webView = ( X5WebView ) findViewById( R.id.web_view );
        mProgressBar = ( ProgressBar ) findViewById( R.id.progress_bar );
        mClose = ( ImageView ) findViewById( R.id.web_close );
        mTextLunch = ( TextView ) findViewById( R.id.txt_view_lunch );
        mViewLunch = ( RelativeLayout ) findViewById( R.id.img_view_lunch );

        initLunchView();

        initListener();
        initData();

        Log.i("QbSdk", " getX5WebViewExtension is " + webView.getX5WebViewExtension());
        if( webView.getX5WebViewExtension() != null )
        {
            webView.loadUrl( _url, null );
        }
        else
        {
            final Handler checkBuglyHandler = new Handler();
            checkBuglyHandler.postDelayed( new Runnable()
            {
                @Override
                public void run ()
                {
                    if ( webView.getX5WebViewExtension() == null )
                    {
                        SystemConfig.getNowActivity().runOnUiThread( new Runnable()
                        {
                            @Override
                            public void run ()
                            {
                                mTextLunch.setText( getString( R.string.launch_browser_x5_text ) );
                            }
                        });
                        checkBuglyHandler.postDelayed( this, 1000 );
                    }
                }
            }, 100 );
        }
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data )
    {
        super.onActivityResult( requestCode, resultCode, data );

        if ( requestCode == 100 )
        {
            if ( resultCode != RESULT_OK )
            {
                //一定要返回null,否则<input file> 就是没有反应
                if ( uploadMessage != null )
                {
                    uploadMessage.onReceiveValue( null );
                    uploadMessage = null;
                }
            }
            //选取照片时
            if ( resultCode == RESULT_OK )
            {
                Uri imageUri = null;

                switch ( requestCode )
                {
                    case 100:

                        if ( data != null )
                        {
                            imageUri = data.getData();
                        }

                        break;
                }
                //上传文件
                if ( uploadMessage != null )
                {
                    uploadMessage.onReceiveValue( imageUri );
                    uploadMessage = null;
                }
            }
        }
    }

    public void initLunchView ()
    {
        if( AppConfig.getViewLunch() != -1 )
        {
            mViewLunch.setBackgroundResource( AppConfig.getViewLunch() );
            mViewLunch.setVisibility( View.VISIBLE );
            webView.setVisibility( View.GONE );
        }

        mTextLunch.setOnLongClickListener( new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick ( View view )
            {
                LogRun.addView( mTextLunch );
                ((TextView)view).setText( LogRun.print() );
                return false;
            }
        });
    }

    public boolean onKeyDown ( int keyCode, KeyEvent event )
    {
        if ( event.getAction() == KeyEvent.ACTION_DOWN )
        {
            switch ( keyCode )
            {
                case KeyEvent.KEYCODE_BACK:
                    if ( WEB_CAN_FINISH && !webView.canGoBack() )
                    {
                        finish();
                    }
            }
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//            LogUtil.logError( "XWalkView", "dispatchKeyEvent KEYCODE_BACK" );
            if( WEB_CAN_BACK )
            {
                return super.dispatchKeyEvent(event);
            }
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onDestroy ()
    {
        LogUtil.logError( "XWalkView", "onDestroy: " + _url );
        super.onDestroy();
        webViewReady = true;
        if ( webView != null )
        {
            webView.destroy();
        }
    }

    private void initData()
    {
        _url = SystemConfig.instance().getSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_KEY_WEB_URL, null );
        LogRun.append( "Now URL: " + _url );
//        _url = "https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/file";
        historyOverrideURL = new ArrayList<>();
        if( getIntent().hasExtra( KEY_BUNDLE_WEB_URL ) )
        {
            _url = getIntent().getStringExtra( KEY_BUNDLE_WEB_URL );

            mClose.setVisibility( View.VISIBLE );
            mViewLunch.setVisibility( View.GONE );

            WEB_CAN_BACK = true;
            WEB_CAN_FINISH = true;
            setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_FULL_USER );
        }
        else
        {
            setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        }

        if( getIntent().hasExtra( KEY_BUNDLE_SUPPORT_MUTI_WINDOWS ) )
        {
//            webView.getSettings().setSupportMultipleWindows( false );
//            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically( false );
        }
    }

    private void initListener ()
    {
        mClose.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick ( View view )
            {

            }
        } );

        mClose.setOnTouchListener( new View.OnTouchListener()
        {
            @Override
            public boolean onTouch ( View view, MotionEvent motionEvent )
            {
                final int X = (int) motionEvent.getRawX();
                final int Y = (int) motionEvent.getRawY();
                float endX = motionEvent.getX();
                float endY = motionEvent.getY();
                switch ( motionEvent.getAction() & MotionEvent.ACTION_MASK )
                {
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = ( RelativeLayout.LayoutParams ) view.getLayoutParams();
                        mMotionDownX = X - lParams.leftMargin;
                        mMotionDownY = Y - lParams.topMargin;

                        startX = motionEvent.getX();
                        startY = motionEvent.getY();

                        isCloseEnable = true;
//                        Log.i( "layout_floating", "ACTION_DOWN: ");
                        break;
                    case MotionEvent.ACTION_UP:
//                        Log.i( "layout_floating", "ACTION_UP: " + isCloseEnable);
                        if( isAClick( startX, endX, startY, endY ) )
                        {
//                            Log.i( "layout_floating", "ACTION_CLICK: " + isCloseEnable);
                            if( isCloseEnable )
                            {
                                finish();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:

                        if( !isAClick( startX, endX, startY, endY ) )
                        {
//                            Log.i( "layout_floating", "ACTION_MOVE: ");
                            isCloseEnable = false;
                        }

                        RelativeLayout.LayoutParams layoutParams = ( RelativeLayout.LayoutParams ) view.getLayoutParams();

                        if( ( X - mMotionDownX < 0) || ( X - mMotionDownX > ScreenUtil.getScreenWidthPx( X5ViewActivity.this )) )
                        {
                            return true;
                        }
                        else
                        {
                            layoutParams.leftMargin = X - mMotionDownX;
                        }
                        if( ( Y - mMotionDownY < 0) || ( Y - mMotionDownY > ScreenUtil.getScreenHeightPx( X5ViewActivity.this )))
                        {
                            return true;
                        }
                        else
                        {
                            layoutParams.topMargin = Y - mMotionDownY;
                        }

                        view.setLayoutParams( layoutParams );
                        break;
                }
                return false;
            }
        } );

        webView.setWebChromeClient( new WebChromeClient()
        {
            @Override
            public boolean onCreateWindow ( WebView webView, boolean b, boolean b1, Message message )
            {
                LogUtil.logInfo( LogUtil.TAG, "onCreateWindow: " );
                final WebView newWebView = new WebView( webView.getContext() );
                newWebView.setLayoutParams( new RelativeLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ) );
                newWebView.setWebViewClient( new WebViewClient()
                {
                    @Override
                    public void onPageStarted ( WebView webView, String url, Bitmap bitmap )
                    {
                        historyOverrideURL.add( url );
                        Intent intent = null;
                        intent = new Intent( webView.getContext(), X5ViewActivity.class );
                        Bundle extras = new Bundle();
                        extras.putString( KEY_BUNDLE_WEB_URL, url );
                        extras.putBoolean( KEY_BUNDLE_CAN_BACK, false );
                        extras.putBoolean( KEY_BUNDLE_CAN_FINISH, true );
                        if( CheckWebURL.instance().isSuppotMutiWindows( historyOverrideURL.get( 0 ) ) )
                        {
                            Log.i( "CheckWebURL", "KEY_BUNDLE_SUPPORT_MUTI_WINDOWS: ");
                            extras.putBoolean( KEY_BUNDLE_SUPPORT_MUTI_WINDOWS, true );
                        }
                        Log.i( "CheckWebURL", "KEY_BUNDLE_SUPPORT_MUTI_WINDOWS NONONO: ");
                        intent.putExtras( extras );
//                        if( CheckWebURL.instance().isCloseButton( historyOverrideURL.get( 0 ) ) )
                        {
                            intent.putStringArrayListExtra( KEY_BUNDLE_HISTORY_URL, historyOverrideURL );
                        }
                        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity( intent );
                        newWebView.destroy();
                    }
                });
                WebView.WebViewTransport transport = ( WebView.WebViewTransport ) message.obj;
                transport.setWebView( newWebView );
                message.sendToTarget();
                return true;
            }

            @Override
            public void onProgressChanged ( final WebView webView, int progressInPercent )
            {
                mProgressBar.setProgress( progressInPercent );
                mTextLunch.setText( getString( R.string.launch_loading_for_text ) + String.valueOf( progressInPercent ) + " %"  );
                if ( progressInPercent == 100 )
                {
                    mProgressBar.setVisibility( View.GONE );
                    _thread_timeout.interrupt();
                    timeout = false;
                    isFirst = false;
                    new Handler().postDelayed( new Runnable()
                    {
                        @Override
                        public void run ()
                        {
                            mViewLunch.setVisibility( View.GONE );
                            webView.setVisibility( View.VISIBLE );
                        }
                    }, 800 );
                }
                else
                {
                    mProgressBar.setVisibility( View.VISIBLE );
                }
            }

            @Override
            public void openFileChooser ( com.tencent.smtt.sdk.ValueCallback< Uri > filePathCallback, String s, String s1 )
            {
                LogRun.append( "open File Chooser" );
                uploadMessage = filePathCallback;
                Intent i = new Intent( Intent.ACTION_GET_CONTENT );
                i.addCategory( Intent.CATEGORY_OPENABLE );
                i.setType( "image/*" );
                startActivityForResult( Intent.createChooser( i, "Image Chooser" ), 100 );
            }
        });

        webView.setWebViewClient( new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading ( WebView webView, String url )
            {
                try
                {
                    if ( !url.startsWith( "https://" ) && !url.startsWith( "http://" ) )
                    {
                        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
                        startActivity( intent );
                        finish();
                        return true;
                    }
                }
                catch (Exception e)
                { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return false;
                }
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError ( WebView webView, SslErrorHandler sslErrorHandler, com.tencent.smtt.export.external.interfaces.SslError sslError )
            {
                LogRun.append( "Received SSL Error" );
                sslErrorHandler.proceed();
            }

            @Override
            public void onPageStarted ( final WebView webView, String s, Bitmap bitmap )
            {
                if( isFirst )
                {
                    timeout = true;
                    _thread_timeout = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{Thread.sleep( 15000 );} catch (InterruptedException e) {}
                            if(timeout) {
//                                doGoPing( X5ViewActivity.this, webView, getString( R.string.error_msg_client_timeout ) );
                                X5ViewActivity.this.runOnUiThread( new Runnable()
                                {
                                    @Override
                                    public void run ()
                                    {
                                        Toast.makeText( X5ViewActivity.this, X5ViewActivity.this.getString( R.string.dialog_please_check_connect_lose ), Toast.LENGTH_SHORT ).show();
                                    }
                                } );
                            }
                        }
                    });
                    _thread_timeout.start();
                }
            }

            @Override
            public void onPageFinished ( WebView webView, String s )
            {
                String allCookies = null;
                CookieManager cookieManager = CookieManager.getInstance();
                try
                {
                    if( _url.indexOf( "https://" ) != -1 )
                    {
                        allCookies = cookieManager.getCookie( "https://" + new URL(_url).getHost() );
                    }
                    else
                    {
                        allCookies = cookieManager.getCookie( "http://" + new URL(_url).getHost() );
                    }
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
                if( allCookies != null )
                    cookieStore( allCookies );
                LogUtil.logError( "onPageLoadStopped", "cookie: " + allCookies );
            }
        });

        WebAppInterface webAppInterface = new WebAppInterface(this);
        webAppInterface.addWebVIew( webView );
        webView.addJavascriptInterface( webAppInterface, "Android" );
    }

    private static String getWebviewVersionInfo ( X5WebView aWebView )
    {
        // Overridden UA string
        String alreadySetUA = aWebView.getSettings().getUserAgentString();

        // Next call to getUserAgentString() will get us the default
        aWebView.getSettings().setUserAgentString( null );

        // Devise a method for parsing the UA string
        String webViewVersion = ( aWebView.getSettings().getUserAgentString() );

        // Revert to overriden UA string
        aWebView.getSettings().setUserAgentString( alreadySetUA );

        return webViewVersion;
    }

    private boolean isAClick ( float startX, float endX, float startY, float endY )
    {
        float differenceX = Math.abs( startX - endX );
        float differenceY = Math.abs( startY - endY );
        return !( differenceX > 7/* =5 */ || differenceY > 7 );
    }

    private void cookieStore( String aCookie )
    {
        try
        {
            String[] a = aCookie.split( ";" );
            for( int i = 0; i < a.length; i++ )
            {
                String[] b = a[i].trim().split( "=" );
                if( b[0].trim().equalsIgnoreCase( "Language" ) )
                {
                    SystemConfig.instance().putSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_WEBVIEW_LANGUAGE_COOKIES, b[1] );
                }
                if( b[0].trim().equalsIgnoreCase( "web_acc" ) )
                {
                    SystemConfig.instance().putSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_WEBVIEW_ACCOUNT_COOKIES, b[1] );
                }
            }
        }
        catch ( Exception e )
        {
//            LogUtil.logException( TAG, e );
        }
    }

    private void doTranfer(final Activity activity)
    {
        new Thread( new Runnable()
        {
            @Override
            public void run ()
            {
                LogRun.append( "==Ping START==" );
                for ( Iterator i = JSONModel.instance().getNewWebUrl().iterator(); i.hasNext(); )
                {
                    final String cUrl = ( String ) i.next();

                    try
                    {
                        LogRun.append( "ping: " + new URL( cUrl ).getHost() );
                    }
                    catch ( MalformedURLException e )
                    {
                        LogRun.append( "URL transfer host exception" );
                        if ( !i.hasNext() )
                        {
                            mHandler.postDelayed( runnable, 10000 );
                        }
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
                                    String tmpUrl = SystemConfig.instance().getSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_KEY_WEB_URL, null );
                                    if ( response != null && StringUtils.containFooBar( response ) && tmpUrl == null )
                                    {
                                        SystemConfig.instance().putSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_KEY_WEB_URL, cUrl );
                                        mStopHandler = true;
                                    }
                                }
                            }
                    );

                    if ( !i.hasNext() )
                    {
                        mHandler.postDelayed( runnable, 10000 );
                    }
                }
            }
        } ).start();
    }

    private void doGoPing ( final Activity aActivity, final WebView aWebView, final String aErrorMessage )
    {
        if ( WEB_CAN_FINISH )
        {
            return;
        }
        LogRun.append( aErrorMessage );
        IPAsyncTask.instance().doQueryCurrentIP();
        SystemConfig.instance().resetWebURL();
        mHandler.postDelayed( runnable, 100 );
    }
}
