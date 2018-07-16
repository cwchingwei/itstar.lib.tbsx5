package library.itstar.wei.tbsx5.local.log;

import android.widget.TextView;

import library.itstar.wei.tbsx5.utils.LogUtil;

/**
 * Created by Ching Wei on 2018/5/10.
 */

public class LogRun
{
    private static StringBuilder builder  = null;
    private static TextView      textView = null;

    public static void create()
    {
        builder = new StringBuilder();
    }

    public static void append( String str )
    {
        if ( builder == null )
        {
            create();
        }
        builder.append( str );
        builder.append( System.getProperty( "line.separator" ) );
        LogUtil.logInfo( LogUtil.TAG, str );

        if( textView != null )
        {
            textView.setText( print() );
        }
    }

    public static void addView( TextView textView )
    {
        LogRun.textView = textView;
    }

    public static String print()
    {
        if( builder == null )
        {
            create();
        }
        return builder.toString();
    }

    public static void clear()
    {
        builder = null;
    }
}
