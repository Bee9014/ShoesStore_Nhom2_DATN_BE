 package com.fpl.edu.shoeStore.product.mapper;

     import com.fpl.edu.shoeStore.product.entity.Product;
     import org.apache.ibatis.annotations.Mapper;
     import org.apache.ibatis.annotations.Param;

     import java.util.List;

     @Mapper
     public interface ProductMapper {
         List<Product> findAll();

         Product findById(@Param("productId") Integer productId);           // Đổi Long → Integer

         Product findByTitle(@Param("title") String title);                 // Đổi từ findByName

         int insert(Product product);

         int update(Product product);

         int deleteById(@Param("productId") Integer productId);             // Đổi Long → Integer

         List<Product> findAllPaged(
             @Param("categoryId") Integer categoryId,                       // Đổi Long → Integer
             @Param("title") String title,                                  // Đổi từ name → title
             @Param("status") String status,                                // Đổi từ Boolean isActive → String status
             @Param("offset") int offset,
             @Param("size") int size
         );

         long countAll(
             @Param("categoryId") Integer categoryId,                       // Đổi Long → Integer
             @Param("title") String title,                                  // Đổi từ name → title
             @Param("status") String status                                 // Đổi từ Boolean isActive → String status
         );
     }
