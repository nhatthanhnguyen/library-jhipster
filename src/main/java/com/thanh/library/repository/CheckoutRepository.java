package com.thanh.library.repository;

import com.thanh.library.domain.Checkout;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Checkout entity.
 */
@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, Long> {
    @Query("select checkout from Checkout checkout where checkout.user.login = ?#{principal.username}")
    List<Checkout> findByUserIsCurrentUser();

    default Optional<Checkout> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Checkout> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Checkout> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct checkout from Checkout checkout left join fetch checkout.user",
        countQuery = "select count(distinct checkout) from Checkout checkout"
    )
    Page<Checkout> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        value = "select distinct c from Checkout c join fetch c.user where c.user.id = :userId",
        countQuery = "select count(distinct c) from Checkout c where c.user.id = :userId"
    )
    Page<Checkout> findAllByCurrentUser(@Param("userId") Long userId, Pageable pageable);

    @Query("select distinct checkout from Checkout checkout left join fetch checkout.user")
    List<Checkout> findAllWithToOneRelationships();

    @Query("select checkout from Checkout checkout left join fetch checkout.user where checkout.id =:id")
    Optional<Checkout> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("select c from Checkout c join fetch c.bookCopy where c.bookCopy.id = :bookCopyId and c.endTime is null")
    List<Checkout> findAllThatBookCopyIsBorrowed(@Param("bookCopyId") Long bookCopyId);

    @Query(
        value = "select c from Checkout c where " +
        "(:bookCopyId is null or c.bookCopy.id = :bookCopyId) and " +
        "(:userId is null or c.user.id = :userId) and " +
        "c.isReturned = :isReturned and c.endTime is not null",
        countQuery = "select count(c) from Checkout c where " +
        "(:bookCopyId is null or c.bookCopy.id = :bookCopyId) and " +
        "(:userId is null or c.user.id = :userId) and " +
        "c.isReturned = :isReturned and c.endTime is not null"
    )
    Page<Checkout> findAllWithEndTimeNotNull(
        @Param("userId") Long userId,
        @Param("bookCopyId") Long bookCopyId,
        @Param("isReturned") Boolean isReturned,
        Pageable pageable
    );

    @Query(
        value = "select c from Checkout c where " +
        "(:bookCopyId is null or c.bookCopy.id = :bookCopyId) and " +
        "(:userId is null or c.user.id = :userId) and " +
        "c.isReturned = :isReturned and c.endTime is null",
        countQuery = "select count(c) from Checkout c where " +
        "(:bookCopyId is null or c.bookCopy.id = :bookCopyId) and " +
        "(:userId is null or c.user.id = :userId) and " +
        "c.isReturned = :isReturned and c.endTime is null"
    )
    Page<Checkout> findAllWithEndTimeNull(
        @Param("userId") Long userId,
        @Param("bookCopyId") Long bookCopyId,
        @Param("isReturned") Boolean isReturned,
        Pageable pageable
    );

    @Query(
        value = "select c from Checkout c where " +
        "(:userId is null or c.user.id = :userId) and " +
        "(:bookCopyId is null or c.bookCopy.id = :bookCopyId)",
        countQuery = "select count(c) from Checkout c where " +
        "(:userId is null or c.user.id = :userId) and " +
        "(:bookCopyId is null or c.bookCopy.id = :bookCopyId)"
    )
    Page<Checkout> findAllWithCondition(@Param("userId") Long userId, @Param("bookCopyId") Long bookCopyId, Pageable pageable);
}
