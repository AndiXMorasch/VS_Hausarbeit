function getXMLHttpRequest() {
    // XMLHttpRequest for Firefox, Opera, Safari
    if (window.XMLHttpRequest) {
        return new XMLHttpRequest();
    }
    if (window.ActveObject) { // Internet Explorer
        try { // for IE new
            return new ActiveXObject("Msxml2.XMLHTTP");
        } catch (e) {  // for IE old
            try {
                return new ActiveXObject("Microsoft.XMLHTTP");
            } catch (e) {
                alert("Your browser does not support AJAX!");
                return null;
            }
        }
    }
    return null;
}

function getHttpRequest(url) {
    console.log("Ich gehe in HtmlHttpRequest rein");
    var xmlhttp = getXMLHttpRequest();
    const username = document.getElementById("usernameTextInput").value;
    const password = document.getElementById("passwordTextInput").value;
    xmlhttp.open("GET", url + "?username=" + username + "&password=" + password, true);
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {

        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            console.log("Ich erhalte eine Response vom Server");
            const response = JSON.parse(xmlhttp.responseText);
            if (response) {
                // Nutzer hat sich erfolgreich eingeloggt --> weiterleiten an groups.jsp
                window.location.href = "/VS_Gruppentrainingsplan_war/groups.jsp";
            } else {
                // Nutzer hat sich nicht erfolgreich eingeloggt --> Fehlermeldung ausgeben
                document.getElementById("errorMessageLogin").innerHTML = "Der Username oder " + "das Passwort ist nicht korrekt.";
                document.getElementById("errorMessageLogin").style.color = 'red';
            }
        }
    };
    xmlhttp.send(null);
}

function goToLoginScreen() {
    window.location.href = "/VS_Gruppentrainingsplan_war/index.jsp";
}