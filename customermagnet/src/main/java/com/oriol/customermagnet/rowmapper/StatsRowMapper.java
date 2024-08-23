package com.oriol.customermagnet.rowmapper;

import com.oriol.customermagnet.domain.Stats;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsRowMapper implements RowMapper<Stats> {

    @Override
    public Stats mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Stats.builder()
                .totalCustomers(rs.getInt("total_customers"))
                .totalInvoices(rs.getInt("total_invoices"))
                .totalBilled(rs.getDouble("total_billed"))
                .build();
    }
}
