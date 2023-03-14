package com.vs_project.vs_gruppentrainingsplan.helper;

import java.util.Date;

public class UserWorkoutOverviewDTO {
    private String username;
    private Date date;
    private int exercisesMade;

    public UserWorkoutOverviewDTO(String username, Date date, int exercisesMade) {
        this.username = username;
        this.date = date;
        this.exercisesMade = exercisesMade;
    }


    public String getUsername() {
        return username;
    }

    public int getExercisesMade() {
        return exercisesMade;
    }

    public Date getDate() {
        return date;
    }
}
