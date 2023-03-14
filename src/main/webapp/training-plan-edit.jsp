<%--
  Created by IntelliJ IDEA.
  User: Andreas
  Date: 15.02.2023
  Time: 12:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Trainingsplan</title>
    <link rel="stylesheet" href="training-plan-edit.css">
    <link rel="icon" href="${pageContext.request.contextPath}/osnasports_favicon.png" type="image/png">
    <script type="text/javascript" src="training-plan-edit.js"></script>
</head>
<body>
<img id="osnasports-logo" src="osnasports.png" alt="site-logo" onclick="goToHomescreen()">
<div class="main">
    <h1>Neuer Trainingsplan</h1>
    <label for="training-plan-name">Name des Trainingsplans</label>
    <input type="text" id="training-plan-name">

    <h2>Übung anlegen</h2>

    <label for="create-exercise">Name der Übung</label>
    <div class="row">
        <input type="text" id="create-exercise">
        <button class="saveExercise" onclick="onSaveExercise()">Übung anlegen</button>
    </div>

    <h2>Übungen wählen</h2>
    <div class="exercise-selection">
        <label for="search">Suche</label>
        <input type="text" id="search" onkeyup="filterExercises(this.value)">


        <div id="exercises">

        </div>
    </div>
    <div id="selected-exercises">

    </div>
    <br>
    <div>
        <label for="validFrom">Übungsanfang:</label>
        <input type="date" id="validFrom"
               value="2023-03-01"
               min="2023-01-01" max="2024-01-01">
    </div>
    <br>
    <div>
        <label for="validUntil">Übungsende:</label>
        <input type="date" id="validUntil"
               value="2023-12-31"
               min="2023-01-01" max="2024-01-01">
    </div>
    <br>
    <div class="bottom-row">
        <button onclick="window.location.href = '/VS_Gruppentrainingsplan_war/groups.jsp'">Zurück
        </button>
        <button class="save" onclick="onSave()">Speichern</button>
    </div>
    <script type="text/javascript">
        filterExercises("");
        displaySelectedExercises();
    </script>
</div>
</body>
</html>
