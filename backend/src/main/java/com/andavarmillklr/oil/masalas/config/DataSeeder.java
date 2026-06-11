package com.andavarmillklr.oil.masalas.config;

import com.andavarmillklr.oil.masalas.model.Product;
import com.andavarmillklr.oil.masalas.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

/**
 * DataSeeder - Automatically inserts initial product data.
 *
 * This runs every time the Spring Boot application starts.
 * It checks if the database is empty, and if so, it inserts all
 * the default oils and masalas with their correct image paths.
 */
@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedDatabase(ProductRepository productRepository, org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        return args -> {


            // Only insert data if the products table is empty
            if (productRepository.count() == 0) {
                System.out.println("Database is empty. Seeding initial products...");

                // ==============================
                // OILS
                // ==============================
                Product coconutOil = new Product();
                coconutOil.setName("Cold Pressed Coconut Oil");
                coconutOil.setDescription("100% Pure, unrefined, and cold-pressed coconut oil. Ideal for cooking, hair, and skin care. Made from high-quality coconuts using traditional wooden sekku.");
                coconutOil.setPrice(new BigDecimal("350.00"));
                coconutOil.setStockQuantity(50);
                coconutOil.setCategory("Oils");
                coconutOil.setImageUrl("/images/oil/coconut_oil.png");

                Product groundnutOil = new Product();
                groundnutOil.setName("Cold Pressed Groundnut Oil");
                groundnutOil.setDescription("Traditional wooden cold-pressed peanut oil. Rich in antioxidants and perfect for deep frying and everyday Indian cooking.");
                groundnutOil.setPrice(new BigDecimal("280.00"));
                groundnutOil.setStockQuantity(50);
                groundnutOil.setCategory("Oils");
                groundnutOil.setImageUrl("/images/oil/peanut_oil.jpg");

                Product sesameOil = new Product();
                sesameOil.setName("Cold Pressed Sesame Oil");
                sesameOil.setDescription("Authentic Gingelly oil (Nallennai) extracted using palm jaggery in traditional wooden ghani. Best for tempering, pickles, and body massage.");
                sesameOil.setPrice(new BigDecimal("420.00"));
                sesameOil.setStockQuantity(30);
                sesameOil.setCategory("Oils");
                sesameOil.setImageUrl("/images/oil/sesame_oil.png");

                Product castorOil = new Product();
                castorOil.setName("Pure Castor Oil");
                castorOil.setDescription("Thick, pure, and unrefined castor oil. Excellent for hair growth, skin moisturizing, and traditional medicinal uses.");
                castorOil.setPrice(new BigDecimal("200.00"));
                castorOil.setStockQuantity(20);
                castorOil.setCategory("Oils");
                castorOil.setImageUrl("/images/oil/castor_oil.png");

                // ==============================
                // MASALAS
                // ==============================
                Product chickenMasala = new Product();
                chickenMasala.setName("Chicken Masala");
                chickenMasala.setDescription("Authentic South Indian style chicken masala blended with roasted spices for the perfect aroma and taste.");
                chickenMasala.setPrice(new BigDecimal("60.00"));
                chickenMasala.setStockQuantity(100);
                chickenMasala.setCategory("Masalas");
                chickenMasala.setImageUrl("/images/masala/chicken_masala.png");

                Product muttonMasala = new Product();
                muttonMasala.setName("Mutton Masala");
                muttonMasala.setDescription("Rich and spicy mutton masala crafted from a traditional family recipe. Gives your curry a dark, flavorful gravy.");
                muttonMasala.setPrice(new BigDecimal("70.00"));
                muttonMasala.setStockQuantity(80);
                muttonMasala.setCategory("Masalas");
                muttonMasala.setImageUrl("/images/masala/mutton_masala.png");

                Product sambarPowder = new Product();
                sambarPowder.setName("Sambar Powder");
                sambarPowder.setDescription("Traditional Madras sambar powder made with premium coriander, chilies, and lentils. Perfect for daily use.");
                sambarPowder.setPrice(new BigDecimal("90.00"));
                sambarPowder.setStockQuantity(120);
                sambarPowder.setCategory("Masalas");
                sambarPowder.setImageUrl("/images/masala/sambar_powder.png");

                Product rasamPowder = new Product();
                rasamPowder.setName("Rasam Powder");
                rasamPowder.setDescription("Aromatic rasam powder with the right balance of black pepper, cumin, and coriander. Good for digestion.");
                rasamPowder.setPrice(new BigDecimal("80.00"));
                rasamPowder.setStockQuantity(100);
                rasamPowder.setCategory("Masalas");
                rasamPowder.setImageUrl("/images/masala/rasam_powder.png");

                Product turmericPowder = new Product();
                turmericPowder.setName("Turmeric Powder");
                turmericPowder.setDescription("100% pure Salem turmeric, freshly ground. High in curcumin, bright yellow color, and free from artificial colors.");
                turmericPowder.setPrice(new BigDecimal("50.00"));
                turmericPowder.setStockQuantity(150);
                turmericPowder.setCategory("Masalas");
                turmericPowder.setImageUrl("/images/masala/termaric_powder.png");

                Product chilliPowder = new Product();
                chilliPowder.setName("Red Chilli Powder");
                chilliPowder.setDescription("Fiery red chilli powder made from sun-dried Guntur chilies. Adds both brilliant color and heat to your dishes.");
                chilliPowder.setPrice(new BigDecimal("80.00"));
                chilliPowder.setStockQuantity(100);
                chilliPowder.setCategory("Masalas");
                chilliPowder.setImageUrl("/images/masala/chilli_powder.png");

                Product corianderPowder = new Product();
                corianderPowder.setName("Coriander Powder");
                corianderPowder.setDescription("Freshly ground coriander seeds (Dhania). An essential base spice for all Indian gravies and curries.");
                corianderPowder.setPrice(new BigDecimal("60.00"));
                corianderPowder.setStockQuantity(100);
                corianderPowder.setCategory("Masalas");
                corianderPowder.setImageUrl("/images/masala/coriander_powder.png");

                Product masalaCombo = new Product();
                masalaCombo.setName("Complete Masala Combo");
                masalaCombo.setDescription("A value pack containing Sambar, Rasam, Turmeric, Chilli, and Coriander powders. Everything you need for an Indian kitchen.");
                masalaCombo.setPrice(new BigDecimal("320.00"));
                masalaCombo.setStockQuantity(30);
                masalaCombo.setCategory("Masalas");
                masalaCombo.setImageUrl("/images/masala/masala_combo.png");

                // Save all products to the database
                productRepository.saveAll(List.of(
                        coconutOil, groundnutOil, sesameOil, castorOil,
                        chickenMasala, muttonMasala, sambarPowder, rasamPowder,
                        turmericPowder, chilliPowder, corianderPowder, masalaCombo
                ));

                System.out.println("Data seeding completed successfully!");
            }
        };
    }
}
