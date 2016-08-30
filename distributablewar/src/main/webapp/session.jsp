<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
%>
<body>
<h1>HTTP Session Test Page</h1>
<p>Counter: <%= counter %></p>
</body>
</html>