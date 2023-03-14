package com.vs_project.vs_gruppentrainingsplan.helper;

public class LeaderboardDTO {
    private String username;
    private int finishedExercises;

    public LeaderboardDTO(String username, int finishedExercises) {
        this.username = username;
        this.finishedExercises = finishedExercises;
    }

    public String getUsername() {
        return username;
    }

    public int getFinishedExercises() {
        return finishedExercises;
    }
}
