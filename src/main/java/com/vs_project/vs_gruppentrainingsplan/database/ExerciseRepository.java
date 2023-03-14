package com.vs_project.vs_gruppentrainingsplan.database;

import com.vs_project.vs_gruppentrainingsplan.models.Exercise;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class ExerciseRepository {
    private static ExerciseRepository instance = null;

    private final Database database;

    public static ExerciseRepository getInstance() {
        if (instance == null) {
            instance = new ExerciseRepository();
        }
        return instance;
    }

    private ExerciseRepository() {
        this.database = Database.getInstance();
    }

    public Collection<Exercise> getExercises() {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    SELECT exercisename
                    FROM exercises
                    ORDER BY exercisename
                """)) {
            ResultSet resultSet = statement.executeQuery();
            return this.mapResultSetToExercises(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<Exercise> searchExercises(String search) {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    SELECT exercisename
                    FROM exercises
                    WHERE exercisename LIKE ?
                """)) {
            statement.setString(1, "%" + search + "%");
            ResultSet resultSet = statement.executeQuery();
            return this.mapResultSetToExercises(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addExercise(Exercise exercise) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  INSERT INTO exercises(exercisename)
                  VALUES (?)
                """)) {
            statement.setString(1, exercise.getExerciseName());
            statement.executeUpdate();
            System.out.println("Exercise " + exercise.getExerciseName() + " successfully added.");
        }
    }

    public boolean isExerciseExisting(String exerciseName) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                SELECT exercisename
                FROM exercises
                WHERE exercisename=?
                """)) {
            statement.setString(1, exerciseName);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public void deleteAllExerciseEntries() throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    DELETE FROM exercises
                """)) {
            statement.executeUpdate();
        }
        System.out.println("Exercise entries successfully deleted.");
    }

    public void deleteSpecificExercise(String exerciseName) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    DELETE FROM exercises WHERE exercisename=?
                """)) {
            statement.setString(1, exerciseName);
            statement.executeUpdate();
        }
        System.out.println("Exercise " + exerciseName + " successfully deleted.");
    }

    private Collection<Exercise> mapResultSetToExercises(ResultSet resultSet) throws SQLException {
        Collection<Exercise> exercises = new ArrayList<>();
        while (resultSet.next()) {
            String exercisename = resultSet.getString("exercisename");
            exercises.add(new Exercise(exercisename));
        }
        return exercises;
    }
}
