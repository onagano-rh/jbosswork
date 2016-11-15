<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Test JSP</title>
</head>
<%
    Object obj = session.getAttribute("counter");
    int counter = 0;
    if (obj != null) {
        counter = ((Integer) obj).intValue();
    }
    counter += 1;
    session.setAttribute("counter", new Integer(counter));
    
    String secs = request.getParameter("secs");
    if (secs != null) {
        try {
            int isecs = Integer.parseInt(secs);
            Thread.sleep(1000L * isecs);
        }
        catch (Exception ignore) {}
    }
    %>
<body>

<h1>Test JSP</h1>

<h2>Counter in HTTP session</h2>
<p>Counter: <%= counter %></p>

<h2>Sleep period</h2>
<p>Slept: <%= secs %></p>

</body>
</html>
