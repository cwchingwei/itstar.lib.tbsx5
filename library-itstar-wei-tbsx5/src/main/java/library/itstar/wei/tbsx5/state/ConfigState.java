package library.itstar.wei.tbsx5.state;

import java.io.Serializable;

/**
 * Created by PC-0320 on 2017/9/5.
 */

public class ConfigState implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String feedback = null;
    private String domain_url = null;
    private String appUpDate_url = null;

    public String getFeedback ()
    {
        return feedback;
    }

    public void setFeedback ( String feedback )
    {
        this.feedback = feedback;
    }

    public String getDomain_url ()
    {
        return domain_url;
    }

    public void setDomain_url ( String domain_url )
    {
        this.domain_url = domain_url;
    }

    public String getAppUpDate_url ()
    {
        return appUpDate_url;
    }

    public void setAppUpDate_url ( String appUpDate_url )
    {
        this.appUpDate_url = appUpDate_url;
    }
}
