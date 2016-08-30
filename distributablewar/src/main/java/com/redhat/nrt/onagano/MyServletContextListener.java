package com.redhat.nrt.onagano;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class MyServletContextListener implements ServletContextListener {

    public void contextDestroyed(ServletContextEvent arg0) {
        String strSecs = arg0.getServletContext().getInitParameter("sleep-seconds-destroying");
        System.out.println("Context Destroying...");
        sleep(strSecs);
        System.out.println("Context Destroyed.");
    }

    public void contextInitialized(ServletContextEvent arg0) {
        String strSecs = arg0.getServletContext().getInitParameter("sleep-seconds-initializing");
        System.out.println("Context Initializing...");
        sleep(strSecs);
        System.out.println("Context Initialized.");
    }

    private void sleep(String strSecs) {
        int secs = 15;
        try {
            secs = Integer.parseInt(strSecs);
        } catch (NumberFormatException e1) {
            e1.printStackTrace();
        }
        for (int i = 0; i < secs; i++) {
            try {
                System.out.println("Sleeping for " + (i + 1) + " secconds...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
