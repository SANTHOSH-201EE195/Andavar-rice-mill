package com.andavarmillklr.oil.masalas.controller;

import com.andavarmillklr.oil.masalas.model.Product;
import com.andavarmillklr.oil.masalas.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProductController - Public API for browsing products.
 *
 * Endpoints starting with "/api/products"
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Endpoint: GET /api/products
     * Optional query param: ?category=Oils
     *
     * @return List of products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false) String category) {
        return ResponseEntity.ok(productService.getProducts(category));
    }

    /**
     * Endpoint: GET /api/products/{id}
     *
     * @return A single product
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
}
