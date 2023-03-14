package com.vs_project.vs_gruppentrainingsplan.servlets;

import com.vs_project.vs_gruppentrainingsplan.database.UserRepository;
import com.vs_project.vs_gruppentrainingsplan.helper.UserDTO;
import com.vs_project.vs_gruppentrainingsplan.models.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * UserServlet zur Verwaltung des User-Models. Diese Klasse
 * kommuniziert mit der Datenbank ueber alle Eintraege, die den Nutzer
 * betreffen. Zudem versorgt das Servlet den Admin mit angefragten
 * Informationen zu Nutzern (Nutzername).
 */
@WebServlet(name = "userServlet", value = "/users")
public class UserServlet extends HttpServlet {

    /**
     * Die doGet Methode der UserServlet Klasse unterscheidet ihren
     * Zweck auf Basis des uebergebenen Parameters aus dem Request.
     * Ist der Parameter "search" gesetzt, so filtert das Servlet die vom Client eingegebene
     * Teilzeichenfolge beim Suchen eines Nutzers und schreibt die dazu in der Datenbank
     * gefundenen Elemente als JSON-String in die Response. Ist "search" hingegen leer, so antwortet
     * das Servlet standardmaessig mit allen in der Datenbank vorhanden Nutzereintraegen
     * als JSON-String.
     *
     * @param request  Client HTTP request
     * @param response Server HTTP response
     * @throws IOException IOException ausgeloest durch den PrintWriter
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserRepository repository = UserRepository.getInstance();
        String search = request.getParameter("search");
        Collection<User> users;
        if (search != null) {
            users = repository.searchUsers(search);
        } else {
            users = repository.getUsers();
        }
        PrintWriter writer = response.getWriter();
        writer.println(this.mapUsersToJSON(users.stream().map(UserDTO::new).collect(Collectors.toList())));
        writer.close();
    }

    private String mapUsersToJSON(Collection<UserDTO> users) {
        JSONArray array = new JSONArray(users);
        return array.toString();
    }

    private String mapUserToJSON(UserDTO user) {
        JSONObject jsonObject = new JSONObject(user);
        return jsonObject.toString();
    }
}

