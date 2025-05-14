package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //设置查询条件，查询当前登录用户的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        if(list != null && list.size() > 0){
            //查询结果list应该只包含0或1条记录，因为：同一个用户(userId) + 同一个商品(dishId/setmealId) + 相同口味(dishFlavor)的组合在购物车中应该是唯一的
            shoppingCart = list.get(0);

            Integer number = shoppingCart.getNumber();
            if(number == 1){
                //当前商品在购物车中的份数为1，直接删除当前记录
                shoppingCartMapper.deleteById(shoppingCart.getId());
            }else {
                //当前商品在购物车中的份数不为1，修改份数即可
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }
        }
    }


    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        //dish_id setmeal_id dish_flavor
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        //如果本来就有，就修改份数
        if(list != null && list.size() > 0){
            //查询结果list应该只包含0或1条记录，因为：同一个用户(userId) + 同一个商品(dishId/setmealId) + 相同口味(dishFlavor)的组合在购物车中应该是唯一的
            shoppingCart = list.get(0);

            //当前商品在购物车中的份数不为0，修改份数即可
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(shoppingCart);
        } else {
            //如果没有，添加当前记录
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            //name image amount 还要查 是菜品还是套餐？TODO 这里连查三次看得有点怪
            if(shoppingCart.getDishId() != null) {
                shoppingCart.setName(dishMapper.getName(shoppingCart.getDishId()));
                shoppingCart.setImage(dishMapper.getImage(shoppingCart.getDishId()));
                shoppingCart.setAmount(dishMapper.getAmount(shoppingCart.getDishId()));
            } else {
                shoppingCart.setName(setmealMapper.getName(shoppingCart.getSetmealId()));
                shoppingCart.setImage(setmealMapper.getImage(shoppingCart.getSetmealId()));
                shoppingCart.setAmount(setmealMapper.getAmount(shoppingCart.getSetmealId()));
            }
            shoppingCartMapper.addShoppingCart(shoppingCart);
        }
    }

    /**
     * 查看购物车
     * @return
     */
    public List<ShoppingCart> listShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    /**
     * 清空购物车
     */
    public void cleanShoppingCart() {
        Long currentId = BaseContext.getCurrentId();
        shoppingCartMapper.clearShoppingCart(currentId);
    }
}
