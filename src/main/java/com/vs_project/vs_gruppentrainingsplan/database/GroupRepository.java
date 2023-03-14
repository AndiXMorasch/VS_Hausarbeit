package com.vs_project.vs_gruppentrainingsplan.database;

import com.vs_project.vs_gruppentrainingsplan.models.Group;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class GroupRepository {
    private static GroupRepository instance = null;

    private final Database database;

    public static GroupRepository getInstance() {
        if (instance == null) {
            instance = new GroupRepository();
        }
        return instance;
    }

    private GroupRepository() {
        this.database = Database.getInstance();
    }

    public void addGroup(String groupname) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  INSERT INTO groups (groupname)
                  VALUES (?)
                """)) {
            statement.setString(1, groupname);
            statement.executeUpdate();
            System.out.println("Group " + groupname + " successfully added.");
        }
    }

    public Collection<Group> getAllGroupsForUser(String username) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  SELECT g.groupname
                  FROM groups g JOIN user_to_group utg on g.groupname = utg.groupname
                  WHERE username = ?
                  ORDER BY g.groupname
                """)) {
            statement.setString(1, username);
            ResultSet resultSetForGroups = statement.executeQuery();
            return this.mapResultSetToGroups(resultSetForGroups);
        }
    }

    private Collection<Group> mapResultSetToGroups(ResultSet resultSetForGroups) throws SQLException {
        Collection<Group> groups = new ArrayList<>();
        while (resultSetForGroups.next()) {
            String name = resultSetForGroups.getString("groupname");
            groups.add(new Group(name));
        }
        return groups;
    }

    public void deleteSpecificGroup(String groupname) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  DELETE FROM groups WHERE groupname=?
                """)) {
            statement.setString(1, groupname);
            statement.executeUpdate();
            System.out.println(groupname + " successfully deleted.");
        }
    }

    public Collection<Group> getAllGroups() throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    SELECT groupname
                    FROM groups
                    ORDER BY groupname
                """)) {
            ResultSet resultSet = statement.executeQuery();
            return this.mapResultSetToGroups(resultSet);
        }
    }

    public Group getGroupForName(String name) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    SELECT groupname
                    FROM groups
                    WHERE groupname = ?
                """)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Group(resultSet.getString("groupname"));
            }
            return null;
        }
    }

    public boolean isGroupExisting(String groupname) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                SELECT groupname
                FROM groups
                WHERE groupname=?
                """)) {
            statement.setString(1, groupname);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public void deleteAllGroups() throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  DELETE FROM groups
                """)) {
            statement.executeUpdate();
            System.out.println("All groups successfully deleted.");
        }
    }
}
