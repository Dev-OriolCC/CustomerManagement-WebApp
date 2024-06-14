package com.oriol.customermagnet.service;

import com.oriol.customermagnet.domain.Customer;
import com.oriol.customermagnet.domain.Stats;
import org.springframework.data.domain.Page;

public interface CustomerService {
    Customer create(Customer customer);
    Customer update(Customer customer);
    Page<Customer> getCustomers(int page, int size);
    Iterable<Customer> getCustomers();
    Customer getCustomer(Long id);
    Page<Customer> searchCustomers(String name, int page, int size);

    Stats getStats();
}
