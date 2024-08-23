package com.oriol.customermagnet.controller;

import com.oriol.customermagnet.domain.HttpResponse;
import com.oriol.customermagnet.domain.Invoice;
import com.oriol.customermagnet.dto.UserDTO;
import com.oriol.customermagnet.service.CustomerService;
import com.oriol.customermagnet.service.InvoiceService;
import com.oriol.customermagnet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final CustomerService customerService;
    private final InvoiceService invoiceService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<HttpResponse> create(@AuthenticationPrincipal UserDTO userDTO, @RequestBody Invoice invoice) {
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUserByEmail(userDTO.getEmail()),
                                "invoice", invoiceService.create(invoice))
                        )
                        .message("Customer Retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/list")
    public ResponseEntity<HttpResponse> getInvoices(@AuthenticationPrincipal UserDTO userDTO, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) {
         return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUserByEmail(userDTO.getEmail()),
                                "page", invoiceService.getInvoices(page.orElse(0), size.orElse(10))
                        ))
                        .message("Customer Retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
    @GetMapping("/new")
    public ResponseEntity<HttpResponse> newInvoice(@AuthenticationPrincipal UserDTO userDTO) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUserByEmail(userDTO.getEmail()),
                                "customers", customerService.getCustomers())
                        )
                        .message("Customers Retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<HttpResponse> getInvoice(@AuthenticationPrincipal UserDTO userDTO, @PathVariable("id") Long id) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUserByEmail(userDTO.getEmail()),
                                "customer", invoice.getCustomer(),
                                "invoice", invoice
                        ))
                        .message("Invoice Retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
    @PostMapping("/addInvoice/to/{customerId}")
    public ResponseEntity<HttpResponse> addInvoiceToCustomer(@AuthenticationPrincipal UserDTO userDTO, @RequestBody Invoice invoice, @PathVariable("customerId") Long customerId) {
        Invoice invoiceCreated = invoiceService.addInvoiceToCustomer(customerId, invoice);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUserByEmail(userDTO.getEmail()),
                                "customers", customerService.getCustomers()
                                //"invoice", invoiceCreated
                        ))
                        .message("Invoice added to customer: "+invoiceCreated.getCustomer().getName())
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

}
