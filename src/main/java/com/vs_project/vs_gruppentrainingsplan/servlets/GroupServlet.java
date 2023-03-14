package com.vs_project.vs_gruppentrainingsplan.servlets;

import com.vs_project.vs_gruppentrainingsplan.database.GroupRepository;
import com.vs_project.vs_gruppentrainingsplan.database.TrainingToExerciseRepository;
import com.vs_project.vs_gruppentrainingsplan.database.UserRepository;
import com.vs_project.vs_gruppentrainingsplan.database.UserToGroupRepository;
import com.vs_project.vs_gruppentrainingsplan.helper.GroupDTO;
import com.vs_project.vs_gruppentrainingsplan.helper.LeaderboardDTO;
import com.vs_project.vs_gruppentrainingsplan.helper.UserWorkoutOverviewDTO;
import com.vs_project.vs_gruppentrainingsplan.models.Group;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * GroupServlet zur Verwaltung des Group-Models. Diese Klasse
 * kommuniziert mit der Datenbank ueber alle Eintrae
 * ge die Gruppen
 * betreffen. Zudem versorgt das Servlet den Nutzer mit angefragten
 * Informationen zu Gruppen und legt auf Anfrage auch neue Gruppen an
 * oder loescht Gruppen.
 */
@WebServlet(name = "groupServlet", value = "/GroupServer")
public class GroupServlet extends HttpServlet {

