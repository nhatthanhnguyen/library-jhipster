package com.thanh.library.repository;

import com.thanh.library.domain.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Publisher entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    @Query(
        "select p from Publisher p where (:search is null or " +
        "p.name like concat('%', :search, '%') or " +
        "cast(p.id as string) like concat('%', :search, '%'))"
    )
    Page<Publisher> getAllPagination(@Param("search") String search, Pageable pageable);
}
