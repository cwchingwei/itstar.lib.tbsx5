package library.itstar.wei.tbsx5.local;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;

import library.itstar.wei.tbsx5.R;
import library.itstar.wei.tbsx5.def.SharedPreferencesKey;
import library.itstar.wei.tbsx5.state.JavascriptCommand;
import library.itstar.wei.tbsx5.view.act.X5ViewActivity;
import library.itstar.wei.tbsx5.view.act.X5ViewToolbarActivity;
import library.itstar.wei.tbsx5.view.x5.X5WebView;

import static library.itstar.wei.tbsx5.view.act.X5ViewActivity.KEY_BUNDLE_CAN_BACK;
import static library.itstar.wei.tbsx5.view.act.X5ViewActivity.KEY_BUNDLE_CAN_FINISH;
import static library.itstar.wei.tbsx5.view.act.X5ViewActivity.KEY_BUNDLE_WEB_URL;

/**
 * Created by Ching Wei on 2018/4/18.
 */

public class WebAppInterface
{
    private Context mContext;
    private X5WebView mWebView = null;
    public WebAppInterface ( Context aContext )
    {
        mContext = aContext;
    }

    public void addWebVIew ( X5WebView webView )
    {
        this.mWebView = webView;
    }
    //Android.glReady("logout")
    @JavascriptInterface
    public void postMessage ( String str )
    {
        Log.e( "WebAppInterface", "postMessage: " + str );
        try
        {
//            JavascriptCommand state = FastJsonUtils.getSingleBean( str, JavascriptCommand.class );
            JavascriptCommand state  = new JavascriptCommand();
            JSONObject        object = new JSONObject( str );

            if( object.has( "register" ) )
            {
                state.setRegister( object.getString( "register" ) );
            }
            if( object.has( "window" ) )
            {
                state.setWindow( object.getString( "window" ) );
            }
            if( object.has( "web_acc" ) )
            {
                state.setWeb_acc( object.getString( "web_acc" ) );
            }

            if( state.getWeb_acc() != null )
            {
                SystemConfig.instance().putSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_WEBVIEW_ACCOUNT_COOKIES, state.getWeb_acc() );
                ViewConfig.getWebAccListener().onWebAccChange( state.getWeb_acc() );
//                LogAsyncTask.instance().sendBasic( "X5 Core", state.getWeb_acc() );
//                String url = mWebView.getUrl();
//                String userAgent = mWebView.getSettings().getUserAgentString();
//                LogAsyncTask.instance().sendLog( url, state.getWeb_acc(), userAgent, 1, "" );
            }
            if( state.getRegister() != null )
            {
                Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( state.getRegister() ) );
                mContext.startActivity( intent );
            }
            if( state.getWindow() != null )
            {
                if( !URLUtil.isValidUrl( state.getWindow() ) )
                {
                    Toast.makeText( mContext, mContext.getString( R.string.dialog_system_error ), Toast.LENGTH_LONG ).show();
                    return;
                }
                Intent intent = null;
                if( ViewConfig.getViewStyle() == 1 )
                {
                    intent = new Intent( mContext, X5ViewToolbarActivity.class );
                }
                else
                {
                    intent = new Intent( mContext, X5ViewActivity.class );
                }
                Bundle extras = new Bundle();
                extras.putString( KEY_BUNDLE_WEB_URL, state.getWindow() );
                extras.putBoolean( KEY_BUNDLE_CAN_BACK, false );
                extras.putBoolean( KEY_BUNDLE_CAN_FINISH, true );
                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                intent.putExtras( extras );
                mContext.startActivity( intent );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }
    @JavascriptInterface
    public void logout ()
    {
        Log.e( "Web fun ", "logout" );
                ((Activity )mContext).runOnUiThread( new Runnable()
        {
            @Override
            public void run ()
            {
                //clear cookie
                CookieSyncManager.createInstance( mContext );
                CookieManager cookieManager = CookieManager.getInstance();

                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
                {
                    cookieManager.removeAllCookies( null );
                }
                else
                {
                    cookieManager.removeAllCookie();
                }
                //clear app local cache
//                deleteCache( mContext );

                //System Memory reswt
                SystemConfig.instance().reset();
                SystemConfig.instance().clearLoginCookie();
                SystemConfig.instance().resetWebURL();
                //Browser clear
                mWebView.clearCache(true);
                mWebView.clearHistory();
                //Android web view clear cookie
                clearCookies( mContext );
                //Android restart
                restartAPP ();
            }
        } );
    }

//    @org.xwalk.core.JavascriptInterface
//    public void loadURL ( final String str )
//    {
//        runOnUiThread( new Runnable()
//        {
//            @Override
//            public void run ()
//            {
//                XWalkCookieManager xm = new XWalkCookieManager();
//                xm.setAcceptCookie( true );
//                xm.setAcceptFileSchemeCookies( true );
//                LogUtil.logInfo( "loadURL", "Cookie " + SystemConfig.instance().getSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_WEBVIEW_LOGIN_COOKIES, "" ));
//                xm.setCookie( str, SystemConfig.instance().getSharedPreString( SharedPreferencesKey.SHARED_PRERENCES_WEBVIEW_LOGIN_COOKIES, "" ) );
//
//                mWebView.getSettings().setSupportMultipleWindows( true );
//                mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically( true );
//                mWebView.loadUrl( str, null );
//            }
//        } );
//    }

    private void restartAPP ()
    {
//        Intent intent = new Intent( mContext, MainThreadActivity.class );
//        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
//        mContext.startActivity( intent );

        android.os.Process.killProcess( android.os.Process.myPid() );
        System.exit( 0 );
    }

    private static void deleteCache(Context context) {
        try {
            File cache = context.getCacheDir();
            File appDir = new File(cache.getParent());
            deleteDir(appDir);
        } catch (Exception e) {}
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies ( Context context )
    {

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
//            Log.d("Web fun ", "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
//            Log.d("Web fun ", "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr =CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager =CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}