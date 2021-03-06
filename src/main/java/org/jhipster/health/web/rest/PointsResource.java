package org.jhipster.health.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.jhipster.health.domain.Points;

import org.jhipster.health.repository.PointsRepository;
import org.jhipster.health.repository.UserRepository;
import org.jhipster.health.security.AuthoritiesConstants;
import org.jhipster.health.security.SecurityUtils;
import org.jhipster.health.web.rest.util.HeaderUtil;
import org.jhipster.health.web.rest.util.PaginationUtil;
import org.jhipster.health.web.rest.vm.PointsPerWeek;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
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
 * REST controller for managing Points.
 */
@RestController
@RequestMapping("/api")
public class PointsResource {

    private final Logger log = LoggerFactory.getLogger(PointsResource.class);

    @Inject
    private PointsRepository pointsRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * POST  /points : Create a new points.
     *
     * @param points the points to create
     * @return the ResponseEntity with status 201 (Created) and with body the new points, or with status 400 (Bad Request) if the points has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/points")
    @Timed
    public ResponseEntity<Points> createPoints(@Valid @RequestBody Points points) throws URISyntaxException {
        log.debug("REST request to save Points : {}", points);
        if (points.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("points", "idexists", "A new points cannot already have an ID")).body(null);
        }

        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN))
        {
            log.debug("No admin user, using current user: {}", SecurityUtils.getCurrentUserLogin());
            points.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        }

        Points result = pointsRepository.save(points);
        return ResponseEntity.created(new URI("/api/points/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("points", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /points : Updates an existing points.
     *
     * @param points the points to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated points,
     * or with status 400 (Bad Request) if the points is not valid,
     * or with status 500 (Internal Server Error) if the points couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/points")
    @Timed
    public ResponseEntity<Points> updatePoints(@Valid @RequestBody Points points) throws URISyntaxException {
        log.debug("REST request to update Points : {}", points);
        if (points.getId() == null) {
            return createPoints(points);
        }
        Points result = pointsRepository.save(points);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("points", points.getId().toString()))
            .body(result);
    }

    /**
     * GET  /points : get all the points.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of points in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/points")
    @Timed
    public ResponseEntity<List<Points>> getAllPoints(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Points");
        Page<Points> page;
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN))
        {
            page = pointsRepository.findAllByOrderByDateDesc(pageable);
        }else
        {
            page = pointsRepository.findAllForCurrentUser(pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/points");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /points/:id : get the "id" points.
     *
     * @param id the id of the points to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the points, or with status 404 (Not Found)
     */
    @GetMapping("/points/{id}")
    @Timed
    public ResponseEntity<Points> getPoints(@PathVariable Long id) {
        log.debug("REST request to get Points : {}", id);
        Points points = pointsRepository.findOne(id);
        return Optional.ofNullable(points)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /points/:id : delete the "id" points.
     *
     * @param id the id of the points to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/points/{id}")
    @Timed
    public ResponseEntity<Void> deletePoints(@PathVariable Long id) {
        log.debug("REST request to delete Points : {}", id);
        pointsRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("points", id.toString())).build();
    }

    public Integer toInt(Boolean val)
    {
        if (val) return 1;
        else  return 0;
    }

    public Integer isEmp(Object o)
    {
        if (o == null)  return 0;
        else if (o.toString().length()==0) return 0;
        else return 3;
    }

    /**
     * GET /points -> get all the points for the current week.
     */
    @RequestMapping(value = "/points-this-week")
    @Timed
    public ResponseEntity<PointsPerWeek> getPointsThisWeek() {
        // Get current date
        LocalDate now = new LocalDate();
        // Get first day of week
        LocalDate sWeek = now.withDayOfWeek(DateTimeConstants.MONDAY);
        // Get last day of week
        LocalDate eWeek = now.withDayOfWeek(DateTimeConstants.SUNDAY);

        java.time.LocalDate startOfWeek = java.time.LocalDate.of(sWeek.getYear(),sWeek.getMonthOfYear(),sWeek.getDayOfMonth());
        java.time.LocalDate endOfWeek = java.time.LocalDate.of(eWeek.getYear(),eWeek.getMonthOfYear(),eWeek.getDayOfMonth());

        log.debug("Looking for points between: {} and {}", startOfWeek, endOfWeek);
        List<Points> points = pointsRepository.findAllByDateBetween(startOfWeek, endOfWeek);
        // filter by current user and sum the points
        Integer numPoints = points.stream()
            .filter(p -> p.getUser().getLogin().equals(SecurityUtils.getCurrentUserLogin()))
            .mapToInt(p -> p.getMeals()+ toInt(p.isExercise())*5+toInt(p.isSleep())*3 + isEmp(p.getLesson()) )
            .sum();

        PointsPerWeek count = new PointsPerWeek(startOfWeek, numPoints);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

}
