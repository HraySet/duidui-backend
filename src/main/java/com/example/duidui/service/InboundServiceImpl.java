package com.example.duidui.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.duidui.common.Result;
import com.example.duidui.entity.*;
import com.example.duidui.mapper.*;
import com.example.duidui.dto.InboundRequest;
import com.example.duidui.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class InboundServiceImpl implements InboundService {

    @Autowired private InboundMapper inboundMapper;
    @Autowired private InboundDetailMapper inboundDetailMapper;
    @Autowired private StockMapper stockMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private SettingsService settingsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> createInbound(InboundRequest request, Long operatorId) {
        if (request == null) {
            return Result.error("请求不能为空");
        }
        // 没传单号 → 自动生成
        String inboundNo = request.getInboundNo();
        if (!StringUtils.hasText(inboundNo)) {
            inboundNo = settingsService.generateInboundNo();
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return Result.error("入库明细不能为空");
        }

        // 单号查重
        long count = inboundMapper.selectCount(
                new QueryWrapper<Inbound>().eq("inbound_no", inboundNo));
        if (count > 0) {
            return Result.error("入库单号已存在");
        }

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

        // 写入主表 — 待审批
        Inbound inbound = new Inbound();
        inbound.setInboundNo(inboundNo);
        inbound.setSupplier(request.getSupplier());
        inbound.setWarehouseId(request.getWarehouseId() == null ? 1L : request.getWarehouseId());
        inbound.setOperatorId(operatorId);
        inbound.setRemark(request.getRemark());
        inbound.setStatus("pending");
        inbound.setTotalQuantity(totalQty);
        inboundMapper.insert(inbound);

        // 写入明细
        for (InboundRequest.Item item : request.getItems()) {
            InboundDetail detail = new InboundDetail();
            detail.setInboundId(inbound.getId());
            detail.setProductId(item.getProductId());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());
            inboundDetailMapper.insert(detail);
        }

        return Result.success("入库单已提交，单号：" + inbound.getInboundNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> approve(Long id) {
        Inbound inbound = inboundMapper.selectById(id);
        if (inbound == null) return Result.error("入库单不存在");
        if (!"pending".equals(inbound.getStatus())) return Result.error("只能审批待处理的入库单");

        inbound.setStatus("completed");
        inboundMapper.updateById(inbound);

        // 更新库存
        QueryWrapper<InboundDetail> qw = new QueryWrapper<>();
        qw.eq("inbound_id", id);
        for (InboundDetail detail : inboundDetailMapper.selectList(qw)) {
            QueryWrapper<Stock> sqw = new QueryWrapper<>();
            sqw.eq("product_id", detail.getProductId())
               .eq("warehouse_id", inbound.getWarehouseId());
            Stock stock = stockMapper.selectOne(sqw);
            if (stock == null) {
                stock = new Stock();
                stock.setProductId(detail.getProductId());
                stock.setWarehouseId(inbound.getWarehouseId());
                stock.setQuantity(detail.getQuantity());
                stockMapper.insert(stock);
            } else {
                stock.setQuantity(stock.getQuantity() + detail.getQuantity());
                stockMapper.updateById(stock);
            }
        }

        return Result.success("入库审批通过");
    }

    @Override
    public Result<?> reject(Long id, String reason) {
        Inbound inbound = inboundMapper.selectById(id);
        if (inbound == null) return Result.error("入库单不存在");
        if (!"pending".equals(inbound.getStatus())) return Result.error("只能驳回待处理的入库单");

        inbound.setStatus("rejected");
        inbound.setRemark((inbound.getRemark() == null ? "" : inbound.getRemark() + " ") + "驳回原因: " + reason);
        inboundMapper.updateById(inbound);

        return Result.success("已驳回");
    }

    @Override
    public Result<?> page(int pageNum, int pageSize, String keyword) {
        Page<Inbound> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Inbound> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like("inbound_no", keyword).or().like("supplier", keyword);
        }
        wrapper.orderByDesc("created_at");
        Page<Inbound> result = inboundMapper.selectPage(page, wrapper);

        // 为每条记录补 items
        for (Inbound inbound : result.getRecords()) {
            QueryWrapper<InboundDetail> dqw = new QueryWrapper<>();
            dqw.eq("inbound_id", inbound.getId());
            java.util.List<InboundDetail> details = inboundDetailMapper.selectList(dqw);
            java.util.List<Map<String, Object>> items = new ArrayList<>();
            for (InboundDetail d : details) {
                Product p = productMapper.selectById(d.getProductId());
                Map<String, Object> item = new HashMap<>();
                item.put("productId", d.getProductId());
                item.put("productName", p != null ? p.getName() : "");
                item.put("sku", p != null ? p.getSku() : "");
                item.put("quantity", d.getQuantity());
                item.put("price", d.getPrice());
                items.add(item);
            }
            inbound.setItems(items);
        }

        return Result.success(result);
    }
}
