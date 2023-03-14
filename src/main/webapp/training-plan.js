let trainingPlan;
let name = new URLSearchParams(window.location.search).get('name');
let groupname = new URLSearchParams(window.location.search).get('groupname');

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

function getTrainingPlan() {
    let xmlhttp = getXMLHttpRequest();
    xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/TrainingPlanServer?name=' + name, true);
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {

        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            trainingPlan = JSON.parse(xmlhttp.responseText);
            initExercises();
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            console.log(xmlhttp)
        }
    };
    xmlhttp.send(null);
}

function initExercises() {
    let container = $("exercises");
    let html = "";
    let i = 0;
    console.log(trainingPlan);
    trainingPlan.exercises.forEach(e => {
        html += '<div class="exercise">';
        html += '<span class="name">' + (++i) + '. ' + e.exerciseName + ' </span>';
        html += '</div>';
    });
    container.innerHTML = html;
}

function onStartTraining() {
    let xmlhttp = getXMLHttpRequest();
    xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/TrainingServer?name=' + name, true);
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {

        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            let date = JSON.parse(xmlhttp.responseText);
            console.log(date);
            if (date) {
                window.location.href = '/VS_Gruppentrainingsplan_war/training.jsp?name=' + name + '&date=' +
                    date + '&groupname=' + groupname;
            } else {
                alert("Das Training konnte nicht gestartet werden.");
            }
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            alert("Das Training konnte nicht gestartet werden.");
            console.log(xmlhttp)
        }
    };
    xmlhttp.send(null);
}

function goToHomescreen() {
    window.location.href = "/VS_Gruppentrainingsplan_war/groups.jsp";
}