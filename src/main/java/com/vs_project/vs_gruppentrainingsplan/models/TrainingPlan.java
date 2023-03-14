package com.vs_project.vs_gruppentrainingsplan.models;

import java.util.Date;
import java.util.List;

public class TrainingPlan {
    private String trainingPlanName;
    private Date validFrom;
    private Date validUntil;
    private List<Exercise> exerciseList;

    public TrainingPlan(String trainingPlanName, Date validFrom, Date validUntil) {
        this.trainingPlanName = trainingPlanName;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    public void addExerciseToTrainingPlan(Exercise exercise) {
        this.exerciseList.add(exercise);
    }

    public String getTrainingPlanName() {
        return trainingPlanName;
    }

    public List<Exercise> getExerciseList() {
        return exerciseList;
    }

    public void setExerciseList(List<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

    public void setTrainingPlanName(String trainingPlanName) {
        this.trainingPlanName = trainingPlanName;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public Date getValidUntil() {
        return validUntil;
    }
}
