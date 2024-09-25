package com.lavkatech.lottery.repository;

import com.lavkatech.lottery.entity.Marker;
import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MarkerRepository extends JpaRepository<Marker, UUID> {

    /**
     * Найти последний маркер
     *
     * @return последний маркер
     */
    Optional<Marker> findTopByUserGroupAndUserLevelOrderByCreatedOnDesc(Group group, Level level);

    @Query(value = """
        SELECT DISTINCT ON (m.user_level, m.user_group)
            *
        FROM markers m
        WHERE user_group IS NOT NULL AND user_level IS NOT NULL
        ORDER BY  m.user_level, m.user_group, m.created_on DESC;
        """, nativeQuery = true)
    List<Marker> findLatestMarkers();
}
