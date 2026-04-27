package com.example.duidui.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.duidui.common.Result;
import com.example.duidui.entity.Outbound;
import com.example.duidui.entity.OutboundItem;
import com.example.duidui.entity.Stock;
import com.example.duidui.mapper.OutboundItemMapper;
import com.example.duidui.mapper.OutboundMapper;
import com.example.duidui.mapper.StockMapper;
import com.example.duidui.dto.OutboundRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class OutboundServiceImpl implements OutboundService {

    @Autowired private OutboundMapper outboundMapper;
    @Autowired private OutboundItemMapper outboundItemMapper;
    @Autowired private StockMapper stockMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> createOutbound(OutboundRequest request) {
        // 1. 校验参数
        if (request == null || !StringUtils.hasText(request.getOutboundNo())) {
            return Result.error("出库单号不能为空");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return Result.error("出库明细不能为空");
        }

        // 2. 单号查重
        long count = outboundMapper.selectCount(new QueryWrapper<Outbound>().eq("outbound_no", request.getOutboundNo()));
        if (count > 0) {
            return Result.error("出库单号已存在");
        }

        int totalQty = 0;
        
        // 3. 检查库存并扣减
        for (OutboundRequest.Item item : request.getItems()) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                return Result.error("商品出库数量必须大于0");
            }
            
            // 查询库存（默认仓库ID为1）
            QueryWrapper<Stock> qw = new QueryWrapper<>();
            qw.eq("product_id", item.getProductId()).eq("warehouse_id", 1L);
            Stock stock = stockMapper.selectOne(qw);
            
            if (stock == null || stock.getQuantity() < item.getQuantity()) {
                return Result.error("商品ID [" + item.getProductId() + "] 库存不足！当前剩余: " + (stock == null ? 0 : stock.getQuantity()));
            }
            
            // 扣减库存
            stock.setQuantity(stock.getQuantity() - item.getQuantity());
            stockMapper.updateById(stock);
            
            totalQty += item.getQuantity();
        }

        // 4. 写入出库主表
        Outbound outbound = new Outbound();
        outbound.setOutboundNo(request.getOutboundNo());
        outbound.setCustomer(request.getCustomer());
        outbound.setTotalQuantity(totalQty);
        outbound.setStatus("COMPLETED");
        // outbound.setCreatedAt(LocalDateTime.now()); // Entity里已经配了 @TableField(fill = FieldFill.INSERT)
        outboundMapper.insert(outbound);

        // 5. 写入明细表
        for (OutboundRequest.Item item : request.getItems()) {
            OutboundItem detail = new OutboundItem();
            detail.setOutboundId(outbound.getId());
            detail.setItemId(item.getProductId());
            detail.setQuantity(item.getQuantity());
            outboundItemMapper.insert(detail);
        }

        return Result.success("出库成功，单号：" + outbound.getOutboundNo());
    }
}
