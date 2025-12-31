 package com.fpl.edu.shoeStore.product.mapper;

     import java.util.List;

     import org.apache.ibatis.annotations.Mapper;
     import org.apache.ibatis.annotations.Param;

     import com.fpl.edu.shoeStore.product.entity.Product;

     @Mapper
     public interface ProductMapper {
         List<Product> findAll();

         Product findById(@Param("productId") Integer productId);           // Đổi Long → Integer

         Product findByTitle(@Param("title") String title);                 // Đổi từ findByName

         int insert(Product product);

         int update(Product product);

         int deleteById(@Param("productId") Integer productId);             // Đổi Long → Integer

         List<Product> findAllPaged(
             @Param("categoryId") Integer categoryId,
             @Param("title") String title,
             @Param("status") String status,
             @Param("isActive") Boolean isActive,
             @Param("offset") int offset,
             @Param("size") int size
         );

         long countAll(
             @Param("categoryId") Integer categoryId,
             @Param("title") String title,
             @Param("status") String status,
             @Param("isActive") Boolean isActive
         );



         void incrementViewCount(Integer productId);

    List<Product> findTopFeatured();

    List<Product> findBestSellers();

    /**
     * Count total products for dashboard
     */
    Long countAllProducts();
    
    // ==================== SEARCH & FILTER ====================
    
    /**
     * Find best selling products with pagination
     * @param limit Page size
     * @param offset Page offset
     * @return List of products sorted by total sold
     */
    List<Product> findBestFiftySellers(
        @Param("limit") Integer limit,
        @Param("offset") Integer offset
    );
    
    /**
     * Count total best sellers for pagination
     */
    Long countBestSellers();
    
    /**
     * Search products by brand or product name
     * @param keyword Search keyword
     * @param limit Page size
     * @param offset Page offset
     * @return List of matching products
     */
    List<Product> findProductsBySearch(
        @Param("keyword") String keyword,
        @Param("limit") Integer limit,
        @Param("offset") Integer offset
    );
    
    /**
     * Count search results for pagination
     * @param keyword Search keyword
     * @return Total count of matching products
     */
    Long countSearchResults(@Param("keyword") String keyword);

     }
