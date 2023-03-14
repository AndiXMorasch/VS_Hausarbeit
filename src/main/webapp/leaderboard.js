let leaderboard;

function $(id) {
    return document.getElementById(id);
}

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

function getUsersWithFinishedExercises(groupname) {
    let xmlhttp = getXMLHttpRequest();
    xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/GroupServer?name=' + groupname + "&leaderboardReq=" + true, true);
    let container = $("leaderboard-container");
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {

        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            console.log("response ist fertig: " + xmlhttp.responseText);
            leaderboard = JSON.parse(xmlhttp.responseText);
            displayLeaderboard();
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            console.log(xmlhttp)
        }
    };
    xmlhttp.send(null);
}

function displayLeaderboard() {
    leaderboard.sort((a, b) => b.finishedExercises - a.finishedExercises);
    let container = $("leaderboard-container");
    let html = "";
    leaderboard.forEach((l, i) => {
        html += '<div class="leaderboard-element">';
        html += '<span class="place">' + (i + 1) + '. </span>';
        html += '<span class="username">' + (l.username) + '</span>';
        html += '<span class="spacer"></span>';
        html += '<span class="hint">Abgeschlossene Übungen: </span>';
        html += '<span class="numberOfExercises">' + (l.finishedExercises) + '</span>';
        html += '</div>';
    });
    container.innerHTML = html;
}