package org.jhipster.health.web.rest;

import org.jhipster.health.Application;
import org.jhipster.health.domain.Points;
import org.jhipster.health.domain.User;
import org.jhipster.health.repository.PointsRepository;
import org.jhipster.health.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import org.joda.time.*;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PointsResource REST controller.
 *
 * @see PointsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PointsResourceIntTest {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_EXERCISE = false;
    private static final Boolean UPDATED_EXERCISE = true;

    private static final Integer DEFAULT_MEALS = 1;
    private static final Integer UPDATED_MEALS = 2;

    private static final Boolean DEFAULT_SLEEP = false;
    private static final Boolean UPDATED_SLEEP = true;

    private static final String DEFAULT_LESSON = "AAAAAAAAAA";
    private static final String UPDATED_LESSON = "BBBBBBBBBB";

    private static final Integer DEFAULT_OVERALL = 1;
    private static final Integer UPDATED_OVERALL = 2;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    @Inject
    private PointsRepository pointsRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPointsMockMvc;

    private Points points;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PointsResource pointsResource = new PointsResource();
        ReflectionTestUtils.setField(pointsResource, "pointsRepository", pointsRepository);
        ReflectionTestUtils.setField(pointsResource, "userRepository", userRepository);
        this.restPointsMockMvc = MockMvcBuilders.standaloneSetup(pointsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Points createEntity(EntityManager em) {
        Points points = new Points()
                .date(DEFAULT_DATE)
                .exercise(DEFAULT_EXERCISE)
                .meals(DEFAULT_MEALS)
                .sleep(DEFAULT_SLEEP)
                .lesson(DEFAULT_LESSON)
                .overall(DEFAULT_OVERALL)
                .notes(DEFAULT_NOTES);
        return points;
    }

    @Before
    public void initTest() {
        points = createEntity(em);
    }

    public void createPointsByWeek(LocalDate thisMonday, LocalDate lastMonday)
    {
        User user = userRepository.findOneByLogin("user").get();
        //Create new points in two separate weeks
        points = new Points(thisMonday.plusDays(2), true, 2, true, "Hello world!", 8, "Hello world!", user );
        pointsRepository.saveAndFlush(points);

        points = new Points(thisMonday.plusDays(3), false, 3, true, "", 8, "Hello world!", user );
        pointsRepository.saveAndFlush(points);

        points = new Points(lastMonday.plusDays(3), true, 1, false, "", 8, "Hello world!", user );
        pointsRepository.saveAndFlush(points);

        points = new Points(lastMonday.plusDays(4), true, 3, true, "Hello world!", 8, "Hello world!", user );
        pointsRepository.saveAndFlush(points);
    }


    @Test
    @Transactional
    public void createPoints() throws Exception {
        int databaseSizeBeforeCreate = pointsRepository.findAll().size();

        // Create security-aware MockMVC
        restPointsMockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        // Create the Points

        restPointsMockMvc.perform(post("/api/points")
                .with(user("user"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(points)))
                .andExpect(status().isCreated());

        // Validate the Points in the database
        List<Points> points = pointsRepository.findAll();
        assertThat(points).hasSize(databaseSizeBeforeCreate + 1);
        Points testPoints = points.get(points.size() - 1);
        assertThat(testPoints.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testPoints.isExercise()).isEqualTo(DEFAULT_EXERCISE);
        assertThat(testPoints.getMeals()).isEqualTo(DEFAULT_MEALS);
        assertThat(testPoints.isSleep()).isEqualTo(DEFAULT_SLEEP);
        assertThat(testPoints.getLesson()).isEqualTo(DEFAULT_LESSON);
        assertThat(testPoints.getOverall()).isEqualTo(DEFAULT_OVERALL);
        assertThat(testPoints.getNotes()).isEqualTo(DEFAULT_NOTES);
    }

    @Test
    @Transactional
    public void getPointsThisWeek() throws Exception
    {
        org.joda.time.LocalDate today =  new org.joda.time.LocalDate();
        org.joda.time.LocalDate tM = today.withDayOfWeek(DateTimeConstants.MONDAY);
        org.joda.time.LocalDate lM = tM.minusWeeks(1);
        LocalDate thisMonday = LocalDate.of(tM.getYear(),tM.getMonthOfYear(),tM.dayOfMonth().get());
        LocalDate lastMonday = LocalDate.of(lM.getYear(),lM.getMonthOfYear(),lM.dayOfMonth().get());
        createPointsByWeek(thisMonday, lastMonday);

        // create security-aware mockMvc
        restPointsMockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        // Get all the points
        restPointsMockMvc.perform(get("/api/points")
            .with(user("user").roles("USER")))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
            //.andExpect(jsonPath("$", hasSize(4)));

        // Get the points for this week only
        restPointsMockMvc.perform(get("/api/points-this-week")
            .with(user("user").roles("USER")))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.week").value(thisMonday.toString()))
            .andExpect(jsonPath("$.points").value(5));
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = pointsRepository.findAll().size();
        // set the field null
        points.setDate(null);

        // Create the Points, which fails.

        restPointsMockMvc.perform(post("/api/points")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(points)))
                .andExpect(status().isBadRequest());

        List<Points> points = pointsRepository.findAll();
        assertThat(points).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkExerciseIsRequired() throws Exception {
        int databaseSizeBeforeTest = pointsRepository.findAll().size();
        // set the field null
        points.setExercise(null);

        // Create the Points, which fails.

        restPointsMockMvc.perform(post("/api/points")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(points)))
                .andExpect(status().isBadRequest());

        List<Points> points = pointsRepository.findAll();
        assertThat(points).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMealsIsRequired() throws Exception {
        int databaseSizeBeforeTest = pointsRepository.findAll().size();
        // set the field null
        points.setMeals(null);

        // Create the Points, which fails.

        restPointsMockMvc.perform(post("/api/points")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(points)))
                .andExpect(status().isBadRequest());

        List<Points> points = pointsRepository.findAll();
        assertThat(points).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSleepIsRequired() throws Exception {
        int databaseSizeBeforeTest = pointsRepository.findAll().size();
        // set the field null
        points.setSleep(null);

        // Create the Points, which fails.

        restPointsMockMvc.perform(post("/api/points")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(points)))
                .andExpect(status().isBadRequest());

        List<Points> points = pointsRepository.findAll();
        assertThat(points).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOverallIsRequired() throws Exception {
        int databaseSizeBeforeTest = pointsRepository.findAll().size();
        // set the field null
        points.setOverall(null);

        // Create the Points, which fails.

        restPointsMockMvc.perform(post("/api/points")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(points)))
                .andExpect(status().isBadRequest());

        List<Points> points = pointsRepository.findAll();
        assertThat(points).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPoints() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // create security-aware mockMvc
        restPointsMockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        // Get all the points
        restPointsMockMvc.perform(get("/api/points?sort=id,desc")
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(points.getId().intValue())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].exercise").value(hasItem(DEFAULT_EXERCISE.booleanValue())))
                .andExpect(jsonPath("$.[*].meals").value(hasItem(DEFAULT_MEALS)))
                .andExpect(jsonPath("$.[*].sleep").value(hasItem(DEFAULT_SLEEP.booleanValue())))
                .andExpect(jsonPath("$.[*].lesson").value(hasItem(DEFAULT_LESSON.toString())))
                .andExpect(jsonPath("$.[*].overall").value(hasItem(DEFAULT_OVERALL)))
                .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES.toString())));
    }

    @Test
    @Transactional
    public void getPoints() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);

        // Get the points
        restPointsMockMvc.perform(get("/api/points/{id}", points.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(points.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.exercise").value(DEFAULT_EXERCISE.booleanValue()))
            .andExpect(jsonPath("$.meals").value(DEFAULT_MEALS))
            .andExpect(jsonPath("$.sleep").value(DEFAULT_SLEEP.booleanValue()))
            .andExpect(jsonPath("$.lesson").value(DEFAULT_LESSON.toString()))
            .andExpect(jsonPath("$.overall").value(DEFAULT_OVERALL))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPoints() throws Exception {
        // Get the points
        restPointsMockMvc.perform(get("/api/points/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePoints() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);
        int databaseSizeBeforeUpdate = pointsRepository.findAll().size();

        // Update the points
        Points updatedPoints = pointsRepository.findOne(points.getId());
        updatedPoints
                .date(UPDATED_DATE)
                .exercise(UPDATED_EXERCISE)
                .meals(UPDATED_MEALS)
                .sleep(UPDATED_SLEEP)
                .lesson(UPDATED_LESSON)
                .overall(UPDATED_OVERALL)
                .notes(UPDATED_NOTES);

        restPointsMockMvc.perform(put("/api/points")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPoints)))
                .andExpect(status().isOk());

        // Validate the Points in the database
        List<Points> points = pointsRepository.findAll();
        assertThat(points).hasSize(databaseSizeBeforeUpdate);
        Points testPoints = points.get(points.size() - 1);
        assertThat(testPoints.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testPoints.isExercise()).isEqualTo(UPDATED_EXERCISE);
        assertThat(testPoints.getMeals()).isEqualTo(UPDATED_MEALS);
        assertThat(testPoints.isSleep()).isEqualTo(UPDATED_SLEEP);
        assertThat(testPoints.getLesson()).isEqualTo(UPDATED_LESSON);
        assertThat(testPoints.getOverall()).isEqualTo(UPDATED_OVERALL);
        assertThat(testPoints.getNotes()).isEqualTo(UPDATED_NOTES);
    }

    @Test
    @Transactional
    public void deletePoints() throws Exception {
        // Initialize the database
        pointsRepository.saveAndFlush(points);
        int databaseSizeBeforeDelete = pointsRepository.findAll().size();

        // Get the points
        restPointsMockMvc.perform(delete("/api/points/{id}", points.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Points> points = pointsRepository.findAll();
        assertThat(points).hasSize(databaseSizeBeforeDelete - 1);
    }
}
