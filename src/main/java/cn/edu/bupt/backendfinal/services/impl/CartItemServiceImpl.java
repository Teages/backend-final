package cn.edu.bupt.backendfinal.services.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.edu.bupt.backendfinal.entity.CartItem;
import cn.edu.bupt.backendfinal.mapper.CartItemMapper;
import cn.edu.bupt.backendfinal.services.CartItemService;

@Service
public class CartItemServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartItemService {
  
}
