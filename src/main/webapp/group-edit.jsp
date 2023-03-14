<%--
  Created by IntelliJ IDEA.
  User: hendrikpurschke
  Date: 14.02.23
  Time: 11:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Trainingsgruppe</title>
    <link rel="stylesheet" href="group-edit.css">
    <link rel="icon" href="${pageContext.request.contextPath}/osnasports_favicon.png" type="image/png">
    <script type="text/javascript" src="group-edit.js"></script>
</head>
<body>
<img id="osnasports-logo" src="osnasports.png" alt="site-logo" onclick="goToHomescreen()">
<div class="main">
    <h1>Neue Trainingsgruppe</h1>

    <label for="group-name">Gruppenname</label>
    <input type="text" id="group-name">

    <h2>Mitglieder</h2>
    <div class="user-selection">
        <label for="search">Suche</label>
        <input type="text" id="search" onkeyup="filterUsers(this.value)">


        <div id="users">

        </div>
    </div>

    <div id="selected-users">

    </div>
    <div class="row">
        <button onclick="window.location.href = '/VS_Gruppentrainingsplan_war/groups.jsp'">Zurück
        </button>
        <button class="save" onclick="onSave()">Speichern</button>
    </div>
    <script type="text/javascript">
        filterUsers("");
        displaySelectedUsers();
    </script>
</div>
</body>
</html>

