package com.vs_project.vs_gruppentrainingsplan.helper;

public class LeaderBoardUpdateDTO {
    private String username;
    private int finishedExercisesAllTime;
    private int finishedExercises;
    private int daysBeforeToday;

    public LeaderBoardUpdateDTO() {
    }

    public LeaderBoardUpdateDTO(String username, int finishedExercisesAllTime, int finishedExercises, int daysBeforeToday) {
        this.username = username;
        this.finishedExercisesAllTime = finishedExercisesAllTime;
        this.finishedExercises = finishedExercises;
        this.daysBeforeToday = daysBeforeToday;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getFinishedExercisesAllTime() {
        return finishedExercisesAllTime;
    }

    public void setFinishedExercisesAllTime(int finishedExercisesAllTime) {
        this.finishedExercisesAllTime = finishedExercisesAllTime;
    }

    public int getFinishedExercises() {
        return finishedExercises;
    }

    public void setFinishedExercises(int finishedExercises) {
        this.finishedExercises = finishedExercises;
    }

    public int getDaysBeforeToday() {
        return daysBeforeToday;
    }

    public void setDaysBeforeToday(int daysBeforeToday) {
        this.daysBeforeToday = daysBeforeToday;
    }
}
