package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO
     */
    void addDishWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    DishVO getDishById(Long id);

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    List<Dish> getDishByCategoryId(Long categoryId);

    /**
     * 修改菜品
     *
     * @param dishDTO
     */
    void editDishWithFlavor(DishDTO dishDTO);

    /**
     * 起售停售菜品
     *
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 批量删除菜品
     *
     * @param ids
     */
    void deleteDishWithFlavor(List<Long> ids);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
