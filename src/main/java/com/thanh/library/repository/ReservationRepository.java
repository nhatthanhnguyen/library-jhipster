package com.thanh.library.repository;

import com.thanh.library.domain.Reservation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
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
    List<Reservation> findAllThatBookCopyIsBorrowed(@Param("bookCopyId") Long bookCopyId);

    @Query(
        value = "select r from Reservation r join fetch r.user where r.user.id = :userId",
        countQuery = "select count(r) from Reservation r where r.user.id = :userId"
    )
    Page<Reservation> getAllByUserPagination(@Param("userId") Long userId, Pageable pageable);
}
