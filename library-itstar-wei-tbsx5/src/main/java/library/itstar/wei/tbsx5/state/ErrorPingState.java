package library.itstar.wei.tbsx5.state;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ching Wei on 2018/2/21.
 */

public class ErrorPingState implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static final String ACTIVITY_EXTRA_KEY = "ACTIVITY_KEY_PING_STATE";

    private String              fastDomain   = null;
    private int                 feedback     = -1;
    private float                 urlTime     = 0.0f;
    private String              errorMessage = null;
    private boolean             isPing       = false;
    private ArrayList< String > webUrls      = new ArrayList<>();
    private ArrayList< Float > webUrlsTime      = new ArrayList<>();

    public String getFastDomain ()
    {
        return fastDomain;
    }

    public void setFastDomain ( String fastDomain )
    {
        this.fastDomain = fastDomain;
    }

    public int getFeedback ()
    {
        return feedback;
    }

    public void setFeedback ( int feedback )
    {
        this.feedback = feedback;
    }

    public String getErrorMessage ()
    {
        return errorMessage;
    }

    public void setErrorMessage ( String errorMessage )
    {
        this.errorMessage = errorMessage;
    }

    public boolean isPing ()
    {
        return isPing;
    }

    public void setPing ( boolean ping )
    {
        isPing = ping;
    }

    public synchronized ArrayList< String > getWebUrls ()
    {
        return webUrls;
    }

    public void setWebUrls ( ArrayList< String > webUrls )
    {
        this.webUrls = webUrls;
    }

    public synchronized ArrayList< Float > getWebUrlsTime ()
    {
        return webUrlsTime;
    }

    public void setWebUrlsTime ( ArrayList< Float > webUrlsTime )
    {
        this.webUrlsTime = webUrlsTime;
    }

    public float getUrlTime ()
    {
        return urlTime;
    }

    public void setUrlTime ( float urlTime )
    {
        this.urlTime = urlTime;
    }
}
