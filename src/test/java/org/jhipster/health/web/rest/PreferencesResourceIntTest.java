package org.jhipster.health.web.rest;

import org.jhipster.health.Application;

import org.jhipster.health.domain.Preferences;
import org.jhipster.health.repository.PreferencesRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.jhipster.health.domain.enumeration.Units;
/**
 * Test class for the PreferencesResource REST controller.
 *
 * @see PreferencesResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PreferencesResourceIntTest {

    private static final Integer DEFAULT_WEEKLY_GOAL = 28;
    private static final Integer UPDATED_WEEKLY_GOAL = 29;

    private static final Units DEFAULT_WEIGHT_UNITS = Units.kg;
    private static final Units UPDATED_WEIGHT_UNITS = Units.lb;

    @Inject
    private PreferencesRepository preferencesRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPreferencesMockMvc;

    private Preferences preferences;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PreferencesResource preferencesResource = new PreferencesResource();
        ReflectionTestUtils.setField(preferencesResource, "preferencesRepository", preferencesRepository);
        this.restPreferencesMockMvc = MockMvcBuilders.standaloneSetup(preferencesResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Preferences createEntity(EntityManager em) {
        Preferences preferences = new Preferences()
                .weekly_goal(DEFAULT_WEEKLY_GOAL)
                .weight_units(DEFAULT_WEIGHT_UNITS);
        return preferences;
    }

    @Before
    public void initTest() {
        preferences = createEntity(em);
    }

    @Test
    @Transactional
    public void createPreferences() throws Exception {
        int databaseSizeBeforeCreate = preferencesRepository.findAll().size();

        // Create the Preferences

        restPreferencesMockMvc.perform(post("/api/preferences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(preferences)))
                .andExpect(status().isCreated());

        // Validate the Preferences in the database
        List<Preferences> preferences = preferencesRepository.findAll();
        assertThat(preferences).hasSize(databaseSizeBeforeCreate + 1);
        Preferences testPreferences = preferences.get(preferences.size() - 1);
        assertThat(testPreferences.getWeekly_goal()).isEqualTo(DEFAULT_WEEKLY_GOAL);
        assertThat(testPreferences.getWeight_units()).isEqualTo(DEFAULT_WEIGHT_UNITS);
    }

    @Test
    @Transactional
    public void checkWeekly_goalIsRequired() throws Exception {
        int databaseSizeBeforeTest = preferencesRepository.findAll().size();
        // set the field null
        preferences.setWeekly_goal(null);

        // Create the Preferences, which fails.

        restPreferencesMockMvc.perform(post("/api/preferences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(preferences)))
                .andExpect(status().isBadRequest());

        List<Preferences> preferences = preferencesRepository.findAll();
        assertThat(preferences).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkWeight_unitsIsRequired() throws Exception {
        int databaseSizeBeforeTest = preferencesRepository.findAll().size();
        // set the field null
        preferences.setWeight_units(null);

        // Create the Preferences, which fails.

        restPreferencesMockMvc.perform(post("/api/preferences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(preferences)))
                .andExpect(status().isBadRequest());

        List<Preferences> preferences = preferencesRepository.findAll();
        assertThat(preferences).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPreferences() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get all the preferences
        restPreferencesMockMvc.perform(get("/api/preferences?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(preferences.getId().intValue())))
                .andExpect(jsonPath("$.[*].weekly_goal").value(hasItem(DEFAULT_WEEKLY_GOAL)))
                .andExpect(jsonPath("$.[*].weight_units").value(hasItem(DEFAULT_WEIGHT_UNITS.toString())));
    }

    @Test
    @Transactional
    public void getPreferences() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);

        // Get the preferences
        restPreferencesMockMvc.perform(get("/api/preferences/{id}", preferences.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(preferences.getId().intValue()))
            .andExpect(jsonPath("$.weekly_goal").value(DEFAULT_WEEKLY_GOAL))
            .andExpect(jsonPath("$.weight_units").value(DEFAULT_WEIGHT_UNITS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPreferences() throws Exception {
        // Get the preferences
        restPreferencesMockMvc.perform(get("/api/preferences/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePreferences() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);
        int databaseSizeBeforeUpdate = preferencesRepository.findAll().size();

        // Update the preferences
        Preferences updatedPreferences = preferencesRepository.findOne(preferences.getId());
        updatedPreferences
                .weekly_goal(UPDATED_WEEKLY_GOAL)
                .weight_units(UPDATED_WEIGHT_UNITS);

        restPreferencesMockMvc.perform(put("/api/preferences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPreferences)))
                .andExpect(status().isOk());

        // Validate the Preferences in the database
        List<Preferences> preferences = preferencesRepository.findAll();
        assertThat(preferences).hasSize(databaseSizeBeforeUpdate);
        Preferences testPreferences = preferences.get(preferences.size() - 1);
        assertThat(testPreferences.getWeekly_goal()).isEqualTo(UPDATED_WEEKLY_GOAL);
        assertThat(testPreferences.getWeight_units()).isEqualTo(UPDATED_WEIGHT_UNITS);
    }

    @Test
    @Transactional
    public void deletePreferences() throws Exception {
        // Initialize the database
        preferencesRepository.saveAndFlush(preferences);
        int databaseSizeBeforeDelete = preferencesRepository.findAll().size();

        // Get the preferences
        restPreferencesMockMvc.perform(delete("/api/preferences/{id}", preferences.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Preferences> preferences = preferencesRepository.findAll();
        assertThat(preferences).hasSize(databaseSizeBeforeDelete - 1);
    }
}
