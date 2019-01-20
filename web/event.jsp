<%--
  Created by IntelliJ IDEA.
  User: vinay
  Date: 1/19/19
  Time: 7:48 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>New Event</title>
</head>
<body>
<p>
    <%= request.getParameter("event_name")%>
    <br>
    <%= request.getParameter("event_date")%>
    <br>
    <%= request.getParameter("event_time")%>
    <br>
    <%= request.getParameter("event_loc")%>
    <br>
    <%= request.getParameter("event_room")%>
</p>

</body>
</html>
