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
import com.kochar.msg.TripletBean;
import com.kochar.services.KSimpleLogger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
    
    @Test
    public void TestWith1Request() {

        final String LOGINSERT = "{call public.usp_insert_triplet_info(?,?,?,?,?,?,?,?)}";

        try {

            TripletBean m = RandomTripletGenerator.getRandomOTATriplet();

            String mobileNo = m.getMobile();
            String imsi = m.getImsi();
            String imei = m.getImei();
            long timeStamp =  m.getTimeStamp();
            int statusValue =  m.getStatus();
            String operator = m.getOperatorName();
            int serviceid = m.getServiceId();
            int nodeId = Integer.valueOf("1");

            Connection dbConn = OTADbConnect.getInstance(OTADbConnect.DBCONN_DBSTATUSWRITER).getConnection();
            assert (dbConn != null);
            OTADbConnect.getInstance(OTADbConnect.DBCONN_DBSTATUSWRITER).setAutoCommit(false);
            CallableStatement cStmt = dbConn.prepareCall(LOGINSERT);

            cStmt.setString(1, mobileNo);
            cStmt.setString(2, imei);
            cStmt.setString(3, imsi);
            cStmt.setInt(4, nodeId); 
            cStmt.setLong(5, timeStamp);
            cStmt.setInt(6, statusValue);
            cStmt.setString(7, operator);
            cStmt.setLong(8, serviceid);
            cStmt.addBatch();

            int[] results = cStmt.executeBatch();
            int rowsAffected = results.length;

            assert (rowsAffected == 1);

            if (rowsAffected > 0) {
                OTADbConnect.getInstance(OTADbConnect.DBCONN_DBSTATUSWRITER).commit();
                KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.INFO, TestOTADBConnect.class.getName(), "TestWith1Request()", "ok writing to db", null)));
            } else {
                OTADbConnect.getInstance(OTADbConnect.DBCONN_DBSTATUSWRITER).rollback();
                KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, TestOTADBConnect.class.getName(), "TestWith1Request()", "error writing to db", null)));
            }

        } catch (SQLException ex) {
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, TestOTADBConnect.class.getName(), "TestOTADBConnectInit()", ex.toString(), ex)));
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, TestOTADBConnect.class.getName(), "TestOTADBConnectInit()", ex.getNextException().toString(), ex.getNextException())));            
        } finally {
            OTADbConnect.ReleaseInstance(OTADbConnect.DBCONN_DBSTATUSWRITER);
        }

    }

    @Test
    public void TestWithOneRequest() {

        final String LOGINSERT = "{call public.usp_triplet_info(?,?,?)}";

        try {

            TripletBean m = RandomTripletGenerator.getRandomOTATriplet();

            String mobileNo = m.getMobile();
            String imsi = m.getImsi();
            String imei = m.getImei();
            long timeStamp =  m.getTimeStamp();
            int statusValue =  m.getStatus();
            String operator = m.getOperatorName();
            int serviceid = m.getServiceId();
            int nodeId = Integer.valueOf("1");

            Connection dbConn = OTADbConnect.getInstance(OTADbConnect.DBCONN_DBSTATUSWRITER).getConnection();
            assert (dbConn != null);
            OTADbConnect.getInstance(OTADbConnect.DBCONN_DBSTATUSWRITER).setAutoCommit(false);
            CallableStatement cStmt = dbConn.prepareCall(LOGINSERT);

            cStmt.setInt(1, nodeId); 
            cStmt.setLong(2, timeStamp);
            cStmt.setInt(3, statusValue);
            cStmt.addBatch();

            int[] results = cStmt.executeBatch();
            int rowsAffected = results.length;

            assert (rowsAffected == 1);

            if (rowsAffected > 0) {
                OTADbConnect.getInstance(OTADbConnect.DBCONN_DBSTATUSWRITER).commit();
                KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.INFO, TestOTADBConnect.class.getName(), "TestWith1Request()", "ok writing to db", null)));
            } else {
                OTADbConnect.getInstance(OTADbConnect.DBCONN_DBSTATUSWRITER).rollback();
                KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, TestOTADBConnect.class.getName(), "TestWith1Request()", "error writing to db", null)));
            }

        } catch (SQLException ex) {
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, TestOTADBConnect.class.getName(), "TestOTADBConnectInit()", ex.toString(), ex)));
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, TestOTADBConnect.class.getName(), "TestOTADBConnectInit()", ex.getNextException().toString(), ex.getNextException())));            
        } finally {
            OTADbConnect.ReleaseInstance(OTADbConnect.DBCONN_DBSTATUSWRITER);
        }

    }
    
}
