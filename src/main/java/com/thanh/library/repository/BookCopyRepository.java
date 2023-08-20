package com.thanh.library.repository;

import com.thanh.library.domain.BookCopy;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BookCopy entity.
 */
@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    default Optional<BookCopy> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<BookCopy> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<BookCopy> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct bookCopy from BookCopy bookCopy left join fetch bookCopy.book",
        countQuery = "select count(distinct bookCopy) from BookCopy bookCopy"
    )
    Page<BookCopy> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct bookCopy from BookCopy bookCopy left join fetch bookCopy.book")
    List<BookCopy> findAllWithToOneRelationships();

    @Query("select bookCopy from BookCopy bookCopy left join fetch bookCopy.book where bookCopy.id =:id")
    Optional<BookCopy> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        value = "select bc from BookCopy bc join fetch bc.book " +
        "where bc.isDeleted = false and bc.book.id = :bookId and bc.book.isDeleted = false and " +
        "((bc.id not in (select r.bookCopy.id from Reservation r where r.endTime is null)) and " +
        "(bc.id not in (select c.bookCopy.id from Checkout c where c.endTime is null)))"
    )
    List<BookCopy> findBookCopiesAvailableByBookId(@Param("bookId") Long bookId);

    @Query(
        value = "select bc from BookCopy bc join fetch bc.book b where b.id = :bookId",
        countQuery = "select count(bc) from BookCopy bc join bc.book b where b.id = :bookId"
    )
    Page<BookCopy> findBookCopiesByBookId(@Param("bookId") Long bookId, Pageable pageable);

    @Query("select bc from BookCopy bc join fetch bc.book where bc.book.isDeleted = false and bc.isDeleted = false")
    List<BookCopy> findAllBookCopiesThatIsNotDeleted(Sort sort);
}
