/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kochar.tests;

import com.kochar.common.LogItem;
import com.kochar.common.OTADbConnect;
import com.kochar.common.ServiceMessage;
import com.kochar.common.Throttle;
import com.kochar.services.KSimpleLogger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author PBal
 */
public class TestOTADBConnect {
    
    public TestOTADBConnect() {
    }
    
    @BeforeAll
    public static void setUpClass() {
        KSimpleLogger.Instance().SetSpeed(Throttle.RunSpeed.Fastest);
        KSimpleLogger.Instance().Start();
        KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.INFO, TestOTADBConnect.class.getName(), "setUpClass()", "KSimpleLogger Initialized", null)));
    }
    
    @AfterAll
    public static void tearDownClass() {
        KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.INFO, TestOTADBConnect.class.getName(), "tearDownClass()", "KSimpleLogger DeInitialized", null)));
        KSimpleLogger.Instance().Stop();
        KSimpleLogger.releaseInstance();
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void TestOTADBConnectInit() {

        String query = "select version()";
            
        try {
            
            Connection connDB = OTADbConnect.getInstance(OTADbConnect.DBCONN_DBSTATUSWRITER).getConnection();
            
            assert(connDB != null);
            
            Statement select = connDB.createStatement();
            ResultSet result = select.executeQuery(query);
            
            while (result.next()) {
                KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.INFO, TestOTADBConnect.class.getName(), "TestOTADBConnectInit()", result.getString(1), null)));
            }
            
        } catch (SQLException ex) {
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, TestOTADBConnect.class.getName(), "TestOTADBConnectInit()", ex.toString(), ex)));            
        } finally {
            OTADbConnect.ReleaseInstance(OTADbConnect.DBCONN_DBSTATUSWRITER);    
        }
        
    }
}
