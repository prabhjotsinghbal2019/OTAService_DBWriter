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
import com.kochar.services.DBStatusWriterService;
import com.kochar.services.KSimpleLogger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author PBal
 */
public class TestDBStatusWriter {
    
    public TestDBStatusWriter() {
    }
    
    @BeforeAll
    public static void setUpClass() {
        KSimpleLogger.Instance().SetSpeed(Throttle.RunSpeed.Fastest);
        KSimpleLogger.Instance().Start();
        KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.INFO, TestDBStatusWriter.class.getName(), "setUpClass()", "KSimpleLogger Initialized", null)));
    }
    
    @AfterAll
    public static void tearDownClass() {
        KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.INFO, TestDBStatusWriter.class.getName(), "tearDownClass()", "KSimpleLogger DeInitialized", null)));
        KSimpleLogger.Instance().Stop();
        KSimpleLogger.releaseInstance();
    }
    
    @BeforeEach
    public void setUp() {
        DBStatusWriterService.Instance().SetSpeed(Throttle.RunSpeed.Fastest);
        DBStatusWriterService.Instance().Start();
        clearOTAStatusTables();
    }
    
    @AfterEach
    public void tearDown() {
        clearOTAStatusTables();
        DBStatusWriterService.Instance().Stop();
        DBStatusWriterService.ReleaseInstance();
    }
    
    private void clearOTAStatusTables() {
        
        try {
            
            Statement stmt = OTADbConnect.getInstance(OTADbConnect.DBCONN_DBHEALTHMONITOR).getConnection().createStatement();
            
            String sqlDel1 = "DELETE FROM ota_bulk_master WHERE otaid > 0";
            String sqlDel2 = "DELETE FROM ota_bulk_otastatus WHERE otaid > 0";
            
            stmt.executeUpdate(sqlDel1);
            stmt.executeUpdate(sqlDel2);
            
            stmt.close();
            
        } catch (SQLException ex) {
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, TestDBStatusWriter.class.getName(), "clearOTAStatusTables()", ex.toString(), ex)));
        } finally {
            OTADbConnect.ReleaseInstance(OTADbConnect.DBCONN_DBHEALTHMONITOR);
        }
        
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

    @Test
    public void TestWith10Requests() {

        try {
            
            for (int i = 0; i < 10; i++) {
                TripletBean m = RandomTripletGenerator.getRandomOTATriplet();
                DBStatusWriterService.Instance().SendMessage(new ServiceMessage(m));
            }

            Thread.sleep(3000);
            Statement stmt = OTADbConnect.getInstance(OTADbConnect.DBCONN_DBCACHEREADER).getConnection().createStatement();
         
            String sqlCount = "SELECT COUNT(otaid) FROM ota_bulk_otastatus";

            ResultSet rs = stmt.executeQuery(sqlCount);
            while(rs.next()){
               KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.INFO, TestDBStatusWriter.class.getName(), "TestWith10Requests()", "otastatus table count: " + rs.getString(1), null))); 
            }
            rs.close();
            stmt.close();
            
        } catch (Exception e) {
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, TestDBStatusWriter.class.getName(), "TestWith10Requests()", e.toString(), e)));
        } finally {
            OTADbConnect.ReleaseInstance(OTADbConnect.DBCONN_DBCACHEREADER);
        }

    }
   
    @Test
    public void TestWith1000Requests() {
        try {
            
            for (int i = 0; i < 1000; i++) {
                TripletBean m = RandomTripletGenerator.getRandomOTATriplet();
                DBStatusWriterService.Instance().SendMessage(new ServiceMessage(m));
            }

            Thread.sleep(20000);  // 20 seconds to complete db writes
            Statement stmt = OTADbConnect.getInstance(OTADbConnect.DBCONN_DBCACHEWRITER).getConnection().createStatement();
         
            String sqlCount = "SELECT COUNT(otaid) FROM ota_bulk_otastatus";

            ResultSet rs = stmt.executeQuery(sqlCount);
            while(rs.next()){
               KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.INFO, TestDBStatusWriter.class.getName(), "TestWith1000Requests()", "otastatus table count: " + rs.getString(1), null))); 
            }
            rs.close();
            stmt.close();
            
        } catch (Exception e) {
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, TestDBStatusWriter.class.getName(), "TestWith1000Requests()", e.toString(), e)));
        } finally {
            OTADbConnect.ReleaseInstance(OTADbConnect.DBCONN_DBCACHEWRITER);
        }
        
    }
    
    @Test
    public void TestWith10000Requests() {

        try {
            
            for (int i = 0; i < 10000; i++) {
                TripletBean m = RandomTripletGenerator.getRandomOTATriplet();
                DBStatusWriterService.Instance().SendMessage(new ServiceMessage(m));
            }

            Thread.sleep(100000);  // 100 seconds to complete db writes
            Statement stmt = OTADbConnect.getInstance(OTADbConnect.DBCONN_DBCACHEWRITER).getConnection().createStatement();
         
            String sqlCount = "SELECT COUNT(otaid) FROM ota_bulk_otastatus";

            ResultSet rs = stmt.executeQuery(sqlCount);
            while(rs.next()){
               KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.INFO, TestDBStatusWriter.class.getName(), "TestWith1000Requests()", "otastatus table count: " + rs.getString(1), null))); 
            }
            rs.close();
            stmt.close();
            
        } catch (Exception e) {
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, TestDBStatusWriter.class.getName(), "TestWith1000Requests()", e.toString(), e)));
        } finally {
            OTADbConnect.ReleaseInstance(OTADbConnect.DBCONN_DBCACHEWRITER);
        }
        
    }
    
    @Test
    public void TestWith100000Requests() {
        
    }
    
}
