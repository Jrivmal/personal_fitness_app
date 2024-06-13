package com.example.application.data;

import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
public class FitnessDetail extends AbstractEntity {

    private LocalDate dates;
    private Integer moves;
    private Integer exercise;
    private Integer stand;
    private Integer steps;
    private Integer calories;

    public LocalDate getDates() {
        return dates;
    }
    public void setDates(LocalDate dates) {
        this.dates = dates;
    }
    public Integer getMoves() {
        return moves;
    }
    public void setMoves(Integer moves) {
        this.moves = moves;
    }
    public Integer getExercise() {
        return exercise;
    }
    public void setExercise(Integer exercise) {
        this.exercise = exercise;
    }
    public Integer getStand() {
        return stand;
    }
    public void setStand(Integer stand) {
        this.stand = stand;
    }
    public Integer getSteps() {
        return steps;
    }
    public void setSteps(Integer steps) {
        this.steps = steps;
    }
    public Integer getCalories() {
        return calories;
    }
    public void setCalories(Integer calories) {
        this.calories = calories;
    }

}
