package com.example.duidui.dto;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class InboundRequest {
    private String inboundNo;
    private String supplier;
    private Long warehouseId;
    private String remark;
    private List<Item> items;

    @Data
    public static class Item {
        private Long productId;
        private Integer quantity;
        private BigDecimal price;
    }
}
