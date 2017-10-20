package com.hisign.solr;


/**
 * this class contain all the options used for data process
 * 
 * @author Administrator
 *
 */
public class ModuleOptions
{
    /**
     * connection string to link mysql
     */
    private static String connectString;
    /**
     * which IP in mysql(server_ip) should I process
     */
    private static String processorIP;
    /**
     * where to save the middle file
     */
    private static String middleFileRoot; 
    /**
     * where to save the Sense file
     */
    private static String senseRoot;

    /**
     * every 1000 file save in the same folder with groupName
     */
    private static String groupName;
    
    private static int successCount = 0;
    private static int failedCount = 0;
    
    public static int synFileSense[] = {0};
    public static int synFileCache[] = {0};
    public static int synDatabse[] = {0};

    public static String getConnectString()
    {
        return connectString;
    }

    public static void setConnectString(String connString)
    {
        connectString = connString;
    }

    /*
     * extract file extension part from path
     */
    public static String getExtensionName(String filename)
    {
        if ((filename != null) && (filename.length() > 0))
        {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1)))
            {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    public static String getProcessorIP()
    {
        return processorIP;
    }

    public static void setProcessorIP(String processorIP)
    {
        ModuleOptions.processorIP = processorIP;
    }
    
    public static void setGroupName(String groupName)
    {
        ModuleOptions.groupName = groupName;
    }

    public static String getMiddleFileRoot()
    {
    	if(middleFileRoot.endsWith("/"))
        {
            return middleFileRoot ;
        }
        return middleFileRoot + "/";
//        if(middleFileRoot.endsWith("/"))
//        {
//            return middleFileRoot + groupName + "/";
//        }
//        return middleFileRoot + "/" + groupName + "/";
    }

    public static void setMiddleFileRoot(String middleFileRoot)
    {
        ModuleOptions.middleFileRoot = middleFileRoot;
    }

    public static String getSenseRoot()
    {
        return senseRoot;
    }

    public static void setSenseRoot(String senseRoot)
    {
        ModuleOptions.senseRoot = senseRoot;
    }

    public static void increaseFailed()
    {
        ++ModuleOptions.failedCount;
    }

    public static int getSuccessCount()
    {
        return successCount;
    }

    public static int getFailedCount()
    {
        return failedCount;
    }

    public static void increaseSuccess()
    {
        ++ModuleOptions.successCount;        
    }
}