    /**
     * Die doGet Methode der GroupServlet Klasse unterscheidet ihren
     * Zweck auf Basis des uebergebenen Parameters aus dem Request.
     * Wird "name" uebergeben so antwortet das Servlet mit einem JSON-String
     * bestehend aus einem GroupDTO. Dieses basiert auf der durch "name"
     * gesuchten Gruppe. Wird zusaetzlich "leaderboardReq" uebergeben, so antwortet das
     * Servlet mit einem JSON-String bestehend aus mehreren LeaderboardDTOs.
     * Wird neben dem "name" noch ein "overviewReq" uebergeben antwortet der
     * das Servlet mit einem JSON-String bestehend aus UserWorkoutOverviewDTOs.
     * Wird keiner der genannten Parameter uebergeben antwortet das Servlet
     * standardmaessig mit einem JSON-String bestehend aus GroupDTOs. Diese basieren
     * auf allen in der Datenbank vorhandenen Gruppen.
     *
     * @param request  Client HTTP request
     * @param response Server HTTP response
     * @throws IOException IOException ausgeloest durch den PrintWriter
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GroupRepository repository = GroupRepository.getInstance();
        UserRepository userRepository = UserRepository.getInstance();
        UserToGroupRepository userToGroupRepository = UserToGroupRepository.getInstance();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendError(401);
            return;
        }
        String groupname = request.getParameter("name");
        String leaderboardReq = request.getParameter("leaderboardReq");
        String overviewReq = request.getParameter("overviewReq");
        if (groupname != null && leaderboardReq == null && overviewReq == null) {
            processGetGroupByName(request, response, repository, userRepository, user, groupname);
        } else if (leaderboardReq != null) {
            processGetExercisesByGroup(request, response, groupname, userToGroupRepository);
        } else if (overviewReq != null) {
            processGetUsersDailyExercises(request, response, groupname, userToGroupRepository);
        } else {
            processGetAllGroups(request, response, repository, userRepository, user);
        }
    }

    private void processGetUsersDailyExercises(HttpServletRequest request, HttpServletResponse response, String groupname, UserToGroupRepository userToGroupRepository) {
        Calendar cal = Calendar.getInstance();
        List<Date> lastSevenDaysList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.DAY_OF_MONTH, -6 + i);
            lastSevenDaysList.add(cal.getTime());
            System.out.println(cal.getTime());
            cal = Calendar.getInstance();
        }

        try {
            List<String> usernamesInGroup = userToGroupRepository.getAllUsersFromGroup(groupname);
            System.out.println(usernamesInGroup.size());
            TrainingToExerciseRepository trainingToExerciseRepository = TrainingToExerciseRepository.getInstance();
            PrintWriter writer = response.getWriter();
            List<UserWorkoutOverviewDTO> dailyWorkoutsPerUser = new ArrayList<>();
            for (String user : usernamesInGroup) {
                for (Date d : lastSevenDaysList) {
                    int i = trainingToExerciseRepository.getFinishedExercisesByUserAndDate(user, d);
                    dailyWorkoutsPerUser.add(new UserWorkoutOverviewDTO(user, d, i));
                    //System.out.println(user + " hat am " + d + " " + i + " Übungen absolviert.");
                }
            }
            writer.println(mapOverviewToJSON(dailyWorkoutsPerUser));
            writer.close();

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processGetExercisesByGroup(HttpServletRequest request, HttpServletResponse response, String groupname, UserToGroupRepository userToGroupRepository) {
        try {
            List<String> usernamesInGroup = userToGroupRepository.getAllUsersFromGroup(groupname);
            TrainingToExerciseRepository trainingToExerciseRepository = TrainingToExerciseRepository.getInstance();
            PrintWriter writer = response.getWriter();
            Collection<LeaderboardDTO> leaderboardDTOS = new ArrayList<>();
            for (String username : usernamesInGroup) {
                int finishedExercises = trainingToExerciseRepository.getFinishedExercisesByUser(username);
                leaderboardDTOS.add(new LeaderboardDTO(username, finishedExercises));
            }
            writer.println(this.mapLeaderboardToJSON(leaderboardDTOS));
            writer.close();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processGetGroupByName(HttpServletRequest request, HttpServletResponse response, GroupRepository repository, UserRepository userRepository, User user, String name) throws IOException {
        try {
            Group group = repository.getGroupForName(name);

            Collection<User> usersInGroup = userRepository.getUsersInGroup(group.getGroupname());
            usersInGroup.forEach(group::addGroupMember);

            if (!usersInGroup.contains(user) && !user.isAdmin()) {
                response.sendError(401);
                return;
            }

            PrintWriter writer = response.getWriter();
            writer.println(this.mapGroupToJSON(new GroupDTO(group)));
            writer.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void processGetAllGroups(HttpServletRequest request, HttpServletResponse response, GroupRepository repository, UserRepository userRepository, User user) throws IOException {
        try {
            Collection<Group> groups;
            if (!user.isAdmin() || request.getParameter("all") == null) {
                groups = repository.getAllGroupsForUser(user.getUsername());
            } else {
                groups = repository.getAllGroups();
            }
            groups.forEach(g -> {
                Collection<User> usersInGroup = userRepository.getUsersInGroup(g.getGroupname());
                usersInGroup.forEach(g::addGroupMember);
            });
            PrintWriter writer = response.getWriter();
            writer.println(this.mapGroupsToJSON(groups.stream().map(GroupDTO::new).collect(Collectors.toList())));
            writer.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Die doPost Methode der GroupServlet Klasse legt eine neue Gruppe
     * in der Datenbank auf Basis der uebergebenen Parameter aus dem Request an.
     * Zusaetzlich werden die uebergebenen Nutzer, die der Gruppe hinzugefuegt werden
     * sollen, in die Many-To-Many Verknuepfungstabelle "UserToGroup" geschrieben.
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

        JSONObject jsonObject = new JSONObject(json.toString());

        String groupname = jsonObject.getString("name");
        List<String> usernames = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("usernames");
        for (int i = 0; i < jsonArray.length(); ++i) {
            usernames.add(jsonArray.get(i).toString());
        }

        GroupRepository groupRepository = GroupRepository.getInstance();
        UserToGroupRepository userToGroupRepository = UserToGroupRepository.getInstance();

        try {
            groupRepository.addGroup(groupname);
        } catch (SQLException e) {
            resp.getWriter().println(false);
            return;
        }

        usernames.forEach(u -> {
            try {
                userToGroupRepository.addUserToGroup(u, groupname);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(groupname);
        usernames.forEach(System.out::println);

        resp.getWriter().println(true);
    }

    private String mapGroupsToJSON(Collection<GroupDTO> groups) {
        JSONArray array = new JSONArray(groups);
        return array.toString();
    }

    private String mapGroupToJSON(GroupDTO group) {
        JSONObject object = new JSONObject(group);
        return object.toString();
    }

    private String mapLeaderboardToJSON(Collection<LeaderboardDTO> leaderboards) {
        JSONArray array = new JSONArray(leaderboards);
        return array.toString();
    }

    private String mapOverviewToJSON(Collection<UserWorkoutOverviewDTO> overview) {
        JSONArray array = new JSONArray(overview);
        return array.toString();
    }

    /**
     * Die doDelete Methode der GroupServlet Klasse loescht eine Gruppe
     * basierend auf dem uebergebenen Parameter "groupname" aus der Datenbank.
     * Gleichzeitig werden saemtliche Many-To-Many Verknuepfungen von anderen
     * Tabellen die diese Gruppe nutzen ebenfalls geloescht.
     *
     * @param req  Client HTTP request
     * @param resp Server HTTP response
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String groupname = req.getParameter("groupname");
        if (groupname.isEmpty() || groupname.isBlank()) {
            return;
        }
        UserToGroupRepository userToGroupRepository = UserToGroupRepository.getInstance();
        GroupRepository groupRepository = GroupRepository.getInstance();

        try {
            userToGroupRepository.deleteAllUsersFromSpecificGroup(groupname);
            groupRepository.deleteSpecificGroup(groupname);
            resp.getWriter().println(true);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
