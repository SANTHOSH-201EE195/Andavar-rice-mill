package com.andavarmillklr.oil.masalas.repository;

import com.andavarmillklr.oil.masalas.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ReviewRepository - Database access layer for the Review entity.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Get all reviews for a specific product, newest first.
     * Used in the ProductPreviewModal to show customer reviews.
     *
     * @param productId The product's ID
     * @return List of reviews for that product, newest first
     */
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    /**
     * Get ALL reviews (used to calculate average ratings for all products at once).
     * The frontend loads all reviews to compute a ratings map for every product.
     *
     * @return List of all reviews
     */
    List<Review> findAll();
}
