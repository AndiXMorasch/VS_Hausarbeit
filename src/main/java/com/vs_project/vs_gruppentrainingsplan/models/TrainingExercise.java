package com.vs_project.vs_gruppentrainingsplan.models;

public class TrainingExercise {
    private Exercise exercise;
    private boolean finished;

    public TrainingExercise(Exercise exercise, boolean finished) {
        this.exercise = exercise;
        this.finished = finished;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
