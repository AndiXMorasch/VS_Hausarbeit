package com.vs_project.vs_gruppentrainingsplan.database;

import com.vs_project.vs_gruppentrainingsplan.models.Exercise;
import com.vs_project.vs_gruppentrainingsplan.models.TrainingExercise;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class TrainingToExerciseRepository {
    private static TrainingToExerciseRepository instance = null;

    private final Database database;

    public static TrainingToExerciseRepository getInstance() {
        if (instance == null) {
            instance = new TrainingToExerciseRepository();
        }
        return instance;
    }

    private TrainingToExerciseRepository() {
        this.database = Database.getInstance();
    }

    public int getFinishedExercisesByUser(String username) {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    SELECT COUNT(exercisename) number
                    FROM training_to_exercise
                    WHERE username = ? AND is_finished= ?
                """)) {
            statement.setString(1, username);
            statement.setBoolean(2, true);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return 0;
            }
            return resultSet.getInt("number");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getFinishedExercisesByUserAndDate(String username, Date date) {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    SELECT COUNT(exercisename) number
                    FROM training_to_exercise
                    WHERE username = ? AND is_finished= ? AND date= ?
                """)) {
            statement.setString(1, username);
            statement.setBoolean(2, true);
            statement.setDate(3, new java.sql.Date(date.getTime()));
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return 0;
            }
            return resultSet.getInt("number");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateFinishedForExercise(String username, String planName,
                                          String exerciseName, Date date, boolean finished) {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  UPDATE training_to_exercise
                  SET is_finished = ?
                  WHERE username = ? AND exercisename = ? AND planname = ? AND date = ?
                """)) {
            statement.setBoolean(1, finished);
            statement.setString(2, username);
            statement.setString(3, exerciseName);
            statement.setString(4, planName);
            statement.setDate(5, new java.sql.Date(date.getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<TrainingExercise> mapResultSetToExercises(ResultSet resultSet) throws SQLException {
        Collection<TrainingExercise> exercises = new ArrayList<>();
        while (resultSet.next()) {
            boolean isFinished = resultSet.getBoolean("is_finished");
            Exercise exercise = new Exercise(resultSet.getString("exercisename"));
            exercises.add(new TrainingExercise(exercise, isFinished));
        }
        return exercises;
    }
}
