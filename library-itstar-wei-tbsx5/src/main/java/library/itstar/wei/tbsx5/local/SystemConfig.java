package library.itstar.wei.tbsx5.local;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import library.itstar.wei.tbsx5.def.SharedPreferencesKey;

/**
 * Created by Ching Wei on 2017/11/3.
 */

public class SystemConfig
{
    public static void construct ( Context aContext )
    {
        if ( _instance == null )
        {
            _instance = new SystemConfig();
        }
        if ( _context == null )
        {
            new SystemConfig( aContext );
        }
    }

    public static void setNowActivity ( Activity nowActivity )
    {
        activity = nowActivity;
    }
    public static Activity getNowActivity()
    {
        return activity;
    }
    public static SystemConfig instance()
    {
        if( _instance == null )
        {
            return new SystemConfig();
        }
        createSharedPref();
        return _instance;
    }
    protected SystemConfig( Context aContext )
    {
        _context = aContext;
    }
    protected SystemConfig()
    {
    }

    private static void createSharedPref()
    {
        if( _context != null )
        _shared_pref = _context.getSharedPreferences( SharedPreferencesKey.SHARED_PRERENCES_KEY, Context.MODE_PRIVATE );
    }

    public void putSharedPreString( String aObject, String aString )
    {
        SharedPreferences.Editor editor = _shared_pref.edit();
        editor.putString( aObject, aString );
        editor.commit();
    }

    public String getSharedPreString( String aObject, String aDefault )
    {
        if( _shared_pref  == null)
        {
            createSharedPref();
        }
        return  _shared_pref.getString( aObject, aDefault );
    }
    public void putSharedPreFloat( String aObject, float aFloat )
    {
        SharedPreferences.Editor editor = _shared_pref.edit();
        editor.putFloat( aObject, aFloat );
        editor.commit();
    }

    public Float getSharedPreFloat( String aObject, float aDefault )
    {
        if( _shared_pref  == null)
        {
            createSharedPref();
        }
        return  _shared_pref.getFloat( aObject, aDefault );
    }

    public void putSharedPreBoolean( String aObject, boolean aBoolean )
    {
        SharedPreferences.Editor editor = _shared_pref.edit();
        editor.putBoolean( aObject, aBoolean );
        editor.commit();
    }

    public Boolean getSharedPreBoolean( String aObject, boolean aDefault )
    {
        if( _shared_pref  == null)
        {
            createSharedPref();
        }
        return  _shared_pref.getBoolean( aObject, false );
    }

    public void reset()
    {
        SharedPreferences.Editor editor = _shared_pref.edit();
        editor.clear();
        editor.commit();
    }

    public void clearLoginCookie()
    {
        removeKey( SharedPreferencesKey.SHARED_PRERENCES_WEBVIEW_LOGIN_COOKIES );
        removeKey( SharedPreferencesKey.SHARED_PRERENCES_WEBVIEW_ACCOUNT_COOKIES );
    }

    public void resetWebURL()
    {
        removeKey( SharedPreferencesKey.SHARED_PRERENCES_KEY_WEB_URL );
        removeKey( SharedPreferencesKey.SHARED_PRERENCES_KEY_DOMAIN_URL );
        removeKey( SharedPreferencesKey.SHARED_PRERENCES_KEY_PING_INIT );
        removeKey( SharedPreferencesKey.SHARED_PRERENCES_KEY_URL_TIMES );
    }

    private void removeKey( String aKey )
    {
        SharedPreferences.Editor editor = _shared_pref.edit();
        editor.remove( aKey );
        editor.apply();
    }

    private static Context           _context     = null;
    private static SystemConfig      _instance    = null;
    private static SharedPreferences _shared_pref = null;
    private static Activity activity;
}
