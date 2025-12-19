package com.fpl.edu.shoeStore;

import com.fpl.edu.shoeStore.voucher.dto.response.VoucherDTOResponse;
import com.fpl.edu.shoeStore.voucher.entity.Voucher;
import com.fpl.edu.shoeStore.voucher.mapper.VoucherMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
