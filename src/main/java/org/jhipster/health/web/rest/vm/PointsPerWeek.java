package org.jhipster.health.web.rest.vm;

import java.time.LocalDate;

/**
 * Created by overcode on 11/24/16.
 */
public class PointsPerWeek
{
    private LocalDate week;
    private Integer points;
    public PointsPerWeek(LocalDate week, Integer points) {
        this.week = week;
        this.points = points;
    }
    public Integer getPoints() {
        return points;
    }
    public void setPoints(Integer points) {
        this.points = points;
    }

    public LocalDate getWeek() {
        return week;
    }
    public void setWeek(LocalDate week) {
        this.week = week;
    }
    @Override
    public String toString() {
        return "PointsThisWeek{" +
            "points=" + points +
            ", week=" + week +
            '}';
    }
}

