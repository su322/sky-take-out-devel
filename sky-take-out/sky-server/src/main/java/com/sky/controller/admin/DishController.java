package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    @CacheEvict(value = "dishCache", key = "#dishDTO.categoryId")
    public Result addDishWithFlavor(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);

        dishService.addDishWithFlavor(dishDTO);

        return Result.success();
    }

    /**
     * 菜品分页查询
     * TODO 图片的回显需要阿里云权限开启公共读
     *
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);

        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    @CacheEvict(value = "dishCache", allEntries = true)
    public Result deleteBatch(@RequestParam List<Long> ids) {
        log.info("批量删除菜品：{}", (Object) ids);

        dishService.deleteDishWithFlavor(ids);

        return Result.success();
    }

    /**
     * 修改菜品
     * TODO 图片的回显需要阿里云权限开启公共读
     *
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    @CacheEvict(value = "dishCache", allEntries = true)
    public Result editDishWithFlavor(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);

        dishService.editDishWithFlavor(dishDTO);

        return Result.success();
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getDishById(@PathVariable Long id) {
        log.info("根据id查询菜品：{}", id);

        DishVO dishVO = dishService.getDishById(id);

        return Result.success(dishVO);
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getDishByCategoryId(Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);

        List<Dish> list = dishService.getDishByCategoryId(categoryId);

        return Result.success(list);
    }

    /**
     * 菜品起售/停售
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    @CacheEvict(value = "dishCache", allEntries = true)
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("菜品：{},启用(1)/禁用(0)：{}", id, status);

        dishService.startOrStop(status, id);

        return Result.success();
    }
}
