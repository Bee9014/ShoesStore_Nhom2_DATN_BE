package com.fpl.edu.shoeStore.category.mapper;

import com.fpl.edu.shoeStore.category.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> findAll();

    Category findById(@Param("categoryId") Integer categoryId);

    int insert(Category category);

    int update(Category category);

    int deleteById(@Param("categoryId") Integer categoryId);

    List<Category> findAllPaged(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            @Param("offset") int offset,
            @Param("size") int size
    );

    long countAll(@Param("search") String search, @Param("isActive") Boolean isActive);
}