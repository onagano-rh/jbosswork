package com.redhat.nrt.onagano;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionBindingEvent;

@WebListener
public class SimpleSessionListener implements HttpSessionListener, HttpSessionAttributeListener, HttpSessionBindingListener {
    public void sessionCreated(HttpSessionEvent event) {
        System.out.println("sessionCreated! " + event);
    }
    public void sessionDestroyed(HttpSessionEvent event) {
        System.out.println("sessionDestroyed! " + event);
    }
    public void attributeAdded(HttpSessionBindingEvent event) {
        System.out.println("attributeAdded! " + event);
    }
    public void attributeRemoved(HttpSessionBindingEvent event) {
        System.out.println("attributeRemoved! " + event);
    }
    public void attributeReplaced(HttpSessionBindingEvent event) {
        System.out.println("attributeReplaced! " + event);
    }
    public void valueBound(HttpSessionBindingEvent event) {
        System.out.println("valueBound! " + event);
    }
    public void valueUnbound(HttpSessionBindingEvent event) {
        System.out.println("valueUnbound! " + event);
    }
}
