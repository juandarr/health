package org.jhipster.health.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.jhipster.health.domain.Preferences;

import org.jhipster.health.domain.User;
import org.jhipster.health.repository.PreferencesRepository;
import org.jhipster.health.repository.UserRepository;
import org.jhipster.health.security.AuthoritiesConstants;
import org.jhipster.health.security.SecurityUtils;
import org.jhipster.health.web.rest.util.HeaderUtil;
import org.jhipster.health.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Preferences.
 */
@RestController
@RequestMapping("/api")
public class PreferencesResource {

    private final Logger log = LoggerFactory.getLogger(PreferencesResource.class);

    @Inject
    private PreferencesRepository preferencesRepository;

    @Inject
    private UserRepository userRepository;
    /**
     * POST  /preferences : Create a new preferences.
     *
     * @param preferences the preferences to create
     * @return the ResponseEntity with status 201 (Created) and with body the new preferences, or with status 400 (Bad Request) if the preferences has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/preferences")
    @Timed
    public ResponseEntity<Preferences> createPreferences(@Valid @RequestBody Preferences preferences) throws URISyntaxException {
        log.debug("REST request to save Preferences : {}", preferences);
        if (preferences.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("preferences", "idexists", "A new preferences cannot already have an ID")).body(null);
        }

        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN))
        {
            log.debug("No admin user, using current user: {}", SecurityUtils.getCurrentUserLogin());
            preferences.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        }

        Preferences result = preferencesRepository.save(preferences);
        return ResponseEntity.created(new URI("/api/preferences/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("preferences", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /preferences : Updates an existing preferences.
     *
     * @param preferences the preferences to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated preferences,
     * or with status 400 (Bad Request) if the preferences is not valid,
     * or with status 500 (Internal Server Error) if the preferences couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/preferences")
    @Timed
    public ResponseEntity<Preferences> updatePreferences(@Valid @RequestBody Preferences preferences) throws URISyntaxException {
        log.debug("REST request to update Preferences : {}", preferences);
        if (preferences.getId() == null) {
            return createPreferences(preferences);
        }
        Preferences result = preferencesRepository.save(preferences);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("preferences", preferences.getId().toString()))
            .body(result);
    }

    /**
     * GET  /preferences : get all the preferences.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of preferences in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/preferences")
    @Timed
    public ResponseEntity<List<Preferences>> getAllPreferences(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Preferences");

        Page<Preferences> page;
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN))
        {
            page = preferencesRepository.findAll(pageable);
        }else
        {
            page = preferencesRepository.findAllForCurrentUser(pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/preferences");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /preferences/:id : get the "id" preferences.
     *
     * @param id the id of the preferences to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the preferences, or with status 404 (Not Found)
     */
    @GetMapping("/preferences/{id}")
    @Timed
    public ResponseEntity<Preferences> getPreferences(@PathVariable Long id) {
        log.debug("REST request to get Preferences : {}", id);
        Preferences preferences = preferencesRepository.findOne(id);
        return Optional.ofNullable(preferences)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /preferences/:id : delete the "id" preferences.
     *
     * @param id the id of the preferences to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/preferences/{id}")
    @Timed
    public ResponseEntity<Void> deletePreferences(@PathVariable Long id) {
        log.debug("REST request to delete Preferences : {}", id);
        preferencesRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("preferences", id.toString())).build();
    }

}
