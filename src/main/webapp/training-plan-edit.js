let selectedExercises = new Set();
let exercises;

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

function filterExercises(search) {
    let xmlhttp = getXMLHttpRequest();
    if (search === "") {
        xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/exercises', true);
    } else {
        xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/exercises?search=' + search, true);
    }
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {
            $('exercises').innerHTML = 'Übungen werden geladen ...';
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            $('exercises').innerHTML = '';
            console.log("response ist fertig: " + xmlhttp.responseText);
            exercises = JSON.parse(xmlhttp.responseText);
            updateExercises();
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            console.log(xmlhttp)
        }
    };
    xmlhttp.send(null);
}

function updateExercises() {
    let container = $("exercises");
    let html = "";
    exercises.filter(e => !selectedExercises.has(e.exerciseName))
        .forEach(e => {
            html += '<div class="exercise clickable" onclick="selectExercise(\'' + e.exerciseName + '\');">';
            html += '<span class="exercisename">' + e.exerciseName + ' </span> <span class="hint">hinzufügen</span>';
            html += '</div>';
        })
    container.innerHTML = html;
}

function selectExercise(exercise) {
    console.log(exercise);
    selectedExercises.add(exercise);
    displaySelectedExercises();
    updateExercises();
}

function unselectExercise(exercise) {
    selectedExercises.delete(exercise);
    displaySelectedExercises();
    updateExercises();
}

function displaySelectedExercises() {
    let container = $("selected-exercises");
    let html = "";
    if (selectedExercises.size === 0) {
        html = "<p>Noch keine Übung ausgewählt.</p>"
    }
    selectedExercises.forEach(e => {
        html += '<div class="exercise">';
        html += '<span class="exercisename">' + e + ' </span>';
        html += '<button class="secondary" onclick="unselectExercise(\'' + e + '\');">x</button>';
        html += '</div>';
    })
    container.innerHTML = html;
}

function onSaveExercise() {
    let xmlhttp = getXMLHttpRequest();
    let nameOfExercise = $("create-exercise").value;

    xmlhttp.open("POST", '/VS_Gruppentrainingsplan_war/exercises?newExercise=' + nameOfExercise, true);

    if (nameOfExercise !== "") {
        xmlhttp.open("POST", '/VS_Gruppentrainingsplan_war/exercises?newExercise=' + nameOfExercise, true);
    }
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {

        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            console.log("response ist fertig: " + xmlhttp.responseText);
            exercises = JSON.parse(xmlhttp.responseText);
            updateExercises();
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            alert("Die Übung konnte nicht angelegt werden, möglicherweise existiert bereits eine solche Übung.")
            console.log(xmlhttp)
        }
    };
    xmlhttp.send(null);
}

function onSave() {
    let trainingPlanName = $("training-plan-name");
    if (trainingPlanName.value.trim() === "") {
        alert("Sie dürfen keinen leeren Namen als Trainingsplannamen eingeben.");
        return;
    } else if (selectedExercises.size === 0) {
        alert("Sie müssen mindestens eine Übung auswählen.");
        return;
    }
    let validFrom = $("validFrom");
    let validUntil = $("validUntil");
    let trainingPlan = {
        name: trainingPlanName.value,
        exercisename: [...selectedExercises],
        validFrom: validFrom.value,
        validUntil: validUntil.value
    }
    console.log(trainingPlan);
    sendSave(trainingPlan);
}

function sendSave(trainingPlan) {
    let xmlhttp = getXMLHttpRequest();
    xmlhttp.open("POST", '/VS_Gruppentrainingsplan_war/TrainingPlanServer', true);
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            console.log("response ist fertig: " + xmlhttp.responseText);
            let success = JSON.parse(xmlhttp.responseText);
            if (success === true) {
                window.location.href = "/VS_Gruppentrainingsplan_war/groups.jsp"
            } else {
                alert("Der Trainingsplan konnte nicht gespeichert werden. Der Name ist möglicherweise schon vergeben.")
            }
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            alert("Die Trainingsplan konnte nicht gespeichert werden. Der Name ist möglicherweise schon vergeben.")
            console.log(xmlhttp)
        }
    };
    xmlhttp.send(JSON.stringify(trainingPlan));
}

function goToHomescreen() {
    window.location.href = "/VS_Gruppentrainingsplan_war/groups.jsp";
}