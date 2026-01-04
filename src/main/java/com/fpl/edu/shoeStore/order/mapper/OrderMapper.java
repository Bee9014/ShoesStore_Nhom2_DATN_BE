package com.fpl.edu.shoeStore.order.mapper;

import java.util.List;

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
     * Lấy danh sách đơn hàng đã mua của một người dùng cụ thể, sắp xếp theo ngày mới nhất.
     * @param userId ID của người mua hàng.
     * @return Danh sách đơn hàng của người dùng đó.
     */
    List<Order> findByBuyerId(@Param("userId") int userId);

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
}