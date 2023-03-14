package com.vs_project.vs_gruppentrainingsplan.servlets;

import com.vs_project.vs_gruppentrainingsplan.database.GroupRepository;
import com.vs_project.vs_gruppentrainingsplan.database.TrainingPlanRepository;
import com.vs_project.vs_gruppentrainingsplan.database.TrainingRepository;
import com.vs_project.vs_gruppentrainingsplan.database.TrainingToExerciseRepository;
import com.vs_project.vs_gruppentrainingsplan.helper.LeaderBoardUpdateDTO;
import com.vs_project.vs_gruppentrainingsplan.helper.TrainingDTO;
import com.vs_project.vs_gruppentrainingsplan.models.Group;
import com.vs_project.vs_gruppentrainingsplan.models.Training;
import com.vs_project.vs_gruppentrainingsplan.models.TrainingPlan;
import com.vs_project.vs_gruppentrainingsplan.models.User;
import com.vs_project.vs_gruppentrainingsplan.socket.GroupSocket;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * TrainingServlet zur Verwaltung des Training-Models. Diese Klasse
 * kommuniziert mit der Datenbank ueber alle Eintraege, die das Training
 * betreffen. Zudem versorgt das Servlet den Nutzer mit angefragten
 * Informationen zu Trainings.
 */
@WebServlet(name = "trainingServlet", value = "/TrainingServer")
public class TrainingServlet extends HttpServlet {

    /**
     * Die doGet Methode der TrainingServlet Klasse unterscheidet ihren
     * Zweck auf Basis des uebergebenen Parameters aus dem Request.
     * Wird kein "name" uebergeben, so antwortet das Servlet mit
     * einem JSON-String bestehend aus allen TrainingDTOs. Diese basieren auf
     * dem Nutzernamen aus dem uebergebenen Parameter "user" des Requests.
     * Wird "name" aber kein "date" uebergeben wird ein neues Training in
     * der Datenbank angelegt. Das Servlet sendet daraufhin eine Antwort
     * mit dem Datum wann das Training gestartet wurde.
     * Sollten die Parameter "finished" sowie "exerciseName" nicht gesetzt sein,
     * aber "name" und "date" sind gesetzt, dann antwortet das Servlet mit einem
     * JSON-String bestehend aus einem TrainingDTO.
     * Wenn alle vier Parameter gesetzt sind, dann wird der Trainingsstatus
     * aktualisiert und ein true zurueckgegeben.
     *
     * @param request  Client HTTP request
     * @param response Server HTTP response
     * @throws IOException IOException ausgeloest durch den PrintWriter
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TrainingPlanRepository trainingPlanRepository = TrainingPlanRepository.getInstance();
        TrainingRepository trainingRepository = TrainingRepository.getInstance();
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            response.sendError(401);
            return;
        }
        String name = request.getParameter("name");
        String dateString = request.getParameter("date");
        String finishedString = request.getParameter("finished");
        String exerciseName = request.getParameter("exercise");
        if (name == null) {
            processGetAllTrainingsForUser(response, trainingRepository, user.getUsername());
            return;
        }
        TrainingPlan trainingPlan;
        try {
            trainingPlan = trainingPlanRepository.getSpecificTrainingPlan(name);
        } catch (SQLException e) {
            response.sendError(400);
            return;
        }
        if (dateString == null) {
            processStartNewTraining(response, trainingPlan, user);
        } else if (finishedString == null || exerciseName == null) {
            try {
                Date date = getDateFromString(dateString);
                processGetTraining(response, trainingRepository, name, user, date);
            } catch (ParseException e) {
                response.sendError(400);
            }
        } else {
            try {
                Date date = getDateFromString(dateString);
                boolean finished = Boolean.parseBoolean(finishedString);
                processUpdateExercise(response, name, user, date, exerciseName, finished);
                sendUpdate(user, date);
            } catch (ParseException e) {
                response.sendError(400);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static void sendUpdate(User user, Date date) throws SQLException, IOException {
        TrainingToExerciseRepository repository = TrainingToExerciseRepository.getInstance();
        int finishedExercisesAllTime = repository.getFinishedExercisesByUser(user.getUsername());
        int finishedExercises = repository.getFinishedExercisesByUserAndDate(user.getUsername(), date);
        int daysBeforeToday = (int) Duration.between(date.toInstant(), Instant.now()).toDays();
        LeaderBoardUpdateDTO leaderBoardUpdateDTO = new LeaderBoardUpdateDTO(user.getUsername(), finishedExercisesAllTime, finishedExercises, daysBeforeToday);
        Collection<Group> allGroupsForUser = GroupRepository.getInstance().getAllGroupsForUser(user.getUsername());
        GroupSocket.broadcast(leaderBoardUpdateDTO, allGroupsForUser.stream().map(Group::getGroupname).collect(Collectors.toList()));
    }

    private void processUpdateExercise(HttpServletResponse response, String name, User user, Date date, String exerciseName, boolean finished) throws IOException {
        TrainingToExerciseRepository repository = TrainingToExerciseRepository.getInstance();
        repository.updateFinishedForExercise(user.getUsername(), name, exerciseName, date, finished);
        PrintWriter writer = response.getWriter();
        writer.println(true);
        writer.close();
    }

    private static Date getDateFromString(String dateString) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.parse(dateString);
    }

    private void processGetAllTrainingsForUser(HttpServletResponse response, TrainingRepository repository, String username) throws IOException {
        Collection<Training> trainings = repository.getTrainingsForUser(username);

        PrintWriter writer = response.getWriter();
        writer.println(this.mapTrainingsToJSON(trainings.stream().map(TrainingDTO::new).collect(Collectors.toList())));
        writer.close();
    }

    private void processStartNewTraining(HttpServletResponse response, TrainingPlan trainingPlan, User user) {
        Training training = new Training(user, trainingPlan, new Date());
        TrainingRepository trainingRepository = TrainingRepository.getInstance();
        try {
            trainingRepository.addTraining(training);
            PrintWriter writer = response.getWriter();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
            writer.println('"' + dateTimeFormatter.format(training.getDate().toInstant()) + '"');
            writer.close();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processGetTraining(HttpServletResponse response, TrainingRepository repository, String name, User user, Date date) throws IOException {
        try {
            Training training = repository.getSpecificTraining(user.getUsername(), name, date);

            PrintWriter writer = response.getWriter();
            writer.println(this.mapTrainingToJSON(new TrainingDTO(training)));
            writer.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String mapTrainingsToJSON(Collection<TrainingDTO> trainings) {
        JSONArray array = new JSONArray(trainings);
        return array.toString();
    }

    private String mapTrainingToJSON(TrainingDTO training) {
        JSONObject object = new JSONObject(training);
        return object.toString();
    }
}
