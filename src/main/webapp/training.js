let training;
let trainingPlanName = new URLSearchParams(window.location.search).get('name');
let date = new URLSearchParams(window.location.search).get('date');

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

function getTraining() {
    let xmlhttp = getXMLHttpRequest();
    xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/TrainingServer?name=' + trainingPlanName + '&date=' + date, true);
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {

        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            training = JSON.parse(xmlhttp.responseText);
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
    console.log(training);
    training.exercises.forEach(e => {
        html += '<div class="exercise">';
        html += '<span class="name">' + (++i) + '. ' + e.exercise.exerciseName + ' </span>';
        html += '<span class="spacer"></span>';
        html += '<input type="checkbox" class="checkbox finished" ' + (e.finished ? 'checked' : '') + ' onchange="exerciseChange(event, \'' + e.exercise.exerciseName + '\')"/>';
        html += '</div>';
    });
    container.innerHTML = html;
}

function exerciseChange(event, exerciseName) {
    let finished = event.srcElement.checked;
    let xmlhttp = getXMLHttpRequest();
    xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/TrainingServer?name=' + trainingPlanName + '&date=' + date + '&exercise=' + exerciseName + '&finished=' + finished, true);
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            alert("Die Änderung konnte nicht gespeichert werden.");
        }
    };
    xmlhttp.send(null);
}

function goToHomescreen() {
    window.location.href = "/VS_Gruppentrainingsplan_war/groups.jsp";
}
