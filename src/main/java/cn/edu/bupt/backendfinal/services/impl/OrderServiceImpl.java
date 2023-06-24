package cn.edu.bupt.backendfinal.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.edu.bupt.backendfinal.entity.Order;
import cn.edu.bupt.backendfinal.mapper.OrderMapper;
import cn.edu.bupt.backendfinal.services.OrderService;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
  @Autowired
  OrderMapper orderMapper;
}
