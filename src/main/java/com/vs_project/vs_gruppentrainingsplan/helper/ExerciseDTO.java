package com.vs_project.vs_gruppentrainingsplan.helper;

import com.vs_project.vs_gruppentrainingsplan.models.Exercise;

public class ExerciseDTO {
    private String exerciseName;

    public ExerciseDTO(Exercise exercise) {
        this.exerciseName = exercise.getExerciseName();
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }
}
