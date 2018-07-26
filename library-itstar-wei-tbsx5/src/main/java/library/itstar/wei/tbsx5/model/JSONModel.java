package library.itstar.wei.tbsx5.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import library.itstar.wei.tbsx5.utils.StringUtils;


/**
 * Created by C.W. Yang on 2016/10/28.
 */
public class JSONModel
{
    public static JSONModel instance ()
    {

        if ( _instance == null )
        {
            _instance = new JSONModel();
        }

        return _instance;
    }

    public void setJSONObject ( String aJSONString )
    {
        try
        {
            _JSONObject = new JSONObject( aJSONString );
        }
        catch ( JSONException e )
        {
            e.printStackTrace();
        }
    }

    public boolean isJSONValid ( String test )
    {
        try
        {
            new JSONObject( test );
        }
        catch ( JSONException ex )
        {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try
            {
                new JSONArray( test );
            }
            catch ( JSONException ex1 )
            {
                return false;
            }
        }
        return true;
    }

    public boolean isJSONFooBar ( String test )
    {
        try
        {
            StringUtils.containFooBar( new JSONObject( test ).getString( "check_link" ) );
        }
        catch ( JSONException ex )
        {

        }
        return true;
    }

    public JSONObject getJSONObject ()
    {
        return _JSONObject;
    }

    public Boolean getFeebackUpdate ()
    {
        try
        {
            if ( _JSONObject.getInt( "feedback" ) == 1 )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch ( JSONException e )
        {
            e.printStackTrace();
            return false;
        }
    }

    public String getWebUrl ()
    {
        try
        {
            return _JSONObject.getString( "web_url" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return "";
        }
    }

    public ArrayList< String > getNewWebUrl ()
    {
        try
        {
            return new ArrayList<>( Arrays.asList( _JSONObject.getString( "web_url" ).split( "," ) ) );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList< String > getDomainUrl ( boolean isDev )
    {
        try
        {
            return new ArrayList<>( Arrays.asList( _JSONObject.getString( "domain_url" ).split( "," ) ) );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return null;
        }
    }

    public String getAppUpDateUrl ()
    {
        try
        {
            return _JSONObject.getString( "appUpDate_url" );
//            return "http://www.6600mmm.com/mobile";
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return "";
        }
    }

    public void release ()
    {
        _instance = null;
        _JSONObject = null;
        _JSONArray = null;
        _array_list = null;
    }

    private static JSONModel           _instance   = null;
    private        JSONArray           _JSONArray  = null;
    private static JSONObject          _JSONObject = null;
    private        ArrayList< String > _array_list = null;
}
