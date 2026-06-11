package com.andavarmillklr.oil.masalas.repository;

import com.andavarmillklr.oil.masalas.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ProductRepository - Database access layer for the Product entity.
 *
 * Provides methods to find products by category and list them in
 * reverse chronological order (newest first), which matches how the
 * frontend displays products.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Get all products sorted by creation date (newest first).
     * This matches the frontend query: .order('created_at', { ascending: false })
     *
     * @return List of all products, newest first
     */
    List<Product> findAllByOrderByCreatedAtDesc();

    /**
     * Get products filtered by category, sorted by creation date (newest first).
     * Used when the user clicks "Oils" or "Masalas" category filter.
     *
     * @param category The category to filter by ("Oils" or "Masalas")
     * @return List of products in that category, newest first
     */
    List<Product> findByCategoryOrderByCreatedAtDesc(String category);
}
