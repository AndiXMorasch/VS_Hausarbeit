package com.vs_project.vs_gruppentrainingsplan.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserToGroupRepository {
    private static UserToGroupRepository instance = null;

    private final Database database;

    public static UserToGroupRepository getInstance() {
        if (instance == null) {
            instance = new UserToGroupRepository();
        }
        return instance;
    }

    private UserToGroupRepository() {
        this.database = Database.getInstance();
    }


    public void addUserToGroup(String username, String groupname) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  INSERT INTO user_to_group(username, groupname)
                  VALUES (?, ?)
                """)) {
            statement.setString(1, username);
            statement.setString(2, groupname);
            statement.executeUpdate();
            System.out.println("User " + username + " successfully added to " + groupname + ".");
        }
    }

    public void deleteUserFromGroup(String username, String groupname) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  DELETE FROM user_to_group WHERE username=? AND groupname=?
                """)) {
            statement.setString(1, username);
            statement.setString(2, groupname);
            statement.executeUpdate();
            System.out.println("User " + username + " successfully deleted from " + groupname + ".");
        }
    }

    public List<String> getAllGroupsFromUser(String username) throws SQLException {
        List<String> allUserGroups = new ArrayList<>();
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                SELECT groupname
                FROM user_to_group
                WHERE username=?
                """)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String groupname = resultSet.getString("groupname");
                allUserGroups.add(groupname);
            }
            return allUserGroups;
        }
    }

    public List<String> getAllUsersFromGroup(String groupname) throws SQLException {
        List<String> allGroupUsers = new ArrayList<>();
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                SELECT username
                FROM user_to_group
                WHERE groupname=?
                """)) {
            statement.setString(1, groupname);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                allGroupUsers.add(username);
            }
            return allGroupUsers;
        }
    }

    public void deleteAllUsersFromSpecificGroup(String groupname) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  DELETE FROM user_to_group WHERE groupname=?
                """)) {
            statement.setString(1, groupname);
            statement.executeUpdate();
            System.out.println("All Users successfully deleted from " + groupname + ".");
        }
    }

    public void deleteAllGroupsFromSpecificUser(String username) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  DELETE FROM user_to_group WHERE username=?
                """)) {
            statement.setString(1, username);
            statement.executeUpdate();
            System.out.println("All groups successfully deleted from " + username + ".");
        }
    }
}
