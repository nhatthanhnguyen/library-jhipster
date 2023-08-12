package com.thanh.library.repository;

import com.thanh.library.domain.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Author entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    @Query(value = "select distinct a from Author a", countQuery = "select count(distinct a) from Author a")
    Page<Author> findAllAvailable(Pageable pageable);
}
