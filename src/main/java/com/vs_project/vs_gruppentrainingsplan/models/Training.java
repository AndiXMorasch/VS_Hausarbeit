package com.vs_project.vs_gruppentrainingsplan.models;

import java.util.Collection;
import java.util.Date;

public class Training {
    private User user;
    private TrainingPlan trainingPlan;

    private Date date;
    private Collection<TrainingExercise> exercises;

    public Training(User user, TrainingPlan trainingPlan, Date date) {
        this.user = user;
        this.trainingPlan = trainingPlan;
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TrainingPlan getTrainingPlan() {
        return trainingPlan;
    }

    public void setTrainingPlan(TrainingPlan trainingPlan) {
        this.trainingPlan = trainingPlan;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Collection<TrainingExercise> getExercises() {
        return exercises;
    }

    public void setExercises(Collection<TrainingExercise> exercises) {
        this.exercises = exercises;
    }
}
