package com.andavarmillklr.oil.masalas.service;

import com.andavarmillklr.oil.masalas.model.Product;
import com.andavarmillklr.oil.masalas.model.Review;
import com.andavarmillklr.oil.masalas.model.User;
import com.andavarmillklr.oil.masalas.repository.ProductRepository;
import com.andavarmillklr.oil.masalas.repository.ReviewRepository;
import com.andavarmillklr.oil.masalas.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReviewService - Handles product reviews and ratings.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         ProductRepository productRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all reviews for a specific product.
     */
    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    /**
     * Add a new review to a product.
     */
    @Transactional
    public Review addReview(Long userId, Long productId, int rating, String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);
        review.setUserName(user.getFullName());

        return reviewRepository.save(review);
    }

    /**
     * Calculate average ratings for ALL products.
     * Returns a map of ProductId -> Average Rating.
     */
    public Map<Long, Double> getAllProductRatings() {
        List<Review> allReviews = reviewRepository.findAll();
        
        // Group by product and calculate sum and count
        Map<Long, RatingStats> statsMap = new HashMap<>();
        
        for (Review r : allReviews) {
            Long pid = r.getProduct().getId();
            statsMap.putIfAbsent(pid, new RatingStats());
            
            RatingStats stats = statsMap.get(pid);
            stats.sum += r.getRating();
            stats.count++;
        }

        // Calculate averages
        Map<Long, Double> averages = new HashMap<>();
        for (Map.Entry<Long, RatingStats> entry : statsMap.entrySet()) {
            averages.put(entry.getKey(), (double) entry.getValue().sum / entry.getValue().count);
        }

        return averages;
    }

    // Helper class for calculating averages
    private static class RatingStats {
        int sum = 0;
        int count = 0;
    }
}
