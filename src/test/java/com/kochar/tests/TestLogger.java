/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kochar.tests;

import com.kochar.common.LogItem;
import com.kochar.common.ServiceMessage;
import com.kochar.common.Throttle;
import com.kochar.services.KSimpleLogger;
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
public class TestLogger {
    
    public TestLogger() {
        //        System.out.println("TestLogger ctor() called");
    }
    
    @BeforeAll
    public static void setUpClass() {
        System.out.println("setUpClass called");
        KSimpleLogger.Instance().SetSpeed(Throttle.RunSpeed.Fastest);
        KSimpleLogger.Instance().Start();
        KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.INFO, TestLogger.class.getName(), "setUpClass()", "KSimpleLogger Initialized", null)));
    }
    
    @AfterAll
    public static void tearDownClass() {
        System.out.println("tearDownClass called");
        KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.INFO, TestLogger.class.getName(), "tearDownClass()", "KSimpleLogger DeInitialized", null)));
        KSimpleLogger.Instance().Stop();
        KSimpleLogger.releaseInstance();
    }
    
    @BeforeEach
    public void setUp() {
//        System.out.println("setUp called");
    }
    
    @AfterEach
    public void tearDown() {
//        System.out.println("tearDown called");
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void testLogging() {
        System.out.println("testLogging test called");
        KSimpleLogger.Instance().SendMessage(new ServiceMessage(new LogItem(Level.INFO, TestLogger.class.getName(), "testLogging()", "KSimpleLogger testLogging", null)));
    }
}
