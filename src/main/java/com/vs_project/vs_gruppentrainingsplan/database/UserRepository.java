package com.vs_project.vs_gruppentrainingsplan.database;

import com.vs_project.vs_gruppentrainingsplan.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class UserRepository {
    private static UserRepository instance = null;

    private final Database database;

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    private UserRepository() {
        this.database = Database.getInstance();
    }

    public Collection<User> getUsers() {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    SELECT username, password, isAdmin
                    FROM users
                    ORDER BY username
                """)) {
            ResultSet resultSet = statement.executeQuery();
            return this.mapResultSetToUsers(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<User> searchUsers(String search) {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    SELECT username, password, isAdmin
                    FROM users
                    WHERE username LIKE ?
                """)) {
            statement.setString(1, "%" + search + "%");
            ResultSet resultSet = statement.executeQuery();
            return this.mapResultSetToUsers(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addUser(User user) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  INSERT INTO users(username, password, isAdmin)
                  VALUES (?, ?, ?)
                """)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setInt(3, (user.isAdmin()) ? 1 : 0);
            statement.executeUpdate();
            System.out.println("User " + user.getUsername() + " successfully added.");
        }
    }

    public User getSpecificUser(String username) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                SELECT username, password, isAdmin
                FROM users
                WHERE username=?
                """)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            String password = resultSet.getString("PASSWORD");
            boolean isAdmin = resultSet.getBoolean("isAdmin");
            return new User(username, password, isAdmin);
        }
    }

    public boolean isUserExisting(String username) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                SELECT username, password, isAdmin
                FROM users
                WHERE username=?
                """)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public void deleteAllUserEntries() throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    DELETE FROM users
                """)) {
            statement.executeUpdate();
        }
        System.out.println("User entries successfully deleted.");
    }

    public void deleteSpecificUser(String username) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    DELETE FROM users WHERE username=?
                """)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
        System.out.println("User " + username + " successfully deleted.");
    }

    private Collection<User> mapResultSetToUsers(ResultSet resultSet) throws SQLException {
        Collection<User> users = new ArrayList<>();
        while (resultSet.next()) {
            String name = resultSet.getString("username");
            String password = resultSet.getString("password");
            boolean isAdmin = resultSet.getBoolean("isAdmin");
            users.add(new User(name, password, isAdmin));
        }
        return users;
    }

    public Collection<User> getUsersInGroup(String groupname) {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    SELECT u.username, password, isAdmin
                    FROM users u JOIN user_to_group utg on u.username = utg.username
                    WHERE groupname = ?
                    ORDER BY username
                """)) {
            statement.setString(1, groupname);
            ResultSet resultSet = statement.executeQuery();
            return this.mapResultSetToUsers(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
