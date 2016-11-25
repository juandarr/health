package org.jhipster.health.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Points.
 */
@Entity
@Table(name = "points")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Points implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "exercise", nullable = false)
    private Boolean exercise;

    @NotNull
    @Column(name = "meals", nullable = false)
    private Integer meals;

    @NotNull
    @Column(name = "sleep", nullable = false)
    private Boolean sleep;

    @Column(name = "lesson")
    private String lesson;

    @NotNull
    @Min(value = 1)
    @Max(value = 10)
    @Column(name = "overall", nullable = false)
    private Integer overall;

    @Column(name = "notes")
    private String notes;

    @ManyToOne
    private User user;

    public Points()
    {

    }
    public Points(LocalDate localDate, boolean b, int i, boolean b1, String s, int i1, String s1, User user) {

        this.date = localDate;
        this.exercise = b;
        this.meals = i;
        this.sleep=b1;
        this.lesson = s;
        this.overall = i1;
        this.notes = s1;
        this.user = user;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Points date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean isExercise() {
        return exercise;
    }

    public Points exercise(Boolean exercise) {
        this.exercise = exercise;
        return this;
    }

    public void setExercise(Boolean exercise) {
        this.exercise = exercise;
    }

    public Integer getMeals() {
        return meals;
    }

    public Points meals(Integer meals) {
        this.meals = meals;
        return this;
    }

    public void setMeals(Integer meals) {
        this.meals = meals;
    }

    public Boolean isSleep() {
        return sleep;
    }

    public Points sleep(Boolean sleep) {
        this.sleep = sleep;
        return this;
    }

    public void setSleep(Boolean sleep) {
        this.sleep = sleep;
    }

    public String getLesson() {
       return lesson;
    }

    public Points lesson(String lesson) {
        this.lesson = lesson;
        return this;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public Integer getOverall() {
        return overall;
    }

    public Points overall(Integer overall) {
        this.overall = overall;
        return this;
    }

    public void setOverall(Integer overall) {
        this.overall = overall;
    }

    public String getNotes() {
        return notes;
    }

    public Points notes(String notes) {
        this.notes = notes;
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public User getUser() {
        return user;
    }

    public Points user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Points points = (Points) o;
        if(points.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, points.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Points{" +
            "id=" + id +
            ", date='" + date + "'" +
            ", exercise='" + exercise + "'" +
            ", meals='" + meals + "'" +
            ", sleep='" + sleep + "'" +
            ", lesson='" + lesson + "'" +
            ", overall='" + overall + "'" +
            ", notes='" + notes + "'" +
            '}';
    }
}
