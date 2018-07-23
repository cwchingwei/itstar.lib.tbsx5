package library.itstar.wei.tbsx5.local.log;

import java.io.Serializable;

/**
 * Created by Ching Wei on 2018/7/11.
 */

public class LogDatasState implements Serializable
{
    public String getSess_id ()
    {
        return sess_id;
    }

    public void setSess_id ( String sess_id )
    {
        this.sess_id = sess_id;
    }

    public String getAppapp ()
    {
        return appapp;
    }

    public void setAppapp ( String appapp )
    {
        this.appapp = appapp;
    }

    public String getAccount ()
    {
        return account;
    }

    public void setAccount ( String account )
    {
        this.account = account;
    }

    public String getRequest_url ()
    {
        return request_url;
    }

    public void setRequest_url ( String request_url )
    {
        this.request_url = request_url;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus ( String status )
    {
        this.status = status;
    }

    public String getPhone_start_time ()
    {
        return phone_start_time;
    }

    public void setPhone_start_time ( String phone_start_time )
    {
        this.phone_start_time = phone_start_time;
    }

    public String getPhone_end_time ()
    {
        return phone_end_time;
    }

    public void setPhone_end_time ( String phone_end_time )
    {
        this.phone_end_time = phone_end_time;
    }

    public String getResponse ()
    {
        return response;
    }

    public void setResponse ( String response )
    {
        this.response = response;
    }

    public String getDevice_id ()
    {
        return device_id;
    }

    public void setDevice_id ( String device_id )
    {
        this.device_id = device_id;
    }

    private LogDatasState datas = null;
    private LogDatasState useful = null;

    public LogDatasState getDatas ()
    {
        return datas;
    }

    public void setDatas ( LogDatasState datas )
    {
        this.datas = datas;
    }

    public LogDatasState getUseful ()
    {
        return useful;
    }

    public void setUseful ( LogDatasState useful )
    {
        this.useful = useful;
    }

    public String getUesr_agent ()
    {
        return uesr_agent;
    }

    public void setUesr_agent ( String uesr_agent )
    {
        this.uesr_agent = uesr_agent;
    }

    private String sess_id          = null;
    private String appapp           = null;
    private String account          = null;
    private String request_url      = null;
    private String uesr_agent       = null;
    private String response         = null;
    private String device_id        = null;
    private String status           = null;
    private String phone_start_time = null;
    private String phone_end_time   = null;
}
