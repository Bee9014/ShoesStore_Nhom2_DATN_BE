package com.fpl.edu.shoeStore.order.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.order.converter.OrderConverter;
import com.fpl.edu.shoeStore.order.dto.request.OrderCreateRequest;
import com.fpl.edu.shoeStore.order.dto.response.OrderResponse;
import com.fpl.edu.shoeStore.order.entity.Order;
import com.fpl.edu.shoeStore.order.entity.OrderItem;
import com.fpl.edu.shoeStore.order.exception.OrderException;
import com.fpl.edu.shoeStore.order.mapper.OrderMapper;
import com.fpl.edu.shoeStore.order.service.OrderService;
import com.fpl.edu.shoeStore.product.entity.Product;
import com.fpl.edu.shoeStore.product.entity.ProductVariant;
import com.fpl.edu.shoeStore.product.mapper.ProductMapper;
import com.fpl.edu.shoeStore.product.mapper.ProductVariantMapper;
import com.fpl.edu.shoeStore.user.mapper.UserMapper;
import com.fpl.edu.shoeStore.voucher.dto.response.VoucherCalcResponse;
import com.fpl.edu.shoeStore.voucher.entity.Voucher;
import com.fpl.edu.shoeStore.voucher.mapper.VoucherMapper;
import com.fpl.edu.shoeStore.voucher.service.VoucherService;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderConverter orderConverter;
    private final ProductVariantMapper variantMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    // Thêm Dependency cho Voucher
    private final VoucherService voucherService;
    private final VoucherMapper voucherMapper;

    @Autowired
    public OrderServiceImpl(
            OrderMapper orderMapper,
            OrderConverter orderConverter,
            ProductVariantMapper variantMapper,
            ProductMapper productMapper,
            UserMapper userMapper,
            VoucherService voucherService,
            VoucherMapper voucherMapper) {
        this.orderMapper = orderMapper;
        this.orderConverter = orderConverter;
        this.variantMapper = variantMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.voucherService = voucherService;
        this.voucherMapper = voucherMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(OrderCreateRequest request) throws OrderException {
        // 0. CHECK USER EXISTENCE
        if (userMapper.findById(request.getUserId()) == null) {
            throw new OrderException(
                    "Người dùng không tồn tại (ID: " + request.getUserId() + "). Vui lòng đăng nhập lại.");
        }

        // 1. CHUYỂN ĐỔI: Request -> Entity ban đầu
        Order order = orderConverter.toEntity(request);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // 2. TÍNH TOÁN GIÁ DỰA TRÊN DỮ LIỆU DB THỰC TẾ
        BigDecimal totalGoodsValue = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (var itemReq : request.getItems()) {
            // TRUY VẤN DB: Lấy thông tin variant thực tế
            ProductVariant variant = variantMapper.findById(itemReq.getVariantId());

            if (variant == null) {
                throw new OrderException("Sản phẩm không tồn tại (Variant ID: " + itemReq.getVariantId() + ")");
            }

            // Kiểm tra tồn kho
            if (variant.getStockQty() == null || variant.getStockQty() < itemReq.getQuantity()) {
                throw new OrderException(
                        "Sản phẩm không đủ số lượng trong kho (Variant ID: " + itemReq.getVariantId() + ")");
            }

            // Lấy thông tin product để có tên sản phẩm
            Product product = productMapper.findById(variant.getProductId());
            if (product == null) {
                throw new OrderException("Không tìm thấy thông tin sản phẩm");
            }

            // Tạo OrderItem với dữ liệu thật từ DB
            OrderItem item = orderConverter.toItemEntity(itemReq);

            BigDecimal unitPrice = variant.getPrice() != null ? variant.getPrice() : BigDecimal.ZERO;

            item.setUnitPrice(unitPrice);
            item.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity())));

            // Snapshot tên sản phẩm + Size + Color
            String productName = product.getTitle();
            String variantInfo = "";

            if (variant.getSize() != null && !variant.getSize().isEmpty()) {
                variantInfo += variant.getSize();
            }
            if (variant.getColor() != null && !variant.getColor().isEmpty()) {
                if (!variantInfo.isEmpty())
                    variantInfo += " - ";
                variantInfo += variant.getColor();
            }
            if (!variantInfo.isEmpty()) {
                productName += " (" + variantInfo + ")";
            }

            item.setProductNameSnapshot(productName);

            items.add(item);
            totalGoodsValue = totalGoodsValue.add(item.getTotalPrice());
        }

        order.setTotalAmount(totalGoodsValue);

        // 3. XỬ LÝ VOUCHER (Đã cập nhật Logic)
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (request.getVoucherId() != null) {
            // Lấy thông tin Voucher từ DB để lấy Code
            Voucher voucher = voucherMapper.findById(request.getVoucherId());
            if (voucher == null) {
                throw new OrderException("Voucher không tồn tại (ID: " + request.getVoucherId() + ")");
            }

            // Gọi Service tính toán (Re-validate các điều kiện như ngày, số lượng,
            // min_spend...)
            VoucherCalcResponse vCalc = voucherService.applyVoucher(
                    voucher.getCode(),
                    totalGoodsValue, // Tính giảm giá dựa trên tổng tiền hàng
                    request.getUserId());

            discountAmount = vCalc.getDiscountAmount();
            order.setVoucherId(voucher.getVoucherId());
        } else {
            order.setVoucherId(null);
        }

        order.setDiscountAmount(discountAmount);

        // 4. TÍNH TỔNG TIỀN CUỐI CÙNG
        BigDecimal shippingFee = request.getShippingFee() != null ? request.getShippingFee() : BigDecimal.ZERO;

        // Final = Total - Discount + Ship
        BigDecimal finalAmount = totalGoodsValue.subtract(discountAmount).add(shippingFee);

        // Đảm bảo không âm tiền
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }
        order.setFinalAmount(finalAmount);

        // 5. LƯU THỰC TẾ XUỐNG DB
        orderMapper.insertOrder(order); // orderId sẽ được map lại vào entity sau dòng này

        for (OrderItem item : items) {
            item.setOrderId(order.getOrderId());
            orderMapper.insertOrderItem(item);
        }

        // 6. TRỪ TỒN KHO SẢN PHẨM
        for (var itemReq : request.getItems()) {
            variantMapper.updateStock(itemReq.getVariantId(), -itemReq.getQuantity());
        }

        // 7. XỬ LÝ VOUCHER SAU KHI TẠO ĐƠN THÀNH CÔNG (Trừ lượt dùng + Lưu lịch sử)
        if (request.getVoucherId() != null) {
            // Tăng số lượng đã dùng của Voucher lên 1
            voucherMapper.incrementUsedCount(request.getVoucherId());

            // Lưu lịch sử: User A dùng Voucher B cho Đơn C
            voucherMapper.insertHistory(
                    request.getUserId(),
                    request.getVoucherId(),
                    order.getOrderId());
        }

        return orderConverter.toResponse(order, items);
    }

    // ... (Giữ nguyên các method khác bên dưới) ...
    @Override
    public OrderResponse getOrderDetails(int orderId) throws OrderException {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new OrderException("Không tìm thấy đơn hàng ID: " + orderId);
        }
        List<OrderItem> items = orderMapper.findItemsByOrderId(orderId);
        return orderConverter.toResponse(order, items);
    }

    @Override
    @Transactional
    public void updateOrderStatus(int orderId, String newStatus) throws OrderException {
        int affectedRows = orderMapper.updateStatus(orderId, newStatus);
        if (affectedRows == 0) {
            throw new OrderException("Cập nhật thất bại, không tìm thấy đơn hàng.");
        }
    }

    @Override
    public PageResponse<OrderResponse> getMyOrders(int userId, String status, int page, int size) {
        if (page < 1)
            page = 1;
        if (size < 1)
            size = 10;
        int offset = (page - 1) * size;

        List<Order> orders = orderMapper.findByBuyerId(userId, status, offset, size);
        long totalElements = orderMapper.countByBuyerId(userId, status);

        List<OrderResponse> orderResponses = orders.stream()
                .map(order -> {
                    List<OrderItem> items = orderMapper.findItemsByOrderId(order.getOrderId());
                    return orderConverter.toResponse(order, items);
                })
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<OrderResponse>builder()
                .content(orderResponses)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public PageResponse<OrderResponse> getAllOrders(String status, String searchTerm, int page, int size) {
        if (page < 1)
            page = 1;
        if (size < 1)
            size = 20;
        int offset = (page - 1) * size;

        List<Order> orders = orderMapper.findAllPaged(status, searchTerm, offset, size);
        long totalElements = orderMapper.countAll(status, searchTerm);

        List<OrderResponse> orderResponses = orders.stream()
                .map(order -> {
                    List<OrderItem> items = orderMapper.findItemsByOrderId(order.getOrderId());
                    return orderConverter.toResponse(order, items);
                })
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) totalElements / size);
        return PageResponse.<OrderResponse>builder()
                .content(orderResponses).pageNumber(page).pageSize(size)
                .totalElements(totalElements).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public void cancelOrder(int orderId, int userId) throws OrderException {
        Order order = orderMapper.findById(orderId);
        if (order == null)
            throw new OrderException("Không tìm thấy đơn hàng #" + orderId);
        if (order.getUserId() != userId) {
            throw new OrderException("Bạn không có quyền hủy đơn hàng này");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw new OrderException("Chỉ có thể hủy đơn hàng đang chờ xác nhận");
        }
        int affectedRows = orderMapper.updateStatus(orderId, "CANCELLED");
        if (affectedRows == 0)
            throw new OrderException("Hủy đơn hàng thất bại");

        // Hoàn lại kho
        List<OrderItem> items = orderMapper.findItemsByOrderId(orderId);
        for (OrderItem item : items) {
            variantMapper.updateStock(item.getVariantId(), item.getQuantity());
        }
    }

    @Override
    public PageResponse<OrderResponse> getAllOrdersForAdmin(String status, String searchTerm, int page, int size) {
        return getAllOrders(status, searchTerm, page, size);
    }

    @Override
    public long countAllOrders() {
        return orderMapper.countAll(null, null);
    }

    @Override
    public long countOrdersByStatus(String status) {
        return orderMapper.countByStatus(status);
    }
}