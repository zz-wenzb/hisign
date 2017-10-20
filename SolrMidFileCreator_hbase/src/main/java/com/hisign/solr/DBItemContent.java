package com.hisign.solr;

public class DBItemContent
{
    private String recordID;
    private String mountPath;
    private String dataIP;
    private String md5;
    private String userName;
    private String updateTime;
    
    
    public DBItemContent()
    {
        super();
    }
    
    public DBItemContent(String recordID, String mountPath, String dataIP, String md5, String userName,String updateTime)
    {
        super();
        this.recordID = recordID;
        this.mountPath = mountPath;
        this.dataIP = dataIP;
        this.md5 = md5;
        this.userName = userName;
        this.updateTime = updateTime;
    }
    public String getRecordID()
    {
        return recordID;
    }
    public void setRecordID(String recordID)
    {
        this.recordID = recordID;
    }
    public String getMountPath()
    {
        return mountPath;
    }
    public void setMountPath(String mountPath)
    {
        this.mountPath = mountPath;
    }
    public String getDataIP()
    {
        return dataIP;
    }
    public void setDataIP(String dataIP)
    {
        this.dataIP = dataIP;
    }
    public String getMd5()
    {
        return md5;
    }
    public void setMd5(String md5)
    {
        this.md5 = md5;
    }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
    
    
}
