package com.andavarmillklr.oil.masalas.controller;

import com.andavarmillklr.oil.masalas.model.Review;
import com.andavarmillklr.oil.masalas.model.User;
import com.andavarmillklr.oil.masalas.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ReviewController - API for product reviews and ratings.
 */
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * PUBLIC: Get reviews for a specific product.
     */
    @GetMapping("/api/reviews/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    /**
     * PUBLIC: Get average ratings for all products.
     * The frontend needs this to show stars on the product listing page.
     */
    @GetMapping("/api/ratings")
    public ResponseEntity<Map<Long, Double>> getAllRatings() {
        return ResponseEntity.ok(reviewService.getAllProductRatings());
    }

    /**
     * AUTHENTICATED: Add a new review.
     */
    @PostMapping("/api/reviews")
    public ResponseEntity<Review> addReview(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> requestBody) {
            
        Long productId = Long.valueOf(requestBody.get("productId").toString());
        int rating = Integer.parseInt(requestBody.get("rating").toString());
        String comment = requestBody.containsKey("comment") ? requestBody.get("comment").toString() : null;

        try {
            Review review = reviewService.addReview(user.getId(), productId, rating, comment);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
