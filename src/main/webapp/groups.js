let ownGroups;
let allGroups;
let isAdmin = false;

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

function getGroups(all = false) {
    let xmlhttp = getXMLHttpRequest();
    let container;
    if (all) {
        xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/GroupServer?all=true', true);
        container = $("all-groups");
    } else {
        xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/GroupServer', true);
        container = $("own-groups");
    }
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {

        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            console.log("response ist fertig: " + xmlhttp.responseText);
            if (all) {
                allGroups = JSON.parse(xmlhttp.responseText);
                updateGroups(container, allGroups, all);
            } else {
                ownGroups = JSON.parse(xmlhttp.responseText);
                updateGroups(container, ownGroups, all);
            }
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            console.log(xmlhttp)
        }
    };
    xmlhttp.send(null);
}

function updateGroups(container, groups, all) {
    let html = "";
    if (groups.length === 0) {
        if (all) {
            container.innerHTML = "<p> Es sind noch keine Gruppen vorhanden. </p>";
        } else {
            container.innerHTML = "<p> Du bist noch keiner Gruppe zugeordnet. </p>";
        }
        return;
    }
    groups.forEach(g => {
        html += '<div class="group clickable" onclick="window.location.href = \'/VS_Gruppentrainingsplan_war/group.jsp?name=' + g.name + '\'">';
        html += '<span class="name">' + g.name + ' </span>';
        html += '<span class="spacer"></span>';
        html += '<span class="hint">' + g.users.length + ' Teilnehmer</span>';
        if (isAdmin) {
            html += '<button class="secondary delete" onclick="deleteGroup(\'' + g.name + '\'); event.stopPropagation()">x</button>';
        }
        html += '</div>';
    })
    container.innerHTML = html;
}

function deleteGroup(groupname) {
    let xmlhttp = getXMLHttpRequest();
    xmlhttp.open("DELETE", '/VS_Gruppentrainingsplan_war/GroupServer?groupname=' + groupname, true);
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {

        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            console.log("response ist fertig: " + xmlhttp.responseText);

            getGroups(false);
            getGroups(true);
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            console.log(xmlhttp)
        }
    };
    xmlhttp.send(null);
}

function goToHomescreen() {
    window.location.href = "/VS_Gruppentrainingsplan_war/groups.jsp";
}

function logout() {
    let xmlhttp = getXMLHttpRequest();
    xmlhttp.open("GET", '/VS_Gruppentrainingsplan_war/LoginServer?logout=' + true, true);
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState !== 4) {

        }
        if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
            console.log("response ist fertig: " + xmlhttp.responseText);
            window.location.href = "/VS_Gruppentrainingsplan_war/index.jsp";
        }
        if (xmlhttp.readyState === 4 && xmlhttp.status !== 200) {
            console.log(xmlhttp)
        }
    };
    xmlhttp.send(null);
}