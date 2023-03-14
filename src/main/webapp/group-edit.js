let selectedUsers = new Set();
let users;

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

function filterUsers(search) {
    let xmlhttp = getXMLHttpRequest();
    if (search === "") {
        xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/users', true);
    } else {
        xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/users?search=' + search, true);
    }
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {
            $('users').innerHTML = 'Nutzer werden geladen ...';
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            $('users').innerHTML = '';
            console.log("response ist fertig: " + xmlhttp.responseText);
            users = JSON.parse(xmlhttp.responseText);
            updateUsers();
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            console.log(xmlhttp)
        }
    };
    xmlhttp.send(null);
}

function updateUsers() {
    let container = $("users");
    let html = "";
    users.filter(u => !selectedUsers.has(u.username))
        .forEach(u => {
            html += '<div class="user clickable" onclick="selectUser(\'' + u.username + '\');">';
            html += '<span class="username">' + u.username + ' </span> <span class="hint">hinzufügen</span>';
            html += '</div>';
        })
    container.innerHTML = html;
}

function selectUser(user) {
    console.log(user);
    selectedUsers.add(user);
    displaySelectedUsers();
    updateUsers();
}

function unselectUser(user) {
    selectedUsers.delete(user);
    displaySelectedUsers();
    updateUsers();
}

function displaySelectedUsers() {
    let container = $("selected-users");
    let html = "";
    if (selectedUsers.size === 0) {
        html = "<p>Noch kein Teammitglied ausgewählt.</p>"
    }
    selectedUsers.forEach(u => {
        html += '<div class="user">';
        html += '<span class="username">' + u + ' </span>';
        html += '<button class="secondary" onclick="unselectUser(\'' + u + '\');">x</button>';
        html += '</div>';
    })
    container.innerHTML = html;
}

function onSave() {
    let groupName = $("group-name");
    if (groupName.value.trim() === "") {
        alert("Sie dürfen keinen leeren Namen als Gruppennamen eingeben.");
        return;
    } else if (selectedUsers.size === 0) {
        alert("Sie müssen mindestens ein Gruppenmitglied auswählen.");
        return;
    }
    let group = {
        name: groupName.value, usernames: [...selectedUsers]
    }
    console.log(group);
    sendSave(group);
}

function sendSave(group) {
    let xmlhttp = getXMLHttpRequest();
    xmlhttp.open("POST", '/VS_Gruppentrainingsplan_war/GroupServer', true);
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            console.log("response ist fertig: " + xmlhttp.responseText);
            let success = JSON.parse(xmlhttp.responseText);
            if (success === true) {
                window.location.href = "/VS_Gruppentrainingsplan_war/groups.jsp"
            } else {
                alert("Die Gruppe konnte nicht gespeichert werden. Der Name ist möglicherweise schon vergeben.")
            }
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            alert("Die Gruppe konnte nicht gespeichert werden. Der Name ist möglicherweise schon vergeben.")
            console.log(xmlhttp)
        }
    };
    xmlhttp.send(JSON.stringify(group));
}

function goToHomescreen() {
    window.location.href = "/VS_Gruppentrainingsplan_war/groups.jsp";
}
