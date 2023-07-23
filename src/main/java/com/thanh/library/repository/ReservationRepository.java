package com.thanh.library.repository;

import com.thanh.library.domain.Reservation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Reservation entity.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    @Query("select reservation from Reservation reservation where reservation.user.login = ?#{principal.username}")
    List<Reservation> findByUserIsCurrentUser();

    default Optional<Reservation> findOneWithEagerRelationships(UUID id) {
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
    Optional<Reservation> findOneWithToOneRelationships(@Param("id") UUID id);
}
