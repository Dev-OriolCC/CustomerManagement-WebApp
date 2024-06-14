package com.oriol.customermagnet.service;

import com.oriol.customermagnet.domain.Invoice;
import org.springframework.data.domain.Page;

public interface InvoiceService {
    Invoice create(Invoice invoice);
    Page<Invoice> getInvoices(int page, int size);
    Invoice addInvoiceToCustomer(Long invoiceId, Invoice invoice);
//    void addInvoiceToCustomer(Long id, Long invoiceId);
    Invoice getInvoiceById(Long id);
}
