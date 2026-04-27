package com.example.duidui.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.duidui.common.Result;
import com.example.duidui.entity.*;
import com.example.duidui.mapper.*;
import com.example.duidui.dto.InboundRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class InboundServiceImpl implements InboundService {

    @Autowired private InboundMapper inboundMapper;
    @Autowired private InboundDetailMapper inboundDetailMapper;
    @Autowired private StockMapper stockMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> createInbound(InboundRequest request, Long operatorId) {

        // ========== 1. 参数校验 ==========
        if (request == null) {
            return Result.error("请求不能为空");
        }

        if (!StringUtils.hasText(request.getInboundNo())) {
            return Result.error("入库单号不能为空");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            return Result.error("入库明细不能为空");
        }

        if (operatorId == null) {
            return Result.error("操作人不能为空");
        }

        // ========== 2. 单号唯一性校验 ==========
        long count = inboundMapper.selectCount(
                new QueryWrapper<Inbound>().eq("inbound_no", request.getInboundNo())
        );
        if (count > 0) {
            return Result.error("入库单号已存在");
        }

        // ========== 3. 创建主表 ==========
        Inbound inbound = new Inbound();
        inbound.setInboundNo(request.getInboundNo());
        inbound.setSupplier(request.getSupplier());
        inbound.setWarehouseId(
                request.getWarehouseId() == null ? 1L : request.getWarehouseId()
        );
        inbound.setOperatorId(operatorId);
        inbound.setRemark(request.getRemark());
        inbound.setStatus("COMPLETED");

        int totalQty = 0;
        for (InboundRequest.Item item : request.getItems()) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                return Result.error("商品数量必须大于0");
            }
            if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                return Result.error("商品价格不合法");
            }
            totalQty += item.getQuantity();
        }

        inbound.setTotalQuantity(totalQty);
        inbound.setCreatedAt(LocalDateTime.now());

        inboundMapper.insert(inbound);
        Long inboundId = inbound.getId();

        // ========== 4. 明细 + 库存 ==========
        for (InboundRequest.Item item : request.getItems()) {

            // 4.1 插入明细
            InboundDetail detail = new InboundDetail();
            detail.setInboundId(inboundId);
            detail.setProductId(item.getProductId());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());
            inboundDetailMapper.insert(detail);

            // 4.2 查询库存
            QueryWrapper<Stock> qw = new QueryWrapper<>();
            qw.eq("product_id", item.getProductId())
                    .eq("warehouse_id", inbound.getWarehouseId());

            Stock stock = stockMapper.selectOne(qw);

            // 4.3 更新库存
            if (stock == null) {
                stock = new Stock();
                stock.setProductId(item.getProductId());
                stock.setWarehouseId(inbound.getWarehouseId());
                stock.setQuantity(item.getQuantity());
                stockMapper.insert(stock);
            } else {
                stock.setQuantity(stock.getQuantity() + item.getQuantity());
                stockMapper.updateById(stock);
            }
        }

        // ========== 5. 返回结果 ==========
        return Result.success("入库成功，单号：" + inbound.getInboundNo());
    }
}
