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
    @Query(
        "select a from Author a where (:search is null or " +
        "a.lastName like concat('%', :search, '%') or " +
        "a.firstName like concat('%', :search, '%') or " +
        "cast(a.id as string) like concat('%', :search, '%'))"
    )
    Page<Author> getAllPagination(@Param("search") String search, Pageable pageable);
}
