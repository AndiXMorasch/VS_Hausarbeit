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
    String groupName = request.getParameter("name");
    if (groupName == null) {
        response.sendRedirect("/VS_Gruppentrainingsplan_war/groups.jsp");
        return;
    }

    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.")
            .withZone(ZoneId.systemDefault());
    Instant today = Instant.now();
%>
<html>
<head>
    <title><%= groupName %>
    </title>
    <link rel="stylesheet" href="groups.css">
    <link rel="stylesheet" href="group.css">
    <link rel="stylesheet" href="leaderboard.css">
    <link rel="icon" href="${pageContext.request.contextPath}/osnasports_favicon.png" type="image/png">
    <script type="text/javascript" src="leaderboard.js"></script>
    <script type="text/javascript" src="group.js"></script>
</head>
<body>
<img id="osnasports-logo" src="osnasports.png" alt="site-logo" onclick="goToHomescreen()">
<div class="main">
    <h1><%= groupName %>
    </h1>
    <div class="overview" id="overview">
        <span> </span>
        <span> <%= dateFormat.format(today.minus(6, ChronoUnit.DAYS))%> </span>
        <span> <%= dateFormat.format(today.minus(5, ChronoUnit.DAYS))%> </span>
        <span> <%= dateFormat.format(today.minus(4, ChronoUnit.DAYS))%> </span>
        <span> <%= dateFormat.format(today.minus(3, ChronoUnit.DAYS))%> </span>
        <span> <%= dateFormat.format(today.minus(2, ChronoUnit.DAYS))%> </span>
        <span> <%= dateFormat.format(today.minus(1, ChronoUnit.DAYS))%> </span>
        <span> <%= dateFormat.format(today)%> </span>
    </div>
    <script type="text/javascript">
        getUsersDailyExercises(<%= "'" + groupName + "'"%>);
    </script>
    <h2>Rangliste
    </h2>
    <div id="leaderboard-container">

    </div>
    <script type="text/javascript">
        getUsersWithFinishedExercises(<%= "'" + groupName + "'"%>);
    </script>
    <h2>Absolvierte Trainingseinheiten</h2>
    <div id="trainings">
        <p>Du hast noch kein Training absolviert</p>
    </div>

    <h2>Trainingspläne</h2>
    <div id="training-plans">
        <p>Leider gibt es noch keine Trainingspläne</p>
    </div>
    <br>
    <button onclick="window.location.href = '/VS_Gruppentrainingsplan_war/groups.jsp'">Zurück
    </button>
</div>
<script>
    groupName = '<%=groupName%>';
    getGroup();
    getTrainings();
    getTrainingPlans();
</script>
</body>
</html>

