package com.vs_project.vs_gruppentrainingsplan.servlets;

import com.vs_project.vs_gruppentrainingsplan.database.ExercisesToTrainingPlanRepository;
import com.vs_project.vs_gruppentrainingsplan.database.TrainingPlanRepository;
import com.vs_project.vs_gruppentrainingsplan.helper.TrainingPlanDTO;
import com.vs_project.vs_gruppentrainingsplan.models.TrainingPlan;
import com.vs_project.vs_gruppentrainingsplan.models.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TrainingPlanServlet zur Verwaltung des TrainingPlan-Models. Diese Klasse
 * kommuniziert mit der Datenbank ueber alle Eintraege, die den TrainingPlan
 * betreffen. Zudem versorgt das Servlet den Nutzer mit angefragten
 * Informationen zu Trainingsplaenen und legt auf Anfrage auch neue
 * Trainingsplaenen an oder loescht diese.
 */
@WebServlet(name = "trainingPlanServlet", value = "/TrainingPlanServer")
public class TrainingPlanServlet extends HttpServlet {

    /**
     * Die doGet Methode der TrainingPlanServlet Klasse unterscheidet ihren
     * Zweck auf Basis des uebergebenen Parameters aus dem Request.
     * Wird der Parameter "name" uebergeben, so antwortet das Servlet mit
     * einem JSON-String bestehend aus einem TrainingPlanDTO. Der TrainingPlanDTO
     * basiert auf dem Nutzernamen aus dem uebergebenen Parameter des Requests.
     * Wird hingegen kein Parameter "name" uebergeben so antwortet das Servlet standardmaessig
     * mit einem JSON-String bestehend aus allen TrainingPlanDTOs. Diese basieren auf
     * allen existierenden Trainingsplaenen aus der Datenbank.
     *
     * @param request  Client HTTP request
     * @param response Server HTTP response
     * @throws IOException IOException ausgeloest durch den PrintWriter
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TrainingPlanRepository trainingPlanRepository = TrainingPlanRepository.getInstance();
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            response.sendError(401);
            return;
        }
        String name = request.getParameter("name");
        if (name != null) {
            processGetTrainingPlanByName(response, trainingPlanRepository, name);
        } else {
            processGetAllTrainingPlans(response, trainingPlanRepository);
        }

    }

    private void processGetAllTrainingPlans(HttpServletResponse response, TrainingPlanRepository repository) throws IOException {
        try {
            Collection<TrainingPlan> trainingPlans;

            trainingPlans = repository.getAllTrainingPlans();

            PrintWriter writer = response.getWriter();
            writer.println(this.mapTrainingPlansToJSON(trainingPlans.stream().map(TrainingPlanDTO::new).collect(Collectors.toList())));
            writer.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void processGetTrainingPlanByName(HttpServletResponse response, TrainingPlanRepository repository, String name) throws IOException {
        try {
            TrainingPlan trainingPlan;

            trainingPlan = repository.getSpecificTrainingPlan(name);

            PrintWriter writer = response.getWriter();
            writer.println(this.mapTrainingPlanToJSON(new TrainingPlanDTO(trainingPlan)));
            writer.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Die doPost Methode der TrainingPlanServlet Klasse legt einen neuen Trainingsplan
     * in der Datenbank auf Basis der uebergebenen Parameter aus dem Request an.
     * Zusaetzlich werden die uebergebenen Uebungen, die dem Trainingsplan hinzugefuegt werden
     * sollen, in die Many-To-Many Verknuepfungstabelle "ExercisesToTrainingPlan" geschrieben.
     *
     * @param req  Client HTTP request
     * @param resp Server HTTP response
     * @throws IOException IOException ausgeloest durch den PrintWriter
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        String line = reader.readLine();
        StringBuilder json = new StringBuilder();
        while (line != null) {
            json.append(line);
            line = reader.readLine();
        }

        try {
            JSONObject jsonObject = new JSONObject(json.toString());

            String trainingPlanName = jsonObject.getString("name");
            List<String> exercises = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("exercisename");

            SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
            String dateFrom = jsonObject.getString("validFrom");

            Date validFrom = sm.parse(dateFrom);

            String dateUntil = jsonObject.getString("validUntil");
            Date validUntil = sm.parse(dateUntil);

            for (int i = 0; i < jsonArray.length(); ++i) {
                exercises.add(jsonArray.get(i).toString());
            }

            TrainingPlanRepository trainingPlanRepository = TrainingPlanRepository.getInstance();
            ExercisesToTrainingPlanRepository exercisesToTrainingPlanRepository = ExercisesToTrainingPlanRepository.getInstance();

            try {
                trainingPlanRepository.TrainingPlan(new TrainingPlan(trainingPlanName, validFrom, validUntil));
            } catch (SQLException e) {
                resp.getWriter().println(false);
                return;
            }

            exercises.forEach(e -> {
                try {
                    exercisesToTrainingPlanRepository.addExerciseToTrainingPlan(e, trainingPlanName);
                } catch (SQLException sqle) {
                    throw new RuntimeException(sqle);
                }
            });
            System.out.println(trainingPlanName);
            exercises.forEach(System.out::println);

            resp.getWriter().println(true);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String mapTrainingPlansToJSON(Collection<TrainingPlanDTO> trainingPlans) {
        JSONArray array = new JSONArray(trainingPlans);
        return array.toString();
    }

    private String mapTrainingPlanToJSON(TrainingPlanDTO trainingPlan) {
        JSONObject object = new JSONObject(trainingPlan);
        return object.toString();
    }

    /**
     * Die doDelete Methode der TrainingPlanServlet Klasse loescht einen Trainingsplan
     * basierend auf dem uebergebenen Parameter "plan-name" aus der Datenbank.
     * Gleichzeitig werden saemtliche Many-To-Many Verknuepfungen von anderen
     * Tabellen, die diesen Trainingsplan nutzen ebenfalls geloescht.
     *
     * @param req  Client HTTP request
     * @param resp Server HTTP response
     */
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String planName = req.getParameter("plan-name");
        TrainingPlanRepository trainingPlanRepository = TrainingPlanRepository.getInstance();
        ExercisesToTrainingPlanRepository exercisesToTrainingPlanRepository = ExercisesToTrainingPlanRepository.getInstance();

        try {
            exercisesToTrainingPlanRepository.deleteAllExercisesFromSpecificTrainingPlan(planName);
            trainingPlanRepository.deleteSpecificTrainingPlan(planName);
            resp.getWriter().println(true);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
