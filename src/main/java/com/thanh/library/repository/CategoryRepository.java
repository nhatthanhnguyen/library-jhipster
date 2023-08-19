package com.thanh.library.repository;

import com.thanh.library.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Category entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(
        "select c from Category c where (:search is null or " +
        "c.name like concat('%', :search, '%') or " +
        "cast(c.id as string) like concat('%', :search, '%'))"
    )
    Page<Category> getAllPagination(@Param("search") String search, Pageable pageable);
}
