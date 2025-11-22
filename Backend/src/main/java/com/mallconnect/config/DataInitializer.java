package com.mallconnect.config;

import com.mallconnect.model.Category;
import com.mallconnect.model.Product;
import com.mallconnect.model.Role;
import com.mallconnect.model.User;
import com.mallconnect.repository.CategoryRepository;
import com.mallconnect.repository.ProductRepository;
import com.mallconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            log.info("Data already initialized, skipping...");
            return;
        }

        log.info("Seeding database with initial data...");

        // Create users
        User admin = createUser("Admin", "User", "admin@mallconnect.com", "admin123", Role.ROLE_ADMIN);
        User seller = createUser("John", "Seller", "seller@mallconnect.com", "seller123", Role.ROLE_SELLER);
        User seller2 = createUser("Jane", "Shop", "jane@mallconnect.com", "seller123", Role.ROLE_SELLER);
        User buyer = createUser("Mike", "Buyer", "buyer@mallconnect.com", "buyer123", Role.ROLE_BUYER);

        // Create categories
        Category electronics = createCategory("Electronics", "Phones, laptops, gadgets and more");
        Category fashion = createCategory("Fashion", "Clothing, accessories, and footwear");
        Category home = createCategory("Home & Kitchen", "Furniture, decor, and kitchen essentials");
        Category sports = createCategory("Sports & Outdoors", "Equipment for all your favorite sports");
        Category books = createCategory("Books", "Fiction, non-fiction, and educational materials");
        Category beauty = createCategory("Beauty & Health", "Skincare, makeup, and wellness products");

        // Create products
        createProduct("Wireless Bluetooth Headphones", "Premium noise-cancelling wireless headphones with 30-hour battery life, crystal-clear audio, and comfortable over-ear design.", new BigDecimal("79.99"), 150, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400", electronics, seller);
        createProduct("Smart Watch Pro", "Advanced smartwatch with heart rate monitoring, GPS tracking, sleep analysis, and 7-day battery life.", new BigDecimal("249.99"), 80, "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400", electronics, seller);
        createProduct("USB-C Fast Charger 65W", "GaN technology ultra-compact fast charger compatible with laptops, tablets, and smartphones.", new BigDecimal("35.99"), 200, "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=400", electronics, seller2);
        createProduct("Portable Bluetooth Speaker", "Waterproof portable speaker with 360° surround sound, 20-hour battery, and built-in microphone.", new BigDecimal("59.99"), 120, "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=400", electronics, seller);

        createProduct("Classic Leather Jacket", "Genuine leather jacket with a timeless design, soft inner lining, and premium YKK zippers.", new BigDecimal("189.99"), 45, "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400", fashion, seller2);
        createProduct("Casual Cotton T-Shirt", "100% organic cotton crew neck t-shirt, pre-shrunk, available in multiple colors.", new BigDecimal("24.99"), 300, "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400", fashion, seller2);
        createProduct("Running Sneakers Ultra", "Lightweight running shoes with responsive cushioning, breathable mesh upper, and durable rubber outsole.", new BigDecimal("129.99"), 90, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400", fashion, seller);
        createProduct("Designer Sunglasses", "UV400 protection polarized sunglasses with lightweight titanium frame.", new BigDecimal("149.99"), 60, "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=400", fashion, seller);

        createProduct("Minimalist Desk Lamp", "LED desk lamp with adjustable brightness, color temperature, and USB charging port.", new BigDecimal("45.99"), 100, "https://images.unsplash.com/photo-1507473885765-e6ed057ab6fe?w=400", home, seller);
        createProduct("Stainless Steel Cookware Set", "10-piece professional cookware set with non-stick coating and heat-resistant handles.", new BigDecimal("199.99"), 40, "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400", home, seller);
        createProduct("Memory Foam Pillow Set", "Ergonomic memory foam pillows with cooling gel layer, hypoallergenic cover, set of 2.", new BigDecimal("54.99"), 150, "https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?w=400", home, seller2);
        createProduct("Indoor Plant Collection", "Set of 4 easy-care indoor plants in decorative ceramic pots, air-purifying varieties.", new BigDecimal("39.99"), 70, "https://images.unsplash.com/photo-1485955900006-10f4d324d411?w=400", home, seller2);

        createProduct("Yoga Mat Premium", "Extra thick 6mm non-slip yoga mat with carrying strap, eco-friendly TPE material.", new BigDecimal("34.99"), 200, "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=400", sports, seller);
        createProduct("Adjustable Dumbbell Set", "Space-saving adjustable dumbbells 5-52.5 lbs, quick-change weight system.", new BigDecimal("299.99"), 30, "https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?w=400", sports, seller);
        createProduct("Insulated Water Bottle", "32oz vacuum insulated stainless steel water bottle, keeps drinks cold 24h or hot 12h.", new BigDecimal("27.99"), 250, "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400", sports, seller2);

        createProduct("Bestselling Fiction Novel", "Award-winning page-turner that has captivated millions of readers worldwide.", new BigDecimal("14.99"), 500, "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400", books, seller2);
        createProduct("Programming Mastery Guide", "Comprehensive guide to modern software development with hands-on projects and best practices.", new BigDecimal("49.99"), 180, "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400", books, seller2);

        createProduct("Organic Face Serum", "Vitamin C + Hyaluronic Acid face serum, brightening and anti-aging, 30ml.", new BigDecimal("29.99"), 160, "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=400", beauty, seller);
        createProduct("Natural Lip Balm Set", "Set of 6 organic beeswax lip balms in assorted flavors, moisturizing and protective.", new BigDecimal("12.99"), 400, "https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=400", beauty, seller);

        log.info("Database seeded successfully: {} users, {} categories, {} products",
                userRepository.count(), categoryRepository.count(), productRepository.count());
    }

    private User createUser(String firstName, String lastName, String email, String password, Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return userRepository.findByEmail(email).get();
        }
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone("+1234567890")
                .password(passwordEncoder.encode(password))
                .role(role)
                .enabled(true)
                .build();
        return userRepository.save(user);
    }

    private Category createCategory(String name, String description) {
        if (categoryRepository.findByName(name).isPresent()) {
            return categoryRepository.findByName(name).get();
        }
        Category category = Category.builder()
                .name(name)
                .description(description)
                .active(true)
                .build();
        return categoryRepository.save(category);
    }

    private Product createProduct(String name, String description, BigDecimal price,
                                   int stock, String imageUrl, Category category, User seller) {
        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .imageUrl(imageUrl)
                .active(true)
                .category(category)
                .seller(seller)
                .build();
        return productRepository.save(product);
    }
}
