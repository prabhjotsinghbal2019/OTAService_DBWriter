package com.kochar.services;



//import com.kochar.caches.KTDBStatusAccumulatorQueue; 
import com.kochar.common.LogItem;
import com.kochar.common.OTADbConnect;
import com.kochar.common.ServiceAttribute;
import com.kochar.common.ServiceMessage;
import com.kochar.common.Throttle;
import com.kochar.msg.TripletBean;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public class DBStatusWriterService extends ServiceAttribute {

    private static DBStatusWriterService instance = null;

    public static DBStatusWriterService Instance() {
        if (instance == null) {
            instance = new DBStatusWriterService();
        }
        return instance;
    }

    public static void ReleaseInstance() {
        if (instance != null) {
            instance = null;
        }
    }

    private final int MaxBatchAge = 4;
    private final int MaxQueueSize = 1000;
    private int batchSize = 0;
    private long LastFlushed = 0;
    private static final String LOGINSERT = "{call usp_insert_triplet_info(?,?,?,?,?,?,?,?)}";
    private Connection dbConn = null;
    private CallableStatement cStmt = null;
    private Queue<TripletBean> printQueue = new ConcurrentLinkedQueue<TripletBean>();
    

    public DBStatusWriterService() {
        SpeedControl = new Throttle(1000, 500, 4, 2000, 3000, 5000);
        //Heartbeat = (IHeartbeat) PULSE_HANDLER.build(this, "heartbeat");
        LastFlushed = System.currentTimeMillis();
        //init();
    }

    @Override
    public void heartbeat(Object args) {

        ticksCounter++;
        
        try {

                // step 1: save any log triplet received in accumulator
                if (args != null) {
                    TripletBean t = (TripletBean) args;
                    //StoreTriplet(t);
                    printQueue.add(t);
                    //t.dtorTripletBean();
                    t = null;
                }
                
                // step 2: on time/space conditions being met 
                //         attempt printing of logs to db
                if (TripletPrintCondition() == true) {
                    LogMessages();
                }


        } catch (Exception e) {
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, this.getClass().getName(), "heartbeat()", e.toString(), e)));
        } finally {

        }

    }
    
    private boolean TripletPrintCondition()
    {
        return ( (printQueue.isEmpty() == false) 
                 && ((printQueue.size() >= MaxQueueSize) || (DoPeriodicFlush())) );
    }

    private boolean DoPeriodicFlush() {
        long now = System.currentTimeMillis();
        long logAge = now - LastFlushed;
        float totalSeconds = (logAge / 1000);

        if (totalSeconds >= MaxBatchAge) {
            return true;
        } else {
            return false;
        }
    }
    
    private void LogMessages() {
        try {

            batchSize = 0;
            
            // step 1: Initialize the DB Connection
            if (initConnection() == true) {

                // step 2: Add the print queue elements to db write batch
                printQueue.forEach(t -> {
                    // add message to db batch
                    AddMessageToDBBatch(t);
                });

                // step 3: flush statement batch to db
                if (CommitBatchToDB() > 0) {

                    // step 4: clear the print queue now if all ok
                    printQueue.forEach(t -> {
                        t.dtorTripletBean();
                    });
                    printQueue.clear();
                
                }

            }
            
        } catch (Exception e) {
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, this.getClass().getName(), "LogMessages()", e.toString(), e)));
        } finally {
                // close the db connection - release to conn pool
                closeConnection();
        }
    }
    
    private boolean initConnection() {
        boolean rVal = false;
        try {
            dbConn = OTADbConnect.getInstance(OTADbConnect.DBCONN_DBSTATUSWRITER).getConnection();
            OTADbConnect.getInstance(OTADbConnect.DBCONN_DBSTATUSWRITER).setAutoCommit(false);
            cStmt = dbConn.prepareCall(LOGINSERT);
            rVal = true;
        } catch (SQLException e) {
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, this.getClass().getName(), "initConnection()", e.toString(), e)));
            rVal = false;
        }
        return rVal;
    }
    
    private void closeConnection() {
        try {
            if (cStmt != null) {
               if (!cStmt.isClosed()) cStmt.close();
               cStmt = null;
            }
            OTADbConnect.getInstance(OTADbConnect.DBCONN_DBSTATUSWRITER).closeConnection();
        } catch (SQLException e) {
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, this.getClass().getName(), "closeConnection()", e.toString(), e)));
        }
    }

    private int CommitBatchToDB() {
        int rowsAffected = 0;
        int[]  results;
        try {
            if (batchSize > 0)  {
              results = cStmt.executeBatch();
              rowsAffected = results.length;
              if (rowsAffected > 0) {
                OTADbConnect.getInstance(OTADbConnect.DBCONN_DBSTATUSWRITER).commit();
                batchSize = 0;
                LastFlushed = System.currentTimeMillis();
              } else {
                OTADbConnect.getInstance(OTADbConnect.DBCONN_DBSTATUSWRITER).rollback();  
              }
            }
        } catch (SQLException e) {
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, this.getClass().getName(), "CommitBatchToDB()", e.toString(), e)));
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, this.getClass().getName(), "CommitBatchToDB()", e.getNextException().toString(), e.getNextException())));
        }
        return rowsAffected;
    }

    private void AddMessageToDBBatch(TripletBean m) {
        String mobileNo, imsi, imei, operator;
        int statusValue = 0, serviceid = 0, nodeId = 0;
        long timeStamp = 0;
        
        try {
            
            mobileNo = m.getMobile();
            imsi = m.getImsi();
            imei = m.getImei();
            timeStamp = m.getTimeStamp();
            statusValue = (int) m.getStatus();
            operator = m.getOperatorName();
            serviceid = m.getServiceId();
            nodeId = Integer.valueOf("1");
            
            cStmt.setString(1, mobileNo);
            cStmt.setString(2, imei);
            cStmt.setString(3, imsi);
            cStmt.setInt(4, nodeId); //NodeId
            cStmt.setLong(5, timeStamp);
            cStmt.setInt(6, statusValue);
            cStmt.setString(7, operator);
            cStmt.setInt(8, serviceid);
            cStmt.addBatch();
            
            batchSize++;
            
        } catch (NumberFormatException | SQLException e) {
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, this.getClass().getName(), "AddMessageToDBBatch()", e.toString(), e)));
        }
    }
    
    @Override
    public synchronized void Stop() {
        try {
            LogMessages();
        } catch (Exception e) {
            KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.SEVERE, this.getClass().getName(), "Stop()", e.toString(), e)));
        } finally {
            super.Stop();
        }
    }

}
