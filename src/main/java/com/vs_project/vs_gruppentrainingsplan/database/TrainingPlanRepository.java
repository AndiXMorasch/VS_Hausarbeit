package com.vs_project.vs_gruppentrainingsplan.database;

import com.vs_project.vs_gruppentrainingsplan.models.TrainingPlan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class TrainingPlanRepository {
    private static TrainingPlanRepository instance = null;

    private final Database database;

    public static TrainingPlanRepository getInstance() {
        if (instance == null) {
            instance = new TrainingPlanRepository();
        }
        return instance;
    }

    private TrainingPlanRepository() {
        this.database = Database.getInstance();
    }

    public Collection<TrainingPlan> getAllTrainingPlans() throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                SELECT planname, validFrom, validUntil
                FROM training_plans
                ORDER BY validFrom DESC, planname
                """)) {
            ResultSet resultSet = statement.executeQuery();

            return mapResultSetToTrainingPlans(resultSet);
        }
    }

    public void TrainingPlan(TrainingPlan trainingPlan) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                  INSERT INTO training_plans(planname, validFrom, validUntil)
                  VALUES (?, ?, ?)
                """)) {
            statement.setString(1, trainingPlan.getTrainingPlanName());
            statement.setDate(2, new java.sql.Date(trainingPlan.getValidFrom().getTime()));
            statement.setDate(3, new java.sql.Date(trainingPlan.getValidUntil().getTime()));
            statement.executeUpdate();
            System.out.println("Training plan " + trainingPlan.getTrainingPlanName() + " successfully added.");
        }
    }

    public TrainingPlan getSpecificTrainingPlan(String planName) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                SELECT planname, validFrom, validUntil
                FROM training_plans
                WHERE planname=?
                """)) {
            statement.setString(1, planName);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            return mapResultSetLineToTrainingPlan(resultSet);
        }
    }

    private Collection<TrainingPlan> mapResultSetToTrainingPlans(ResultSet resultSet) throws SQLException {
        Collection<TrainingPlan> trainingPlans = new ArrayList<>();
        while (resultSet.next()) {
            TrainingPlan trainingPlan = mapResultSetLineToTrainingPlan(resultSet);
            trainingPlans.add(trainingPlan);
        }
        return trainingPlans;
    }

    private static TrainingPlan mapResultSetLineToTrainingPlan(ResultSet resultSet) throws SQLException {
        Date validFrom = resultSet.getDate("validFrom");
        Date validUntil = resultSet.getDate("validUntil");
        String planName = resultSet.getString("planname");
        TrainingPlan trainingPlan = new TrainingPlan(planName, validFrom, validUntil);
        trainingPlan.setExerciseList(ExercisesToTrainingPlanRepository.getInstance().getAllExercisesFromTrainingPlan(planName));
        return trainingPlan;
    }

    public boolean isTrainingPlanExisting(String planName) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                SELECT planname
                FROM training_plans
                WHERE planname=?
                """)) {
            statement.setString(1, planName);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public void deleteAllTrainingPlans() throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    DELETE FROM training_plans
                """)) {
            statement.executeUpdate();
        }
        System.out.println("All training plans successfully deleted.");
    }

    public void deleteSpecificTrainingPlan(String planName) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("""
                    DELETE FROM training_plans WHERE planname=?
                """)) {
            statement.setString(1, planName);
            statement.executeUpdate();
        }
        System.out.println("Training plan " + planName + " successfully deleted.");
    }
}
