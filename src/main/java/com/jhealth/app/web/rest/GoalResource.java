package com.jhealth.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.jhealth.app.domain.Goal;
import com.jhealth.app.repository.GoalRepository;
import com.jhealth.app.repository.search.GoalSearchRepository;
import com.jhealth.app.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Goal.
 */
@RestController
@RequestMapping("/api")
public class GoalResource {

    private final Logger log = LoggerFactory.getLogger(GoalResource.class);
        
    @Inject
    private GoalRepository goalRepository;
    
    @Inject
    private GoalSearchRepository goalSearchRepository;
    
    /**
     * POST  /goals : Create a new goal.
     *
     * @param goal the goal to create
     * @return the ResponseEntity with status 201 (Created) and with body the new goal, or with status 400 (Bad Request) if the goal has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/goals",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Goal> createGoal(@Valid @RequestBody Goal goal) throws URISyntaxException {
        log.debug("REST request to save Goal : {}", goal);
        if (goal.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("goal", "idexists", "A new goal cannot already have an ID")).body(null);
        }
        Goal result = goalRepository.save(goal);
        goalSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/goals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("goal", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /goals : Updates an existing goal.
     *
     * @param goal the goal to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated goal,
     * or with status 400 (Bad Request) if the goal is not valid,
     * or with status 500 (Internal Server Error) if the goal couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/goals",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Goal> updateGoal(@Valid @RequestBody Goal goal) throws URISyntaxException {
        log.debug("REST request to update Goal : {}", goal);
        if (goal.getId() == null) {
            return createGoal(goal);
        }
        Goal result = goalRepository.save(goal);
        goalSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("goal", goal.getId().toString()))
            .body(result);
    }

    /**
     * GET  /goals : get all the goals.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of goals in body
     */
    @RequestMapping(value = "/goals",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Goal> getAllGoals() {
        log.debug("REST request to get all Goals");
        List<Goal> goals = goalRepository.findAll();
        return goals;
    }

    /**
     * GET  /goals/:id : get the "id" goal.
     *
     * @param id the id of the goal to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the goal, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/goals/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Goal> getGoal(@PathVariable Long id) {
        log.debug("REST request to get Goal : {}", id);
        Goal goal = goalRepository.findOne(id);
        return Optional.ofNullable(goal)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /goals/:id : delete the "id" goal.
     *
     * @param id the id of the goal to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/goals/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        log.debug("REST request to delete Goal : {}", id);
        goalRepository.delete(id);
        goalSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("goal", id.toString())).build();
    }

    /**
     * SEARCH  /_search/goals?query=:query : search for the goal corresponding
     * to the query.
     *
     * @param query the query of the goal search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/goals",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Goal> searchGoals(@RequestParam String query) {
        log.debug("REST request to search Goals for query {}", query);
        return StreamSupport
            .stream(goalSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
