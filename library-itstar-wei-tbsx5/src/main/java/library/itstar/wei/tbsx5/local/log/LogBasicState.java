package library.itstar.wei.tbsx5.local.log;

/**
 * Created by Ching Wei on 2018/7/11.
 */

public class LogBasicState
{
    public String getDevice_id ()
    {
        return device_id;
    }

    public void setDevice_id ( String device_id )
    {
        this.device_id = device_id;
    }

    public String getDevice_os ()
    {
        return device_os;
    }

    public void setDevice_os ( String device_os )
    {
        this.device_os = device_os;
    }

    public String getDevice_branch ()
    {
        return device_branch;
    }

    public void setDevice_branch ( String device_branch )
    {
        this.device_branch = device_branch;
    }

    public String getDevice_name ()
    {
        return device_name;
    }

    public void setDevice_name ( String device_name )
    {
        this.device_name = device_name;
    }

    public String getApp_version ()
    {
        return app_version;
    }

    public void setApp_version ( String app_version )
    {
        this.app_version = app_version;
    }

    public String getDevice_version ()
    {
        return device_version;
    }

    public void setDevice_version ( String device_version )
    {
        this.device_version = device_version;
    }

    public String getSdk_version ()
    {
        return sdk_version;
    }

    public void setSdk_version ( String sdk_version )
    {
        this.sdk_version = sdk_version;
    }

    public String getAccount ()
    {
        return account;
    }

    public void setAccount ( String account )
    {
        this.account = account;
    }

    public LogBasicState getBasic ()
    {
        return basic;
    }

    public void setBasic ( LogBasicState basic )
    {
        this.basic = basic;
    }

    private LogBasicState basic          = null;
    private String device_id      = null;
    private String device_os      = null;
    private String device_branch  = null;
    private String device_name    = null;
    private String app_version    = null;
    private String device_version = null;
    private String sdk_version    = null;
    private String account        = null;

    public String getDevice_browser_core ()
    {
        return device_browser_core;
    }

    public void setDevice_browser_core ( String device_browser_core )
    {
        this.device_browser_core = device_browser_core;
    }

    private String device_browser_core = null;
}
