package library.itstar.wei.tbsx5.local;

/**
 * Created by Ching Wei on 2018/7/18.
 */

public class ViewConfig
{
    public static int getViewStyle ()
    {
        return viewStyle;
    }

    public static void setViewStyle ( int viewStyle )
    {
        ViewConfig.viewStyle = viewStyle;
    }

    private static int viewStyle = -1;

    public static WebAccListener getWebAccListener ()
    {
        return webAccListener;
    }

    public static void setWebAccListener ( WebAccListener webAccListener )
    {
        ViewConfig.webAccListener = webAccListener;
    }

    private static WebAccListener webAccListener = null;

    public interface WebAccListener
    {
        void onWebAccChange( String webAcc );
    }
}
