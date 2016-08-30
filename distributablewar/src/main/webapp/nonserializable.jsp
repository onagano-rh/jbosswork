<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.redhat.nrt.onagano.NonSerializable" %>
<%@ page import="com.redhat.nrt.onagano.MyHolder" %>
<%@ page import="com.redhat.nrt.onagano.MyHolder2" %>
<%@ page import="java.util.HashSet" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<%
	Object obj = session.getAttribute("counter");
	int counter = 0;
	if (obj != null) {
		counter = ((Integer) obj).intValue();
	}
	counter += 1;
	session.setAttribute("counter", new Integer(counter));

    if (counter == 1) {
        session.setAttribute("set", new HashSet());
    }

    Object set = session.getAttribute("set");
    if (set != null && counter == 3) {
        MyHolder mh = new MyHolder();
        MyHolder2 mh2 = new MyHolder2();
        NonSerializable ns = new NonSerializable();
        ns.setValue("count:" + counter);
        mh2.setMyHolder(ns);
        mh.setMyHolder2(mh2);
        ((HashSet) set).add(mh);
        System.out.println("NonSerializable has been inserted into session!");
    }
%>
<body>
<h1>HTTP Session Test Page</h1>
<p>Counter: <%= counter %></p>
</body>
</html>
