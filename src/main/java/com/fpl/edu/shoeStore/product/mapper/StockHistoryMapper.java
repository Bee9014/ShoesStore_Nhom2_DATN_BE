package com.fpl.edu.shoeStore.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.product.entity.StockHistory;

@Mapper
public interface StockHistoryMapper {

    /**
     * Ghi lại một bản ghi lịch sử biến động kho hàng (Nhập hàng hoặc Xuất hàng).
     * * Lưu ý cho SQL Server XML: 
     * - Cột 'created_at' nên sử dụng hàm GETDATE() trực tiếp trong câu SQL.
     * - Không chèn giá trị vào cột ID nếu nó là IDENTITY(1,1).
     */
    void insert(StockHistory history);
    
    /**
     * Truy xuất toàn bộ lịch sử thay đổi số lượng của một biến thể sản phẩm cụ thể.
     * * @param variantId: ID của biến thể cần xem lịch sử (ví dụ: Giày Nike Size 42 Trắng).
     * @return Danh sách các bản ghi lịch sử sắp xếp theo thời gian mới nhất.
     */
    List<StockHistory> findByVariantId(@Param("variantId") Integer variantId);
}