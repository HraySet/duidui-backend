package com.example.duidui.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.duidui.common.Result;
import com.example.duidui.entity.Outbound;
import com.example.duidui.entity.OutboundItem;
import com.example.duidui.entity.Product;
import com.example.duidui.entity.Stock;
import com.example.duidui.mapper.OutboundItemMapper;
import com.example.duidui.mapper.OutboundMapper;
import com.example.duidui.mapper.StockMapper;
import com.example.duidui.dto.OutboundRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class OutboundServiceImpl implements OutboundService {

    @Autowired private OutboundMapper outboundMapper;
    @Autowired private OutboundItemMapper outboundItemMapper;
    @Autowired private StockMapper stockMapper;
    @Autowired private com.example.duidui.mapper.ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> createOutbound(OutboundRequest request) {
        if (request == null || !StringUtils.hasText(request.getOutboundNo())) {
            return Result.error("出库单号不能为空");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return Result.error("出库明细不能为空");
        }

        // 单号查重
        long count = outboundMapper.selectCount(
                new QueryWrapper<Outbound>().eq("outbound_no", request.getOutboundNo()));
        if (count > 0) {
            return Result.error("出库单号已存在");
        }

        int totalQty = 0;
        for (OutboundRequest.Item item : request.getItems()) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                return Result.error("商品出库数量必须大于0");
            }
            totalQty += item.getQuantity();
        }

        // 写入主表 — 待审批，不扣库存
        Outbound outbound = new Outbound();
        outbound.setOutboundNo(request.getOutboundNo());
        outbound.setCustomer(request.getCustomer());
        outbound.setTotalQuantity(totalQty);
        outbound.setStatus("pending");
        outboundMapper.insert(outbound);

        // 写入明细
        for (OutboundRequest.Item item : request.getItems()) {
            OutboundItem detail = new OutboundItem();
            detail.setOutboundId(outbound.getId());
            detail.setItemId(item.getProductId());
            detail.setQuantity(item.getQuantity());
            outboundItemMapper.insert(detail);
        }

        return Result.success("出库单已提交，待审批");
    }

    @Override
    public Result<?> page(int pageNum, int pageSize, String keyword) {
        Page<Outbound> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Outbound> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like("outbound_no", keyword).or().like("customer", keyword);
        }
        wrapper.orderByDesc("created_at");
        Page<Outbound> result = outboundMapper.selectPage(page, wrapper);

        // 为每条记录补 items
        for (Outbound outbound : result.getRecords()) {
            QueryWrapper<OutboundItem> iqw = new QueryWrapper<>();
            iqw.eq("outbound_id", outbound.getId());
            java.util.List<OutboundItem> items = outboundItemMapper.selectList(iqw);
            java.util.List<Map<String, Object>> itemList = new ArrayList<>();
            for (OutboundItem oi : items) {
                Product p = productMapper.selectById(oi.getItemId());
                Map<String, Object> item = new HashMap<>();
                item.put("productId", oi.getItemId());
                item.put("productName", p != null ? p.getName() : "");
                item.put("sku", p != null ? p.getSku() : "");
                item.put("quantity", oi.getQuantity());
                itemList.add(item);
            }
            outbound.setItems(itemList);
        }

        return Result.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> approve(Long id) {
        Outbound outbound = outboundMapper.selectById(id);
        if (outbound == null) return Result.error("出库单不存在");
        if (!"pending".equals(outbound.getStatus())) return Result.error("只能审批待处理的出库单");

        // 检查库存
        QueryWrapper<OutboundItem> qw = new QueryWrapper<>();
        qw.eq("outbound_id", id);
        for (OutboundItem item : outboundItemMapper.selectList(qw)) {
            QueryWrapper<Stock> sqw = new QueryWrapper<>();
            sqw.eq("product_id", item.getItemId()).eq("warehouse_id", 1L);
            Stock stock = stockMapper.selectOne(sqw);
            if (stock == null || stock.getQuantity() < item.getQuantity()) {
                return Result.error("商品ID [" + item.getItemId() + "] 库存不足！剩余: "
                        + (stock == null ? 0 : stock.getQuantity()));
            }
        }

        // 扣减库存
        for (OutboundItem item : outboundItemMapper.selectList(qw)) {
            QueryWrapper<Stock> sqw = new QueryWrapper<>();
            sqw.eq("product_id", item.getItemId()).eq("warehouse_id", 1L);
            Stock stock = stockMapper.selectOne(sqw);
            stock.setQuantity(stock.getQuantity() - item.getQuantity());
            stockMapper.updateById(stock);
        }

        outbound.setStatus("completed");
        outboundMapper.updateById(outbound);

        return Result.success("出库审批通过");
    }

    @Override
    public Result<?> reject(Long id, String reason) {
        Outbound outbound = outboundMapper.selectById(id);
        if (outbound == null) return Result.error("出库单不存在");
        if (!"pending".equals(outbound.getStatus())) return Result.error("只能驳回待处理的出库单");

        outbound.setStatus("rejected");
        outboundMapper.updateById(outbound);

        return Result.success("已驳回" + (reason != null ? ": " + reason : ""));
    }
}
