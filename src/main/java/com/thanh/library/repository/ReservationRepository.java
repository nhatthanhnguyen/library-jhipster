package com.thanh.library.repository;

import com.thanh.library.domain.Reservation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Reservation entity.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("select reservation from Reservation reservation where reservation.user.login = ?#{principal.username}")
    List<Reservation> findByUserIsCurrentUser();

    default Optional<Reservation> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Reservation> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Reservation> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct reservation from Reservation reservation left join fetch reservation.user",
        countQuery = "select count(distinct reservation) from Reservation reservation"
    )
    Page<Reservation> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct reservation from Reservation reservation left join fetch reservation.user")
    List<Reservation> findAllWithToOneRelationships();

    @Query("select reservation from Reservation reservation left join fetch reservation.user where reservation.id =:id")
    Optional<Reservation> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("select r from Reservation r where r.bookCopy.id = :bookCopyId and r.endTime is null")
    Optional<Reservation> findThatBookCopyIsHold(@Param("bookCopyId") Long bookCopyId);

    @Query(
        value = "select r from Reservation r join fetch r.user where r.user.id = :userId",
        countQuery = "select count(r) from Reservation r where r.user.id = :userId"
    )
    Page<Reservation> getAllByUserPagination(@Param("userId") Long userId, Pageable pageable);

    @Query(
        value = "select r from Reservation r join fetch r.user join fetch r.bookCopy where " +
        "(:user is null " +
        "or cast(r.user.id as string) = :user " +
        "or r.user.firstName like concat('%', :user, '%') " +
        "or r.user.email like concat('%', :user, '%') " +
        "or r.user.lastName like concat('%', :user, '%')) " +
        "and" +
        "(:bookCopy is null " +
        "or cast(r.bookCopy.id as string) = :bookCopy) " +
        "or cast(r.bookCopy.book.id as string) = :bookCopy " +
        "or r.bookCopy.book.title like concat('%', :bookCopy, '%')",
        countQuery = "select count(r) from Reservation r where " +
        "(:user is null " +
        "or cast(r.user.id as string) = :user " +
        "or r.user.firstName like concat('%', :user, '%') " +
        "or r.user.email like concat('%', :user, '%') " +
        "or r.user.lastName like concat('%', :user, '%')) " +
        "and" +
        "(:bookCopy is null " +
        "or cast(r.bookCopy.id as string) = :bookCopy) " +
        "or cast(r.bookCopy.book.id as string) = :bookCopy " +
        "or r.bookCopy.book.title like concat('%', :bookCopy, '%')"
    )
    Page<Reservation> getAllWithConditionPagination(@Param("user") String user, @Param("bookCopy") String bookCopy, Pageable pageable);
}
