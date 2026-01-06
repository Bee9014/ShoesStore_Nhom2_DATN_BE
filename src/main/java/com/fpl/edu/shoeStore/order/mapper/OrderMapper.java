package com.fpl.edu.shoeStore.order.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.order.entity.Order;
import com.fpl.edu.shoeStore.order.entity.OrderItem;

@Mapper
public interface OrderMapper {

    /**
     * Lưu thông tin tổng quan của đơn hàng (Master).
     * Tự động lấy ID phát sinh từ database gán vào đối tượng Order.
     * @param order Đối tượng đơn hàng cần lưu.
     */
    void insertOrder(Order order);

    /**
     * Lưu thông tin chi tiết từng sản phẩm trong đơn hàng (Detail).
     * @param item Đối tượng chi tiết đơn hàng.
     */
    void insertOrderItem(OrderItem item);

    /**
     * Truy vấn thông tin đơn hàng theo mã ID duy nhất.
     * @param orderId Mã đơn hàng.
     * @return Đối tượng Order hoặc null nếu không tìm thấy.
     */
    Order findById(@Param("orderId") int orderId);

    /**
     * Lấy danh sách tất cả các sản phẩm chi tiết thuộc về một đơn hàng.
     * @param orderId Mã đơn hàng cha.
     * @return Danh sách các OrderItem.
     */
    List<OrderItem> findItemsByOrderId(@Param("orderId") int orderId);

    /**
     * Cập nhật trạng thái của đơn hàng và thời gian cập nhật cuối cùng.
     * @param orderId Mã đơn hàng cần cập nhật.
     * @param status Trạng thái mới (VD: PENDING, SHIPPING, DELIVERED).
     * @return Số dòng bị ảnh hưởng.
     */
    int updateStatus(@Param("orderId") int orderId, @Param("status") String status);

    /**
     * Lấy danh sách đơn hàng đã mua của một người dùng cụ thể, hỗ trợ lọc theo trạng thái và phân trang.
     * Kết quả thường được sắp xếp theo thời gian đặt hàng (order_date) giảm dần.
     * * @param userId ID của người mua hàng (khách hàng).
     * @param status Trạng thái đơn hàng cần lọc (VD: PENDING, COMPLETED). Nếu null sẽ lấy tất cả.
     * @param offset Vị trí bắt đầu lấy bản ghi trong tập dữ liệu (phục vụ phân trang).
     * @param size Số lượng bản ghi tối đa trả về trên một trang.
     * @return Danh sách các đối tượng Order thỏa mãn điều kiện tìm kiếm.
     */
    List<Order> findByBuyerId(
            @Param("userId") int userId,
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * Đếm tổng số lượng đơn hàng của một người dùng cụ thể dựa trên trạng thái lọc.
     * Kết quả này được sử dụng để tính toán tổng số trang (totalPages) trong logic phân trang.
     * * @param userId ID của người mua hàng.
     * @param status Trạng thái đơn hàng cần lọc để đếm.
     * @return Tổng số lượng đơn hàng tìm thấy (kiểu long).
     */
    long countByBuyerId(@Param("userId") int userId, @Param("status") String status);

    /**
     * Tìm kiếm và phân trang đơn hàng dành cho giao diện quản trị Admin.
     * Hỗ trợ lọc theo trạng thái và tìm kiếm theo tên, số điện thoại hoặc mã đơn.
     * @param status Trạng thái đơn hàng cần lọc.
     * @param searchTerm Từ khóa tìm kiếm (Tên, SĐT, hoặc ID đơn hàng).
     * @param offset Vị trí bắt đầu lấy bản ghi (SQL Server OFFSET).
     * @param size Số lượng bản ghi trên một trang.
     * @return Danh sách đơn hàng thỏa mãn điều kiện.
     */
    List<Order> findAllPaged(
        @Param("status") String status,
        @Param("searchTerm") String searchTerm,
        @Param("offset") int offset,
        @Param("size") int size
    );
    /**
     * Đếm tổng số lượng đơn hàng thỏa mãn điều kiện lọc để phục vụ tính toán phân trang.
     * @param status Trạng thái đơn hàng.
     * @param searchTerm Từ khóa tìm kiếm.
     * @return Tổng số bản ghi (long).
     */
    long countAll(@Param("status") String status, @Param("searchTerm") String searchTerm);

    /**
     * Thống kê số lượng đơn hàng theo từng trạng thái cụ thể.
     * @param status Trạng thái cần đếm.
     * @return Số lượng đơn hàng có trạng thái đó.
     */
    long countByStatus(@Param("status") String status);

    // Tính tổng số đơn hàng
    Long countAllOrders();

    // Tính tổng doanh thu (chỉ tính đơn DELIVERED)
    BigDecimal calculateTotalRevenue();

    // Tính tổng doanh thu theo tháng
    BigDecimal calculateRevenueByMonth(
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    // Top sản phẩm bán chạy
    List<Map<String, Object>> findTopSellingProducts(
            @Param("limit") Integer limit
    );
}