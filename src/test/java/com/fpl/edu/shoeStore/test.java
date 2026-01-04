package com.fpl.edu.shoeStore;

import com.fpl.edu.shoeStore.product.entity.Product;
import com.fpl.edu.shoeStore.product.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Nạp toàn bộ cấu hình Spring để kết nối DB thật
class test {

    @Autowired
    private ProductMapper productMapper;

    @Test
    void testGetProductFromDatabase() {
        // ID 1001 là sản phẩm đầu tiên bạn đã INSERT trong ShopStore.sql
        Integer targetId = 1001;

        // Hành động: Gọi vào Database thật
        Product product = productMapper.findById(targetId);

        // Kiểm tra dữ liệu lấy ra có khớp với file SQL không
        assertNotNull(product, "Không tìm thấy sản phẩm ID 1001 trong Database!");
        assertEquals("Dép thời trang", product.getTitle(), "Tên sản phẩm không khớp!");
        assertEquals("D001", product.getProductCode(), "Mã sản phẩm không khớp!");

        // In ra console để xác nhận mắt thấy tai nghe
        System.out.println("--- KẾT QUẢ TEST ---");
        System.out.println("ID: " + product.getProductId());
        System.out.println("Tên: " + product.getTitle());
        System.out.println("Lượt xem hiện tại: " + product.getViewCount());

        // Kiểm tra nếu có variants đi kèm (nếu Mapper của bạn có join bảng)
        if (product.getVariants() != null) {
            System.out.println("Số lượng biến thể: " + product.getVariants().size());
        }
    }
}