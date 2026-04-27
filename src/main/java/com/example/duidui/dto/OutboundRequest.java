package com.example.duidui.dto;

import lombok.Data;
import java.util.List;

@Data
public class OutboundRequest {
    private String outboundNo;
    private String customer;
    private List<Item> items;

    @Data
    public static class Item {
        private Long productId;
        private Integer quantity;
    }
}
