/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kochar.common;

/**
 *
 * @author PBal
 */
public class KTSharedContext {
    
    public static final String DBCONN_DBIP = "localhost:5433";
    public static final String DBCONN_DBNAME = "postgres";
    public static final String DBCONN_DBUNAME = "postgres";
    public static final String DBCONN_DBUPASS = "postgres";

    
    private static KTSharedContext _instance = null;

    public static KTSharedContext Instance() {
        if (_instance == null) {
            _instance = new KTSharedContext();
        }
        return _instance;
    }

    public static void ReleaseInstance() {
        if (_instance != null) {
            _instance.Close();
            _instance = null;
        }
    }

    public KTSharedContext() {
        
    }
    
    public void Close()
    {
    }

    public String getOtaDbIp() {
        return DBCONN_DBIP;
    }

    public String getDbName() {
        return DBCONN_DBNAME;
    }
    
    public String getDbUserName() {
        return DBCONN_DBUNAME;
    }    
    
    public String getDbPassword() {
        return DBCONN_DBUPASS;
    }

}
