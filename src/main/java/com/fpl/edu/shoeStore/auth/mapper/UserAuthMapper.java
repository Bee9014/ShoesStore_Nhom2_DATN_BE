package com.fpl.edu.shoeStore.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.auth.entity.UserAuth;
import com.fpl.edu.shoeStore.user.entity.User;

@Mapper
public interface UserAuthMapper {

    /**
     * Tìm kiếm thông tin đăng nhập bao gồm cả tên Role.
     * Khớp với <select id="findUserByUsername" resultMap="UserAuthResultMap">
     */
    UserAuth findUserByUsername(@Param("username") String username);
    
    /**
     * Lấy Role ID cho người dùng đang hoạt động (status = 'active').
     * Khớp với <select id="getRoleIdByUsername" resultType="java.lang.Integer">
     */
    Integer getRoleIdByUsername(@Param("username") String username);
    
    /**
     * Trong XML dùng COUNT(*), trả về Integer sẽ an toàn hơn.
     * MyBatis sẽ tự hiểu: 0 là false, >0 là true nếu bạn vẫn muốn để boolean.
     * Nhưng để khớp nhất với resultType="java.lang.Integer", tôi khuyên dùng Integer hoặc int.
     */
    int existsByUsername(@Param("username") String username);
    
    int existsByEmail(@Param("email") String email);
    
    int existsByPhone(@Param("phone") String phone);
    
    /**
     * Thêm mới người dùng. 
     * XML sử dụng useGeneratedKeys="true" nên sau khi chạy, 
     * ID sẽ được tự động fill vào object user.
     * Trả về số dòng bị ảnh hưởng (thường là 1).
     */
    int insertUser(User user);
}