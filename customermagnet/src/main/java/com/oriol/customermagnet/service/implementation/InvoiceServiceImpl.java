package com.oriol.customermagnet.service.implementation;

import com.oriol.customermagnet.domain.Customer;
import com.oriol.customermagnet.domain.Invoice;
import com.oriol.customermagnet.repository.CustomerRepository;
import com.oriol.customermagnet.repository.InvoiceRepository;
import com.oriol.customermagnet.service.InvoiceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    @Override
    public Invoice create(Invoice invoice) {
        invoice.setInvoiceNumber(UUID.randomUUID().toString());
        return invoiceRepository.save(invoice);
    }

    @Override
    public Page<Invoice> getInvoices(int page, int size) {
        return invoiceRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Invoice addInvoiceToCustomer(Long customerId, Invoice invoice) {
        Customer customer = customerRepository.findById(customerId).get();
        //Invoice invoice = invoiceRepository.findById(customerId).get();
        invoice.setInvoiceNumber(UUID.randomUUID().toString());
        invoice.setCustomer(customer);
        return invoiceRepository.save(invoice);
    }

//    @Override
//    public void addInvoiceToCustomer(Long id, Long invoiceId) {
//        Customer customer = customerRepository.findById(id).get();
//        Invoice invoice = invoiceRepository.findById(invoiceId).get();
//        invoice.setInvoiceNumber(UUID.randomUUID().toString());
//        invoice.setCustomer(customer);
//        invoiceRepository.save(invoice);
//    }

    @Override
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id).get();
    }
}
