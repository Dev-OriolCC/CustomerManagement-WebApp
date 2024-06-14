package com.oriol.customermagnet.service.implementation;

import com.oriol.customermagnet.domain.Customer;
import com.oriol.customermagnet.domain.Stats;
import com.oriol.customermagnet.repository.CustomerRepository;
import com.oriol.customermagnet.rowmapper.StatsRowMapper;
import com.oriol.customermagnet.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

import static com.oriol.customermagnet.query.StatsQuery.SELECT_STATS_QUERY;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Customer create(Customer customer) {
        customer.setCreatedAt(new Date());
        return customerRepository.save(customer);
    }

    @Override
    public Customer update(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Page<Customer> getCustomers(int page, int size) {
        return customerRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Iterable<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomer(Long id) {
        return customerRepository.findById(id).get();
    }

    @Override
    public Page<Customer> searchCustomers(String name, int page, int size) {
        return customerRepository.findByNameContaining(name, PageRequest.of(page, size));
    }

    @Override
    public Stats getStats() {
        return jdbc.queryForObject(SELECT_STATS_QUERY, Map.of(), new StatsRowMapper());
    }
}
