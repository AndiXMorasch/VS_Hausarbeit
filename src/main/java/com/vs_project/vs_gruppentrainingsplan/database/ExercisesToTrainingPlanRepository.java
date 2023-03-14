package com.vs_project.vs_gruppentrainingsplan.database;

import com.vs_project.vs_gruppentrainingsplan.models.Exercise;
import com.vs_project.vs_gruppentrainingsplan.models.TrainingPlan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExercisesToTrainingPlanRepository {
    private static ExercisesToTrainingPlanRepository instance = null;

    private final Database database;

    public static ExercisesToTrainingPlanRepository getInstance() {
        if (instance == null) {
            instance = new ExercisesToTrainingPlanRepository();
        }
        return instance;
    }

    private ExercisesToTrainingPlanRepository() {
        this.database = Database.getInstance();
    }

    public void addExerciseToTrainingPlan(String exerciseName, String trainingPlanName) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  INSERT INTO exercises_to_training_plan(exercisename, planname)
                  VALUES (?, ?)
                """)) {
            statement.setString(1, exerciseName);
            statement.setString(2, trainingPlanName);
            statement.executeUpdate();
            System.out.println("Exercise " + exerciseName + " successfully added to " + trainingPlanName + ".");
        }
    }

    public void deleteExerciseFromTrainingPlan(String exerciseName, String trainingPlanName) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  DELETE FROM exercises_to_training_plan WHERE exercisename=? AND planname=?
                """)) {
            statement.setString(1, exerciseName);
            statement.setString(2, trainingPlanName);
            statement.executeUpdate();
            System.out.println("Exercise " + exerciseName + " successfully deleted from " + trainingPlanName + ".");
        }
    }

    public List<Exercise> getAllExercisesFromTrainingPlan(String trainingPlanName) throws SQLException {
        List<Exercise> exerciseList = new ArrayList<>();
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                SELECT exercisename
                FROM exercises_to_training_plan
                WHERE planname=?
                ORDER BY exercisename
                """)) {
            statement.setString(1, trainingPlanName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String exercise = resultSet.getString("exercisename");
                exerciseList.add(new Exercise(exercise));
            }
            return exerciseList;
        }
    }

    public List<TrainingPlan> getAllTrainingPlansFromExercise(String exerciseName) throws SQLException {
        List<TrainingPlan> trainingPlanList = new ArrayList<>();
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                SELECT planname
                FROM exercises_to_training_plan
                WHERE exercisename=?
                """)) {
            statement.setString(1, exerciseName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String planName = resultSet.getString("planname");
                TrainingPlan trainingPlan = TrainingPlanRepository.getInstance().getSpecificTrainingPlan(planName);
                trainingPlanList.add(trainingPlan);
            }
            return trainingPlanList;
        }
    }

    public void deleteAllExercisesFromSpecificTrainingPlan(String planName) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  DELETE FROM exercises_to_training_plan WHERE planname=?
                """)) {
            statement.setString(1, planName);
            statement.executeUpdate();
            System.out.println("All Exercises successfully deleted from " + planName + ".");
        }
    }

    public void deleteAllTrainingPlansFromSpecificExercise(String exercise) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  DELETE FROM exercises_to_training_plan WHERE exercisename=?
                """)) {
            statement.setString(1, exercise);
            statement.executeUpdate();
            System.out.println("All training plans successfully deleted from " + exercise + ".");
        }
    }
}
