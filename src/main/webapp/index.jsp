<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.vs_project.vs_gruppentrainingsplan.models.User" %>
<% HttpSession userSession = request.getSession();
    User user = (User) userSession.getAttribute("user");

    if (user != null) {
        String redirectURL = "/VS_Gruppentrainingsplan_war/groups.jsp";
        response.sendRedirect(redirectURL);
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" href="index.css">
    <link rel="icon" href="${pageContext.request.contextPath}/osnasports_favicon.png" type="image/png">
    <script type="module" src="index.js"></script>
    <script type="text/javascript" src="index.js"></script>
</head>
<body>
<img id="osnasports-logo" src="osnasports.png" alt="site-logo" onclick="goToLoginScreen()">
<form class="main" onsubmit="return false;">
    <h1><%= "Gruppentrainingsplan" %>
    </h1>
    <h2><%= "User Login:" %>
    </h2>
    <label id="usernameTextInputLabel" for="usernameTextInput">Username:</label><br>
    <input type="text" id="usernameTextInput" name="username"><br><br>
    <label id="passwordTextInputLabel" for="passwordTextInput">Password:</label><br>
    <input type="password" id="passwordTextInput" name="password"><br><br>
    <button class="login" onclick='getHttpRequest("LoginServer")'>Login</button>
</form>
<br><br>
<p id="errorMessageLogin"></p>
<!-- <a href="hello-servlet">Hello Servlet</a> -->
</body>
</html>