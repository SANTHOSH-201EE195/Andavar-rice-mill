package com.andavarmillklr.oil.masalas.service;

import com.andavarmillklr.oil.masalas.model.Product;
import com.andavarmillklr.oil.masalas.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ProductService - Handles business logic for Products.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Get all products, or filter by category if provided.
     * Always returns newest first.
     */
    public List<Product> getProducts(String category) {
        if (category != null && !category.trim().isEmpty()) {
            return productRepository.findByCategoryOrderByCreatedAtDesc(category);
        }
        return productRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get a single product by ID.
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    /**
     * Create a new product (Admin only).
     */
    public Product createProduct(Product product, Long adminId) {
        product.setCreatedBy(adminId);
        return productRepository.save(product);
    }

    /**
     * Update an existing product (Admin only).
     */
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setCategory(productDetails.getCategory());
        
        // Only update image details if new ones are provided
        if (productDetails.getImageUrl() != null) {
            product.setImageUrl(productDetails.getImageUrl());
            product.setImageKey(productDetails.getImageKey());
        }
        
        return productRepository.save(product);
    }

    /**
     * Delete a product (Admin only).
     */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
