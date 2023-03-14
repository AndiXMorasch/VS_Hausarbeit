package com.vs_project.vs_gruppentrainingsplan.helper;

import com.vs_project.vs_gruppentrainingsplan.models.Training;
import com.vs_project.vs_gruppentrainingsplan.models.TrainingExercise;
import com.vs_project.vs_gruppentrainingsplan.models.TrainingPlan;
import com.vs_project.vs_gruppentrainingsplan.models.User;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class TrainingDTO {
    private UserDTO user;
    private TrainingPlanDTO trainingPlan;

    private Date date;
    private Collection<TrainingExerciseDTO> exercises;

    public TrainingDTO(Training training) {
        this.user = new UserDTO(training.getUser());
        this.trainingPlan = new TrainingPlanDTO(training.getTrainingPlan());
        this.date = training.getDate();
        this.exercises = training.getExercises().stream()
                .map(TrainingExerciseDTO::new)
                .collect(Collectors.toList());
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public TrainingPlanDTO getTrainingPlan() {
        return trainingPlan;
    }

    public void setTrainingPlan(TrainingPlanDTO trainingPlan) {
        this.trainingPlan = trainingPlan;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Collection<TrainingExerciseDTO> getExercises() {
        return exercises;
    }

    public void setExercises(Collection<TrainingExerciseDTO> exercises) {
        this.exercises = exercises;
    }
}
