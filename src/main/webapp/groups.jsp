<%@ page import="com.vs_project.vs_gruppentrainingsplan.models.User" %><%--
  Created by IntelliJ IDEA.
  User: hendrikpurschke
  Date: 14.02.23
  Time: 10:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("/VS_Gruppentrainingsplan_war");
        return;
    }
%>
<html>
<head>
    <title>Trainingsgruppen</title>
    <link rel="stylesheet" href="groups.css">
    <link rel="icon" href="${pageContext.request.contextPath}/osnasports_favicon.png" type="image/png">
    <script type="text/javascript" src="groups.js"></script>
</head>
<body>
<img id="osnasports-logo" src="osnasports.png" alt="site-logo" onclick="goToHomescreen()">
<div class="main">
    <h1>Trainingsgruppen von <%= user.getUsername() %>
    </h1>
    <div id="own-groups">
        <p>Du bist leider noch keiner Gruppe zugeordnet.</p>
    </div>
    <% if (user.isAdmin()) { %>
    <h1>Alle Gruppen</h1>
    <div id="all-groups">
        <p>Es sind noch keine Gruppen vorhanden</p>
    </div>
    <script type="text/javascript">
        isAdmin = true;
        getGroups(true);
    </script>
    <button id="new-group" onclick="window.location.href = '/VS_Gruppentrainingsplan_war/group-edit.jsp'">Neue Gruppe
        erstellen
    </button>
    <br>
    <button onclick="window.location.href = '/VS_Gruppentrainingsplan_war/training-plan-edit.jsp'">Neuen Trainingsplan
        erstellen
    </button>
    <% } %>
    <br>
    <button onclick="logout()">Logout
    </button>
</div>
<script type="text/javascript">
    getGroups();
</script>
</body>
</html>

