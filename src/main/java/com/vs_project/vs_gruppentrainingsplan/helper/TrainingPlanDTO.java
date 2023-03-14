package com.vs_project.vs_gruppentrainingsplan.helper;

import com.vs_project.vs_gruppentrainingsplan.models.TrainingPlan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrainingPlanDTO {
    private String name;
    private Date validFrom;
    private Date validUntil;
    private List<ExerciseDTO> exercises = new ArrayList<>();

    public TrainingPlanDTO(TrainingPlan trainingPlan) {
        this.name = trainingPlan.getTrainingPlanName();
        this.validFrom = trainingPlan.getValidFrom();
        this.validUntil = trainingPlan.getValidUntil();
        trainingPlan.getExerciseList().stream()
                .map(ExerciseDTO::new)
                .forEach(this.exercises::add);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }

    public List<ExerciseDTO> getExercises() {
        return exercises;
    }

    public void setExercises(List<ExerciseDTO> exercises) {
        this.exercises = exercises;
    }

}
