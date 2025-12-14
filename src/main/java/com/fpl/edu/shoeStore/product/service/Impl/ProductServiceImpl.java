 package com.fpl.edu.shoeStore.product.service.Impl;

     import java.time.LocalDate;
     import java.util.List;
     import java.util.stream.Collectors;

     import org.springframework.stereotype.Service;
     import org.springframework.transaction.annotation.Transactional;

     import com.fpl.edu.shoeStore.common.handler.PageResponse;
     import com.fpl.edu.shoeStore.product.convert.ProductConverter;
     import com.fpl.edu.shoeStore.product.dto.request.ProductDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductDtoResponse;
     import com.fpl.edu.shoeStore.product.entity.Product;
     import com.fpl.edu.shoeStore.product.mapper.ProductMapper;
     import com.fpl.edu.shoeStore.product.service.ProductService;

     import lombok.RequiredArgsConstructor;

     @Service
     @RequiredArgsConstructor
     public class ProductServiceImpl implements ProductService {

         private final ProductMapper productMapper;

         @Override
         @Transactional
         public ProductDtoResponse createProduct(ProductDtoRequest request) {
             Product product = ProductConverter.toEntity(request);
             product.setCreateAt(LocalDate.now());           // Đổi từ LocalDateTime
             product.setUpdateAt(LocalDate.now());           // Đổi từ LocalDateTime
             productMapper.insert(product);
             return ProductConverter.toResponse(product);
         }

         @Override
         @Transactional
         public ProductDtoResponse updateProduct(Integer id, ProductDtoRequest request) {
             Product existing = productMapper.findById(id);
             if (existing == null) {
                 throw new RuntimeException("Không tìm thấy Product id = " + id);
             }

             if (request.getCategoryId() != null) existing.setCategoryId(request.getCategoryId());
             if (request.getTitle() != null) existing.setTitle(request.getTitle());                      // Đổi từ setName
             if (request.getDescription() != null) existing.setDescription(request.getDescription());
             if (request.getBrand() != null) existing.setBrand(request.getBrand());                      // THÊM MỚI
             if (request.getCondition() != null) existing.setCondition(request.getCondition());          // THÊM MỚI
             if (request.getDefaultImage() != null) existing.setDefaultImage(request.getDefaultImage()); // THÊM MỚI
             if (request.getStatus() != null) existing.setStatus(request.getStatus());                   // Đổi từ setIsActive
             if (request.getUpdateBy() != null) existing.setUpdateBy(request.getUpdateBy());             // THÊM MỚI

             existing.setUpdateAt(LocalDate.now());          // Đổi từ LocalDateTime
             productMapper.update(existing);

             return ProductConverter.toResponse(existing);
         }

         @Override
         @Transactional
         public int deleteProduct(Integer id) {
             Product existing = productMapper.findById(id);
             if (existing == null) {
                 throw new RuntimeException("Không tìm thấy Product để xóa");
             }
             return productMapper.deleteById(id);
         }

         @Override
         public ProductDtoResponse findById(Integer id) {
             Product product = productMapper.findById(id);
             return product == null ? null : ProductConverter.toResponse(product);
         }

         @Override
         public ProductDtoResponse findByTitle(String title) {   // Đổi từ findByName
             Product product = productMapper.findByTitle(title); // Đổi từ findByName
             return product == null ? null : ProductConverter.toResponse(product);
         }

         @Override
         public PageResponse<ProductDtoResponse> findAllPaged(
                 Integer categoryId,              // Đổi từ Long → Integer
                 String title,                    // Đổi từ name → title
                 String status,                   // Đổi từ Boolean isActive → String status
                 int page,
                 int size
         ) {
             int offset = (page - 1) * size;

             List<Product> products = productMapper.findAllPaged(
                     categoryId, title, status, offset, size  // Cập nhật tham số
             );

             long totalElements = productMapper.countAll(
                     categoryId, title, status                // Cập nhật tham số
             );

             List<ProductDtoResponse> content = products.stream()
                     .map(ProductConverter::toResponse)
                     .collect(Collectors.toList());

             int totalPages = (int) Math.ceil((double) totalElements / size);

             return PageResponse.<ProductDtoResponse>builder()
                     .content(content)
                     .pageNumber(page)
                     .pageSize(size)
                     .totalElements(totalElements)
                     .totalPages(totalPages)
                     .build();
         }
     }
