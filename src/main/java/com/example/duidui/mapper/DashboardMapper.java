package com.example.duidui.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DashboardMapper {

    @Select("SELECT COUNT(*) FROM product WHERE status = 1")
    long productCount();

    @Select("SELECT COALESCE(SUM(quantity), 0) FROM stock")
    long totalStock();

    @Select("SELECT COUNT(*) FROM inbound WHERE DATE(created_at) = CURDATE()")
    long todayInCount();

    @Select("SELECT COUNT(*) FROM outbound WHERE DATE(created_at) = CURDATE()")
    long todayOutCount();

    @Select("SELECT COUNT(*) FROM product p " +
            "LEFT JOIN stock s ON p.id = s.product_id " +
            "WHERE p.status = 1 AND p.low_stock_threshold > 0 " +
            "AND COALESCE(s.quantity, 0) <= p.low_stock_threshold")
    long lowStockCount();

    @Select("SELECT COALESCE(SUM(total_quantity), 0) FROM inbound " +
            "WHERE YEAR(created_at) = YEAR(CURDATE()) AND MONTH(created_at) = MONTH(CURDATE())")
    long monthInQty();

    @Select("SELECT DATE_FORMAT(d.date, '%m/%d') as date, " +
            "COALESCE(t.inbound, 0) as inbound, " +
            "COALESCE(o.outbound, 0) as outbound " +
            "FROM ( " +
            "  SELECT CURDATE() - INTERVAL 6 DAY as date " +
            "  UNION ALL SELECT CURDATE() - INTERVAL 5 DAY " +
            "  UNION ALL SELECT CURDATE() - INTERVAL 4 DAY " +
            "  UNION ALL SELECT CURDATE() - INTERVAL 3 DAY " +
            "  UNION ALL SELECT CURDATE() - INTERVAL 2 DAY " +
            "  UNION ALL SELECT CURDATE() - INTERVAL 1 DAY " +
            "  UNION ALL SELECT CURDATE() " +
            ") d " +
            "LEFT JOIN (SELECT DATE(created_at) as dt, SUM(total_quantity) as inbound " +
            "  FROM inbound WHERE created_at >= CURDATE() - INTERVAL 6 DAY GROUP BY dt) t ON d.date = t.dt " +
            "LEFT JOIN (SELECT DATE(created_at) as dt, SUM(total_quantity) as outbound " +
            "  FROM outbound WHERE created_at >= CURDATE() - INTERVAL 6 DAY GROUP BY dt) o ON d.date = o.dt " +
            "ORDER BY d.date")
    List<Map<String, Object>> trend();
}
