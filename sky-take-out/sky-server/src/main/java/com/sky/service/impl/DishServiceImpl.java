package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO
     */
    @Transactional
    public void addDishWithFlavor(DishDTO dishDTO) {
        //向菜品表插入1条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insertDish(dish);

        //获取insert语句生成的主键值
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor df : flavors) {
                df.setDishId(dishId);
            }
            //向口味表插入n条数据
            dishFlavorMapper.insertBatchFlavors(flavors);
        }

    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    public DishVO getDishById(Long id) {
        return dishMapper.selectById(id);
    }

    /**
     * 根据分类id查询菜品
     * TODO 和他不同的地方在于他只查了起售状态的菜品，我这里查了所有的菜品，而且他查的时候还带if，但是文档里也没有写他要干嘛
     *
     * @param categoryId
     * @return
     */
    public List<Dish> getDishByCategoryId(Long categoryId) {
        return dishMapper.selectByCategoryId(categoryId);
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     */
    public void editDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateDish(dish);

        Long dishId = dishDTO.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //先删除原来的口味数据
        dishFlavorMapper.deleteByDishId(dishId);
        //再插入新的口味数据
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor df : flavors) {
                df.setDishId(dishId);
            }
            //向口味表插入n条数据
            dishFlavorMapper.insertBatchFlavors(flavors);
        }
    }

    /**
     * 起售停售菜品
     *
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.updateDish(dish);
    }

    /**
     * 批量删除菜品
     * TODO 删除套餐之后图片不会从阿里云删除，需要手动删除
     *
     * @param ids
     */
    public void deleteDishWithFlavor(List<Long> ids) {
        //判断菜品是否起售，如果起售则不能删除
        for (Long dishId : ids) {
            DishVO dishVO = dishMapper.selectById(dishId);//这里用dish, dishVO, dishDTO都可以
            if (dishVO.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断菜品是否关联了套餐，如果关联了套餐则不能删除
        List<Long> setmealIds = setmealDishMapper.selectSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品表中的数据
        for (Long dishId : ids) {
            dishMapper.deleteById(dishId);
            //删除口味表中的数据
            dishFlavorMapper.deleteByDishId(dishId);
        }
    }

    /**
     * 条件查询菜品和口味
     *
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
