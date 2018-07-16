package library.itstar.wei.tbsx5.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by C.W. Yang on 2017/1/25.
 */
public class ScreenUtil
{
    private static String TAG = ScreenUtil.class.getSimpleName();

    public static void initializeScreenSize( Context context )
    {
        DisplayMetrics metrics      = context.getResources().getDisplayMetrics();
        float          screenHeight = Math.min( metrics.heightPixels, metrics.widthPixels );
        float          screenWidth  = Math.max( metrics.widthPixels, metrics.heightPixels );
        Log.i( TAG, "initializeScreenSize---屏幕宽度：" + metrics.widthPixels + "px 屏幕高度："
                + metrics.heightPixels + "px" );
        Log.i( TAG, "initializeScreenSize---屏幕密度：" + metrics.density );
        Log.i( TAG, "initializeScreenSize---屏幕DPI：" + metrics.densityDpi );
        Log.i( TAG, "initializeScreenSize----screenWidth=" + screenWidth + " --screenHeight=" + screenHeight );
    }

    public static DisplayMetrics getDisplayMetrics ( Context context )
    {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm;
    }

    public static float dp2px ( Context context, float dpValue )
    {
        return Math.round( dpValue * getDisplayMetrics( context ).density );
    }

    public static float px2dp ( Context context, float pxValue )
    {
        return Math.round( pxValue / getDisplayMetrics( context ).density );
    }

    public static float sp2px ( Context context, float pxValue )
    {
        return Math.round( pxValue * getDisplayMetrics( context ).scaledDensity );
    }

    public static float px2sp ( Context context, float pxValue )
    {
        return Math.round( pxValue / getDisplayMetrics( context ).scaledDensity );
    }

    public static int getScreenWidthPx ( Context context )
    {
        return getDisplayMetrics( context ).widthPixels;
    }

    public static int getScreenHeightPx ( Context context )
    {
        return getDisplayMetrics( context ).heightPixels;
    }
}