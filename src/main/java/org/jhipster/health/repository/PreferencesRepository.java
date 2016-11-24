package org.jhipster.health.repository;

import org.jhipster.health.domain.Preferences;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Preferences entity.
 */
@SuppressWarnings("unused")
public interface PreferencesRepository extends JpaRepository<Preferences,Long> {

    @Query("select preferences from Preferences preferences where preferences.user.login = ?#{principal.username}")
    Page<Preferences> findAllForCurrentUser(Pageable pageable);
}
