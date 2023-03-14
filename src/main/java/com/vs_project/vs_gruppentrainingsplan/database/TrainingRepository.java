package com.vs_project.vs_gruppentrainingsplan.database;

import com.vs_project.vs_gruppentrainingsplan.models.Exercise;
import com.vs_project.vs_gruppentrainingsplan.models.Training;
import com.vs_project.vs_gruppentrainingsplan.models.TrainingExercise;
import com.vs_project.vs_gruppentrainingsplan.models.TrainingPlan;
import com.vs_project.vs_gruppentrainingsplan.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class TrainingRepository {
    private static TrainingRepository instance = null;

    private final Database database;

    public static TrainingRepository getInstance() {
        if (instance == null) {
            instance = new TrainingRepository();
        }
        return instance;
    }

    private TrainingRepository() {
        this.database = Database.getInstance();
    }

    public Collection<Training> getTrainingsForUser(String username) {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    SELECT username, date, planname
                    FROM training
                    WHERE username = ?
                    ORDER BY date desc, planname
                """)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return this.mapResultSetToTrainings(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addTraining(Training training) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  INSERT INTO training(username, planname, date)
                  VALUES (?, ?, ?)
                """)) {
            statement.setString(1, training.getUser().getUsername());
            statement.setString(2, training.getTrainingPlan().getTrainingPlanName());
            statement.setDate(3, new java.sql.Date(training.getDate().getTime()));
            statement.executeUpdate();
        }
        training.getTrainingPlan().getExerciseList().forEach(exercise -> {
            try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                      INSERT INTO training_to_exercise(username, planname, date, is_finished, exercisename)
                      VALUES (?, ?, ?, ?, ?)
                    """)) {
                statement.setString(1, training.getUser().getUsername());
                statement.setString(2, training.getTrainingPlan().getTrainingPlanName());
                statement.setDate(3, new java.sql.Date(training.getDate().getTime()));
                statement.setBoolean(4, false);
                statement.setString(5, exercise.getExerciseName());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
    }

    public Training getSpecificTraining(String username, String planName, Date date) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                SELECT username, date, planname
                FROM training
                WHERE username = ? AND planname = ? AND date = ?
                """)) {
            statement.setString(1, username);
            statement.setString(2, planName);
            statement.setDate(3, new java.sql.Date(date.getTime()));
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return this.mapResultsetLineToTraining(resultSet);
        }
    }

    private Collection<Training> mapResultSetToTrainings(ResultSet resultSet) throws SQLException {
        Collection<Training> trainings = new ArrayList<>();
        while (resultSet.next()) {
            Training training = mapResultsetLineToTraining(resultSet);
            trainings.add(training);
        }
        return trainings;
    }

    private Training mapResultsetLineToTraining(ResultSet resultSet) throws SQLException {
        User user = UserRepository.getInstance().getSpecificUser(resultSet.getString(
                "username"));
        TrainingPlan trainingPlan =
                TrainingPlanRepository.getInstance().getSpecificTrainingPlan(resultSet.getString("planname"));
        Date date = resultSet.getDate("date");
        Training training = new Training(user, trainingPlan, date);
        training.setExercises(this.getExercisesForTraining(training));
        return training;
    }

    private Collection<TrainingExercise> getExercisesForTraining(Training training) {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    SELECT is_finished, exercisename
                    FROM training_to_exercise
                    WHERE username = ? AND planname = ? AND date = ?
                    ORDER BY exercisename
                """)) {
            statement.setString(1, training.getUser().getUsername());
            statement.setString(2, training.getTrainingPlan().getTrainingPlanName());
            statement.setDate(3, new java.sql.Date(training.getDate().getTime()));
            ResultSet resultSet = statement.executeQuery();
            return this.mapResultSetToExercises(resultSet);
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
