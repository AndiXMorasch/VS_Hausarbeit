package com.vs_project.vs_gruppentrainingsplan.servlets;

import com.vs_project.vs_gruppentrainingsplan.database.ExerciseRepository;
import com.vs_project.vs_gruppentrainingsplan.models.Exercise;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * ExerciseServlet zur Verwaltung des Exercise-Models. Diese Klasse
 * kommuniziert mit der Datenbank ueber alle Einträge die Uebungen
 * betreffen. Zudem versorgt das Servlet den Nutzer mit angefragten
 * Informationen zu Uebungen und legt auf Anfrage auch neue Uebungen an.
 */
@WebServlet(name = "exerciseServlet", value = "/exercises")
public class ExerciseServlet extends HttpServlet {
    /**
     * Die doGet Methode der UserServlet Klasse unterscheidet ihren
     * Zweck auf Basis des uebergebenen Parameters aus dem Request.
     * Ist der Parameter "search" gesetzt, so filtert das Servlet die vom Client eingegebene
     * Teilzeichenfolge beim Suchen einer Uebung und schreibt die dazu in der Datenbank
     * gefundenen DTO Elemente in die Response. Ist "search" hingegen leer, so antwortet
     * das Servlet standardmässig mit allen in der Datenbank vorhanden Uebungseinträgen
     * als JSON-String.
     *
     * @param request  Client HTTP request
     * @param response Server HTTP response
     * @throws IOException IOException ausgeloest durch den PrintWriter
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ExerciseRepository repository = ExerciseRepository.getInstance();
        String search = request.getParameter("search");
        Collection<Exercise> exercises;
        if (search != null) {
            exercises = repository.searchExercises(search);
        } else {
            exercises = repository.getExercises();
        }
        PrintWriter writer = response.getWriter();
        writer.println(this.mapExercisesToJSON(exercises.stream().map(com.vs_project.
                vs_gruppentrainingsplan.helper.ExerciseDTO::new).collect(Collectors.toList())));
        writer.close();
    }

    private String mapExercisesToJSON(Collection<com.vs_project.vs_gruppentrainingsplan.helper.ExerciseDTO> exercises) {
        JSONArray array = new JSONArray(exercises);
        return array.toString();
    }

    private String mapExercisesToJSON(com.vs_project.vs_gruppentrainingsplan.helper.ExerciseDTO exercise) {
        JSONObject jsonObject = new JSONObject(exercise);
        return jsonObject.toString();
    }

    /**
     * Die doPost Methode der ExerciseServlet Klasse legt eine neue Uebung
     * in der Datenbank auf Basis der uebergebenen Parameter aus dem Request an.
     *
     * @param req  Client HTTP request
     * @param resp Server HTTP response
     * @throws IOException IOException ausgeloest durch den PrintWriter
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String exerciseName = req.getParameter("newExercise");
        if (!exerciseName.isBlank() && !exerciseName.isEmpty()) {
            ExerciseRepository repository = ExerciseRepository.getInstance();
            try {
                repository.addExercise(new Exercise(exerciseName));

                Collection<Exercise> exercises;
                exercises = repository.getExercises();

                PrintWriter writer = resp.getWriter();
                writer.println(this.mapExercisesToJSON(exercises.stream().map(com.vs_project.vs_gruppentrainingsplan.helper.ExerciseDTO::new).collect(Collectors.toList())));
                writer.close();
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
