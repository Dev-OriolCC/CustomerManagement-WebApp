package com.oriol.customermagnet.query;

public class StatsQuery {
    public static final String SELECT_STATS_QUERY = "SELECT c.total_customers, i.total_invoices, b.total_billed FROM (SELECT COUNT(*) total_customers FROM customer) c, (SELECT COUNT(*) total_invoices FROM invoice) i, (SELECT ROUND(SUM(total)) total_billed FROM invoice) b";
}
