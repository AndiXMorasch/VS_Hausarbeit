<%@ page import="com.vs_project.vs_gruppentrainingsplan.models.User" %>
<%@ page import="java.time.Instant" %>
<%@ page import="java.time.temporal.ChronoUnit" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.ZoneId" %>
<%--
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
    String trainingPlanName = request.getParameter("name");
    String groupname = request.getParameter("groupname");
    if (trainingPlanName == null || groupname == null) {
        response.sendRedirect("/VS_Gruppentrainingsplan_war/groups.jsp");
        return;
    }
%>
<html>
<head>
    <title><%= trainingPlanName %>
    </title>
    <link rel="stylesheet" href="groups.css">
    <link rel="stylesheet" href="training-plan.css">
    <link rel="icon" href="${pageContext.request.contextPath}/osnasports_favicon.png" type="image/png">
    <script type="text/javascript" src="training-plan.js"></script>
</head>
<body>
<img id="osnasports-logo" src="osnasports.png" alt="site-logo" onclick="goToHomescreen()">
<div class="main">
    <h1><%= trainingPlanName %>
    </h1>

    <h2>Übungen</h2>
    <div id="exercises">
        <p>Dieser Trainingsplan hat keine Übungen.</p>
    </div>

    <button id="new-training"
            onclick="onStartTraining()">
        Training starten
    </button>
    <br>
    <button onclick="window.location.href = '/VS_Gruppentrainingsplan_war/group.jsp?name=<%= groupname %>'">
        Zurück
    </button>
</div>
<script>
    getTrainingPlan();
</script>
</body>
</html>

