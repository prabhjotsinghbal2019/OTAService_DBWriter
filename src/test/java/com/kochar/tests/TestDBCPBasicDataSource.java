/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kochar.tests;

import com.kochar.common.LogItem;
import com.kochar.common.OTADbConnectPoolHelper;
import com.kochar.common.ServiceMessage;
import com.kochar.common.Throttle;
import com.kochar.services.KSimpleLogger;
import java.sql.Connection;
import java.sql.SQLException;
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
public class TestDBCPBasicDataSource {
    
    public TestDBCPBasicDataSource() {
        //        System.out.println("TestDBCPBasicDataSource ctor() called");
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
        System.out.println("setUpClass called");
        try {
            KSimpleLogger.Instance().SetSpeed(Throttle.RunSpeed.Fastest);
            KSimpleLogger.Instance().Start();
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.INFO, TestDBCPBasicDataSource.class.getName(), "setUpClass()", "KSimpleLogger Initialized", null)));
            OTADbConnectPoolHelper.getInstance();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
        System.out.println("tearDownClass called");
        OTADbConnectPoolHelper.releaseInstance();
        KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.INFO, TestDBCPBasicDataSource.class.getName(), "tearDownClass()", "KSimpleLogger DeInitialized", null)));
        KSimpleLogger.Instance().Stop();
        KSimpleLogger.releaseInstance();
    }

    @BeforeEach
    public void setUp() throws Exception {
//        System.out.println("setUp called");
    }

    @AfterEach
    public void tearDown() throws Exception {
//        System.out.println("tearDown called");
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void Test_OTADBConnectPoolHelper_Init() {
        System.out.println("Test_OTADBConnectPoolHelper_Init test called");
        try {
            // get data source stats
            OTADbConnectPoolHelper.getInstance().printDataSourceStats();
            
            // get a connection handle
            Connection dbConn = OTADbConnectPoolHelper.getInstance().getConnection();
            assert (dbConn != null);
            
            // get data source stats
            OTADbConnectPoolHelper.getInstance().printDataSourceStats();            
            
            // release the connection handle
            assert (OTADbConnectPoolHelper.getInstance().relConnection(dbConn) == true);
            
            // get data source stats
            OTADbConnectPoolHelper.getInstance().printDataSourceStats();            
            
        } catch (SQLException ex) {
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, this.getClass().getName(), "Test_OTADBConnectPoolHelper_Init()", ex.toString(), ex)));
        }
    }
    
    
}
