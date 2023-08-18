package com.thanh.library.repository;

import com.thanh.library.domain.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Book entity.
 * <p>
 * When extending this class, extend BookRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface BookRepository extends BookRepositoryWithBagRelationships, JpaRepository<Book, Long> {
    default Optional<Book> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Book> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Book> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct book from Book book left join fetch book.publisher",
        countQuery = "select count(distinct book) from Book book"
    )
    Page<Book> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct book from Book book left join fetch book.publisher")
    List<Book> findAllWithToOneRelationships();

    @Query("select book from Book book left join fetch book.publisher where book.id =:id")
    Optional<Book> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("select b from Book b " + "where :search is null or b.title like concat('%', :search, '%') ")
    Page<Book> findAll(@Param("search") String search, Pageable pageable);

    @Query("select b from Book b " + "where b.isDeleted = false " + "and (:search is null or b.title like concat('%', :search, '%'))")
    Page<Book> findAllAvailablePagination(@Param("search") String search, Pageable pageable);

    @Query("select b from Book b where b.isDeleted = false")
    List<Book> findAllAvailable(Sort sort);

    @Query(
        value = "select distinct b from Book b join b.categories c where c.id = :categoryId",
        countQuery = "select count(distinct b) from Book b join b.categories c where c.id = :categoryId"
    )
    Page<Book> findAllByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query(
        value = "select b from Book b join fetch b.publisher p where p.id = :publisherId",
        countQuery = "select count(b) from Book b join b.publisher p where p.id = :publisherId"
    )
    Page<Book> findAllByPublisher(@Param("publisherId") Long publisherId, Pageable pageable);
}
