package com.thanh.library.repository;

import com.thanh.library.domain.Checkout;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Checkout entity.
 */
@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, UUID> {
    @Query("select checkout from Checkout checkout where checkout.user.login = ?#{principal.username}")
    List<Checkout> findByUserIsCurrentUser();

    default Optional<Checkout> findOneWithEagerRelationships(UUID id) {
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

    @Query("select distinct checkout from Checkout checkout left join fetch checkout.user")
    List<Checkout> findAllWithToOneRelationships();

    @Query("select checkout from Checkout checkout left join fetch checkout.user where checkout.id =:id")
    Optional<Checkout> findOneWithToOneRelationships(@Param("id") UUID id);
}