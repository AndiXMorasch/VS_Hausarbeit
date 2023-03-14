let group;
let groupName;
let trainingPlans;
let trainings;
let overviewTable;
let name = new URLSearchParams(window.location.search).get('name');

initWebsocket();

// inspiriert durch https://javascript.info/websocket
function initWebsocket() {
    let socket = new WebSocket(`ws://${window.location.host}/VS_Gruppentrainingsplan_war/group/${name}`);

    socket.onopen = function (e) {
        console.log("[open] Connection established");
    };

    socket.onmessage = function (event) {
        let updateInfo = JSON.parse(event.data);
        leaderboard.find(e => e.username === updateInfo.username).finishedExercises = updateInfo.finishedExercisesAllTime;
        displayLeaderboard();
        if (updateInfo.daysBeforeToday < 7) {
            let container = $(updateInfo.username + '_' + (7 - updateInfo.daysBeforeToday));
            let n = updateInfo.finishedExercises;
            container.innerHTML = n === 0 ? '<span class="zero">' + n + '</span>' : n;
        }
    };

    socket.onclose = function (event) {
        if (event.wasClean) {
            console.log(`[close] Connection closed cleanly, code=${event.code} reason=${event.reason}`);
        } else {
            // e.g. server process killed or network down
            // event.code is usually 1006 in this case
            console.log('[close] Connection died');
        }
    };

    socket.onerror = function (error) {
        console.error(`[error]`);
    };
}

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

function getGroup() {
    let xmlhttp = getXMLHttpRequest();

    xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/GroupServer?name=' + name, true);

    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {

        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            console.log("group-response ist fertig: " + xmlhttp.responseText);
            group = JSON.parse(xmlhttp.responseText);
            initUsers();
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            console.log(xmlhttp)
        }
    };
    xmlhttp.send(null);
}

function getTrainings() {
    let xmlhttp = getXMLHttpRequest();

    xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/TrainingServer', true);

    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {

        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            trainings = JSON.parse(xmlhttp.responseText);
            initTrainings();
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            console.log(xmlhttp)
        }
    };
    xmlhttp.send(null);
}

function getTrainingPlans() {
    let xmlhttp = getXMLHttpRequest();

    xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/TrainingPlanServer', true);

    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {

        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            trainingPlans = JSON.parse(xmlhttp.responseText);
            initTrainingPlans();
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            console.log(xmlhttp)
        }
    };
    xmlhttp.send(null);
}

function initUsers() {
    let container = $("overview");
    group.users.forEach(u => {
        let html = '<span class="username">' + u.username + '</span>';
        for (let i = 1; i <= 7; i++) {
            html += `<span id="${u.username}_${i}"></span>`;
        }
        container.innerHTML = container.innerHTML + html;
    });
}

function initTrainings() {
    let container = $("trainings");
    if (trainings.length === 0) {
        container.innerHTML = "Du hast noch keine Trainingseinheit absolviert.";
        return;
    }
    let html = "";
    trainings.forEach(t => {
        html += '<div class="training clickable" onclick="window.location.href = \'/VS_Gruppentrainingsplan_war/training.jsp?name=' + t.trainingPlan.name + '&date=' + t.date + '&groupname=' + groupName + '\'">';
        html += '<span class="training-plan-name">' + t.trainingPlan.name + '</span>';
        html += '<span class="hint">' + t.date + '</span>';
        html += '<span class="hint">' + t.exercises.filter(e => e.finished).length + '/' + t.exercises.length + ' Übung' + (t.exercises.length === 1 ? "" : "en") + '</span>';
        html += '</div>';
    });
    container.innerHTML = html;
}

function initTrainingPlans() {
    let container = $("training-plans");
    if (trainingPlans.length === 0) {
        container.innerHTML = "Leider gibt es aktuell keine Trainingspläne";
        return;
    }
    let html = "";
    trainingPlans.forEach(t => {
        html += '<div class="training-plan clickable" onclick="window.location.href = \'/VS_Gruppentrainingsplan_war/training-plan.jsp?name=' + t.name + '&groupname=' + groupName + '\'">';
        html += '<span class="training-plan-name">' + t.name + '</span>';
        html += '<span class="hint">' + t.validFrom + ' ' + t.validUntil + '</span>';
        html += '<span class="hint">' + t.exercises.length + ' Übung' + (t.exercises.length === 1 ? "" : "en") + '</span>';
        html += '</div>';
    });
    container.innerHTML = html;
}

function getUsersWithFinishedExercises(groupname) {
    let xmlhttp = getXMLHttpRequest();
    xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/GroupServer?name=' + groupname + "&leaderboardReq=" + true, true);
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {

        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
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

function getUsersDailyExercises(groupname) {
    let xmlhttp = getXMLHttpRequest();
    xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/GroupServer?name=' + groupname + "&overviewReq=" + true, true);
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {

        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            overviewTable = JSON.parse(xmlhttp.responseText);
            fillOverviewTable();
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            console.log(xmlhttp)
        }
    };
    xmlhttp.send(null);
}

function fillOverviewTable() {
    let i = 1;
    overviewTable.forEach(o => {
        let container = $(o.username + '_' + i);
        let n = o.exercisesMade;
        container.innerHTML = n === 0 ? '<span class="zero">' + n + '</span>' : n;
        if (i % 7 === 0) {
            i = 0;
        }
        i++;
    });
}

function goToHomescreen() {
    window.location.href = "/VS_Gruppentrainingsplan_war/groups.jsp";
}