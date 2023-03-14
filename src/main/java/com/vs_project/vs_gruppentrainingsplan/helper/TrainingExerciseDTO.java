package com.vs_project.vs_gruppentrainingsplan.helper;

import com.vs_project.vs_gruppentrainingsplan.models.Exercise;
import com.vs_project.vs_gruppentrainingsplan.models.TrainingExercise;

public class TrainingExerciseDTO {
    private ExerciseDTO exercise;
    private boolean finished;

    public TrainingExerciseDTO(TrainingExercise trainingExercise) {
        this.exercise = new ExerciseDTO(trainingExercise.getExercise());
        this.finished = trainingExercise.isFinished();
    }

    public ExerciseDTO getExercise() {
        return exercise;
    }

    public void setExercise(ExerciseDTO exercise) {
        this.exercise = exercise;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
