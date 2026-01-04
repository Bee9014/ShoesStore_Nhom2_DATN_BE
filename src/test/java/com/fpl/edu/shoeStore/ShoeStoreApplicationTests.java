package com.fpl.edu.shoeStore;

import com.fpl.edu.shoeStore.product.convert.ProductConverter;
import com.fpl.edu.shoeStore.product.dto.response.ProductDetailDtoResponse;
import com.fpl.edu.shoeStore.product.entity.Product;
import com.fpl.edu.shoeStore.product.entity.ProductVariant;
import com.fpl.edu.shoeStore.product.mapper.ProductMapper;
import com.fpl.edu.shoeStore.voucher.dto.response.VoucherDTOResponse;
import com.fpl.edu.shoeStore.voucher.entity.Voucher;
import com.fpl.edu.shoeStore.voucher.mapper.VoucherMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShoeStoreApplicationTests {

    @Autowired
    private VoucherMapper voucherMapper;

    @Test
    void testFindById() {
        Voucher v = voucherMapper.findById(1);
        assertNotNull(v); // Kiểm tra đối tượng không null
        assertNotNull(v.getCode()); // Kiểm tra xem code đã được map vào chưa
        System.out.println(v.toString()); // Xem toàn bộ dữ liệu đã map
    }

    @Test
    void testFindByCode() {
        List<Voucher> vouchers = voucherMapper.findByCode("sale");
        assertNotNull(vouchers);
        System.out.println(vouchers.toString());
    }

    @Test
    void testfindAll() {
        List<Voucher> vouchers = voucherMapper.findAll();
        assertNotNull(vouchers);
        List<Voucher> listTest;
        System.out.println(vouchers);
    }

    @Test
    void testMappingProductToResponse() {
        // 1. Giả lập dữ liệu (Dùng đúng BigDecimal để khớp với Entity)
        Product entity = Product.builder()
                .productId(1001)
                .title("Dép thời trang")
                .basePrice(new BigDecimal("199000"))
                .viewCount(50L)
                .build();

        List<ProductVariant> variants = List.of(
                ProductVariant.builder()
                        .variantId(1)
                        .size("38")
                        .color("Đen")
                        .price(new BigDecimal("199000")) // Đảm bảo kiểu BigDecimal
                        .build()
        );

        // 2. Chạy hàm convert
        ProductDetailDtoResponse response = ProductConverter.toDetailResponse(entity, variants);

        // 3. Kiểm tra kết quả (Assert)
        assertNotNull(response);
        assertEquals("Dép thời trang", response.getTitle());
        assertEquals(1, response.getVariants().size());

        // 4. So sánh giá trị BigDecimal (Sửa lỗi 199000.0 vs 199000)
        // Cách 1: Sử dụng compareTo (Trả về 0 nếu giá trị bằng nhau, bất kể scale)
        assertTrue(new BigDecimal("199000").compareTo(response.getVariants().get(0).getPrice()) == 0);

        // Cách 2: Nếu dùng assertEquals, hãy đảm bảo khởi tạo đúng đối tượng BigDecimal
        // assertEquals(new BigDecimal("199000"), response.getVariants().get(0).getPrice());
    }

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
