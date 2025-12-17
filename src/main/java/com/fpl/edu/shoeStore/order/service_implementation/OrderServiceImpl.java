package com.fpl.edu.shoeStore.order.service_implementation;

import com.fpl.edu.shoeStore.order.converter.OrderConverter;
import com.fpl.edu.shoeStore.order.dto.OrderCreateRequest;
import com.fpl.edu.shoeStore.order.dto.OrderResponse;
import com.fpl.edu.shoeStore.order.exception.OrderException;
import com.fpl.edu.shoeStore.order.mapper.OrderMapper;
import com.fpl.edu.shoeStore.order.model.Order;
import com.fpl.edu.shoeStore.order.model.OrderItem;
import com.fpl.edu.shoeStore.order.servicei_nterface.OrderService;
import com.fpl.edu.shoeStore.order.servicei_nterface.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// *Lưu ý: Cần import Product Service và Voucher Service để xử lý nghiệp vụ đầy đủ*

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderConverter orderConverter;
    // @Autowired private final ProductService productService; // Giả định có ProductService
    // @Autowired private final VoucherService voucherService; // Giả định có VoucherService

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, OrderConverter orderConverter) {
        this.orderMapper = orderMapper;
        this.orderConverter = orderConverter;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) throws OrderException {
        // 1. CHUYỂN ĐỔI: DTO -> Entity
        Order order = orderConverter.toEntity(request);
        List<OrderItem> items = request.getItems().stream()
                .map(orderConverter::toItemEntity)
                .toList();

        // 2. NGHIỆP VỤ & TÍNH TOÁN GIÁ
        BigDecimal totalGoodsValue = BigDecimal.ZERO;

        for (OrderItem item : items) {
            // TODO: Lấy thông tin sản phẩm (giá, tên) từ ProductService dựa trên variantId
            // TODO: Kiểm tra tồn kho (quantity available)
            // Lấy giá thực tế (unitPrice) và tên sản phẩm (productNameSnapshot)
            // item.setUnitPrice(productService.getPrice(item.getVariantId()));
            // item.setProductNameSnapshot(productService.getName(item.getVariantId()));

            // Giả định giá
            BigDecimal unitPrice = new BigDecimal("1000000"); // Giá giả định
            item.setUnitPrice(unitPrice);
            item.setProductNameSnapshot("Giày XYZ - Variant " + item.getVariantId());

            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setTotalPrice(itemTotal);
            totalGoodsValue = totalGoodsValue.add(itemTotal);
        }

        order.setTotalAmount(totalGoodsValue);

        // 3. ÁP DỤNG VOUCHER (Nếu có)
        BigDecimal discount = BigDecimal.ZERO;
        if (request.getVoucherId() != null) {
            // TODO: Áp dụng logic voucher từ VoucherService
            // discount = voucherService.calculateDiscount(request.getVoucherId(), totalGoodsValue);
        }
        order.setDiscountAmount(discount);

        // 4. TÍNH TỔNG SỐ TIỀN CUỐI CÙNG
        BigDecimal finalAmount = totalGoodsValue
                .subtract(discount)
                .add(request.getShippingFee());
        order.setFinalAmount(finalAmount);

        // 5. LƯU VÀO CSDL
        // orderMapper.insertOrder(order); // Lưu Order chính (MyBatis sẽ cập nhật orderId)
        // for (OrderItem item : items) {
        //    item.setOrderId(order.getOrderId());
        //    orderMapper.insertOrderItem(item); // Lưu Order Items
        // }
        // TODO: Giảm tồn kho (productService.decreaseStock(items));

        // 6. CHUYỂN ĐỔI: Entity -> Response DTO và trả về
        return orderConverter.toResponse(order, items);
    }

    @Override
    public OrderResponse getOrderDetails(int orderId) throws OrderException {
        // Order order = orderMapper.findById(orderId);
        // List<OrderItem> items = orderMapper.findItemsByOrderId(orderId);

        // if (order == null) {
        //     throw new OrderException("Order not found with ID: " + orderId);
        // }

        // Ví dụ dữ liệu giả
        Order order = new Order();
        order.setOrderId(orderId);
        order.setBuyerId(10);
        order.setStatus("PENDING");
        order.setTotalAmount(new BigDecimal("2000000"));
        order.setDiscountAmount(new BigDecimal("100000"));
        order.setShippingFee(new BigDecimal("30000"));
        order.setFinalAmount(new BigDecimal("1930000"));

        OrderItem item1 = new OrderItem();
        item1.setOrderItemId(1);
        item1.setVariantId(101);
        item1.setQuantity(2);
        item1.setUnitPrice(new BigDecimal("1000000"));
        item1.setTotalPrice(new BigDecimal("2000000"));
        item1.setProductNameSnapshot("Giày Performance X");

        List<OrderItem> items = List.of(item1);

        return orderConverter.toResponse(order, items);
    }

    @Override
    public void updateOrderStatus(int orderId, String newStatus) throws OrderException {
        // int result = orderMapper.updateStatus(orderId, newStatus);
        // if (result == 0) {
        //     throw new OrderException("Order not found or status update failed for ID: " + orderId);
        // }
        System.out.println("Order ID " + orderId + " status updated to " + newStatus);
    }
}
