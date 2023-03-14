package com.vs_project.vs_gruppentrainingsplan.servlets;

import com.vs_project.vs_gruppentrainingsplan.database.UserRepository;
import com.vs_project.vs_gruppentrainingsplan.models.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * SessionLoginServlet zur Verwaltung einer Session und Pruefung ob der
 * Nutzer sich einloggen darf oder nicht. Diese Klasse
 * kommuniziert mit der Datenbank ueber alle Einträge die Nutzer
 * betreffen. Primaer ist dabei der Username sowie das gehashte
 * Passwort von Relevanz.
 */
@WebServlet(name = "sessionLoginServlet", value = "/LoginServer")
public class SessionLoginServlet extends HttpServlet {

    /**
     * Die doGet Methode der SessionLoginServlet Klasse antwortet ueber boolean
     * Wert darueber, ob sich der Nutzer erfolgreich angemeldet hat oder nicht.
     * Hat sich der Nutzer erfolgreich angemeldet wird zudem eine Session fuer diesen
     * erstellt. Falls der Request einen Parameter "logout" besitzt, wird die jeweilige
     * Session beendet und der Nutzer wird zum LoginScreen geleitet.
     *
     * @param request  Client HTTP request
     * @param response Server HTTP response
     * @throws IOException IOException ausgeloest durch den PrintWriter
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String logout = request.getParameter("logout");
        if (logout != null) {
            System.out.println("Nutzer wird ausgeloggt & Session wird geschlossen.");
            request.getSession().invalidate();
            PrintWriter out = response.getWriter();
            out.println(true);
        } else {
            try {
                boolean loginSuccessful = processLoginRequest(request, response);
                response.setContentType("text/html;charset=UTF-8");
                System.out.println("Login genehmigt? -> " + loginSuccessful);
                PrintWriter out = response.getWriter();
                out.println(loginSuccessful);
            } catch (SQLException | ClassNotFoundException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean processLoginRequest(HttpServletRequest req, HttpServletResponse res) throws IOException, SQLException, ClassNotFoundException, NoSuchAlgorithmException {
        String enteredUsername = req.getParameterValues("username")[0];
        String enteredPassword = hashPassword(req.getParameterValues("password")[0]);

        // Prüfen ob solcher Nutzer überhaupt existiert
        boolean isUserExisting = UserRepository.getInstance().isUserExisting(enteredUsername);
        if (!isUserExisting) {
            System.out.println("So ein Nutzer existiert nicht!");
            return false;
        }

        // Prüfen, ob eingegebenes Passwort gleich dem DB-Passwort
        User user = UserRepository.getInstance().getSpecificUser(enteredUsername);
        boolean validation = enteredPassword.equals(user.getPassword());
        if (!validation) {
            System.out.println("Das eingegebene Passwort ist falsch!");
            return false;
        }

        res.setContentType("text/html;charset=UTF-8");

        // Neue Session anlegen
        HttpSession session = req.getSession(true);
        // Objekt ‚name‘ in Session ablegen
        session.setAttribute("user", user);
        System.out.println("Session für " + enteredUsername + " erfolgreich angelegt.");
        return true;
    }

    // Quelle: https://www.baeldung.com/sha-256-hashing-java
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (int i = 0; i < encodedHash.length; i++) {
            String hex = Integer.toHexString(0xff & encodedHash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}