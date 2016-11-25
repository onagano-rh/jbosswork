package com.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.util.Properties;

/*
 * Originally taken from https://bugzilla.redhat.com/show_bug.cgi?id=1008763.
 */
@SuppressWarnings("serial")
@WebServlet("/HogeServlet")
public class HogeServlet extends HttpServlet {
	private Logger logger = Logger.getLogger("com.example");

    @Resource(lookup="java:jboss/datasources/ExampleDS")
    private DataSource dataSource;
    
    private int size = 1;
    private long sleep = 100;

    @PostConstruct
    private void createTable() throws Exception {
        Connection con = dataSource.getConnection();
        Statement st = con.createStatement();
        st.execute("create table if not exists test(pkey number, TRX_DATE timestamp)");
        st.close();
        con.close();
    }    
    
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        pw.println("Hello!!Welcome to the first servlet");
        try {
        	size = Integer.parseInt(request.getParameter("size"));
        } catch (NumberFormatException ignore) {}
        try {
        	sleep = Long.parseLong(request.getParameter("sleep"));
        } catch (NumberFormatException ignore) {}

        try {
        	doSQL();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void doSQL() throws SQLException {
        Connection con = dataSource.getConnection();
        try {
            for (int i = 1; i <= size; i++) {
                String s = "select * from test where pkey in (" + repeat(i) + ")";
                PreparedStatement pst = con.prepareStatement(s);
                try {
                    for (int j = 1; j <= i; j++) {
                        pst.setInt(j, 123);
                    }
                    ResultSet rs = pst.executeQuery();

                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {}

                    rs.close();       
                    logger.log(Level.INFO, "Prepared statement {0} executed.", i);
                }
                finally {
                    pst.close();                	
                }
            } // end for
        }
        finally {
            con.close();
        }
    }

    private String repeat(int params) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < params; i++) {
            builder.append("?,");
        }
        StringBuilder result = builder.delete(builder.length() - 1, builder.length());
        return result.toString();
    }
}
