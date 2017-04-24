package com.example.restcache;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.jboss.logging.Logger;

@WebListener
public class CacheInitializer implements ServletContextListener {
	private static Logger LOG = Logger.getLogger(CacheInitializer.class);
	
	@Inject
	private CacheManagerService cmService;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		LOG.info("Context destroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		LOG.info("Context initialized " + cmService);
	}
}
