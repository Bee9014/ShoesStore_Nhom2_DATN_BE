package com.fpl.edu.shoeStore.order.service.impl;

import com.fpl.edu.shoeStore.order.converter.OrderConverter;
import com.fpl.edu.shoeStore.order.dto.request.OrderCreateRequest;
import com.fpl.edu.shoeStore.order.dto.response.OrderResponse;
import com.fpl.edu.shoeStore.order.exception.OrderException;
import com.fpl.edu.shoeStore.order.mapper.OrderMapper;
import com.fpl.edu.shoeStore.order.model.Order;
import com.fpl.edu.shoeStore.order.model.OrderItem;
import com.fpl.edu.shoeStore.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderConverter orderConverter;
    // Giả định bạn đã có các Mapper này để truy vấn DB
    // private final ProductVariantMapper variantMapper;
    // private final VoucherMapper voucherMapper;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, OrderConverter orderConverter) {
        this.orderMapper = orderMapper;
        this.orderConverter = orderConverter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(OrderCreateRequest request) throws OrderException {
        // 1. CHUYỂN ĐỔI: Request -> Entity ban đầu
        Order order = orderConverter.toEntity(request);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // 2. TÍNH TOÁN GIÁ DỰA TRÊN DỮ LIỆU DB
        BigDecimal totalGoodsValue = BigDecimal.ZERO;
        List<OrderItem> items = request.getItems().stream().map(itemReq -> {
            // TRUY VẤN DB: Lấy thông tin phiên bản sản phẩm thực tế
            // var variant = variantMapper.findById(itemReq.getVariantId());
            // if (variant == null || variant.getQtyAvailable() < itemReq.getQuantity()) {
            //     throw new RuntimeException("Sản phẩm không tồn tại hoặc hết hàng");
            // }

            OrderItem item = orderConverter.toItemEntity(itemReq);
            BigDecimal priceFromDb = new BigDecimal("1000000"); // Thay bằng: variant.getPrice();

            item.setUnitPrice(priceFromDb);
            item.setTotalPrice(priceFromDb.multiply(BigDecimal.valueOf(itemReq.getQuantity())));
            item.setProductNameSnapshot("Tên giày từ DB"); // Thay bằng: variant.getTitle();

            return item;
        }).collect(Collectors.toList());

        for (OrderItem item : items) {
            totalGoodsValue = totalGoodsValue.add(item.getTotalPrice());
        }
        order.setTotalAmount(totalGoodsValue);

        // 3. KIỂM TRA VOUCHER TỪ DB
        BigDecimal discount = BigDecimal.ZERO;
        if (request.getVoucherId() != null) {
            // var voucher = voucherMapper.findById(request.getVoucherId());
            // if (voucher != null && totalGoodsValue.compareTo(voucher.getMinSpend()) >= 0) {
            //     discount = voucher.getValue(); // Logic tính % hoặc số tiền cố định
            // }
        }
        order.setDiscountAmount(discount);

        // 4. TÍNH TỔNG TIỀN CUỐI CÙNG
        order.setFinalAmount(totalGoodsValue.subtract(discount).add(request.getShippingFee()));

        // 5. LƯU THỰC TẾ XUỐNG DB
        orderMapper.insertOrder(order); // MyBatis trả về orderId sau khi insert

        for (OrderItem item : items) {
            item.setOrderId(order.getOrderId());
            orderMapper.insertOrderItem(item);
        }

        return orderConverter.toResponse(order, items);
    }

    @Override
    public OrderResponse getOrderDetails(int orderId) throws OrderException {
        // LẤY DỮ LIỆU THỰC TẾ TỪ DB
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new OrderException("Không tìm thấy đơn hàng ID: " + orderId);
        }

        // LẤY CHI TIẾT SẢN PHẨM TỪ DB
        List<OrderItem> items = orderMapper.findItemsByOrderId(orderId);

        return orderConverter.toResponse(order, items);
    }

    @Override
    @Transactional
    public void updateOrderStatus(int orderId, String newStatus) throws OrderException {
        // CẬP NHẬT TRẠNG THÁI TRỰC TIẾP TRÊN DB
        int affectedRows = orderMapper.updateStatus(orderId, newStatus);
        if (affectedRows == 0) {
            throw new OrderException("Cập nhật thất bại, không tìm thấy đơn hàng.");
        }
    }
}