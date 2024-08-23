package com.oriol.customermagnet.controller;

import com.oriol.customermagnet.domain.Customer;
import com.oriol.customermagnet.domain.HttpResponse;
import com.oriol.customermagnet.dto.UserDTO;
import com.oriol.customermagnet.reporte.CustomerReport;
import com.oriol.customermagnet.service.CustomerService;
import com.oriol.customermagnet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<HttpResponse> list(@AuthenticationPrincipal UserDTO userDTO, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUserByEmail(userDTO.getEmail()),
                                "page", customerService.getCustomers(page.orElse(0), size.orElse(10)),
                                "stats", customerService.getStats()
                        ))
                        .message("Customer Retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<HttpResponse> getCustomer(@AuthenticationPrincipal UserDTO userDTO, @PathVariable("id") Long id) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUserByEmail(userDTO.getEmail()),
                                "customers", customerService.getCustomer(id)
                        ))
                        .message("Customer Retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/search")
    public ResponseEntity<HttpResponse> searchCustomers(@AuthenticationPrincipal UserDTO userDTO, @RequestParam Optional<String> name, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUserByEmail(userDTO.getEmail()),
                                "page", customerService.searchCustomers(name.orElse(""), page.orElse(0), size.orElse(10))
                        ))
                        .message("Customer Retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PostMapping("/create")
    public ResponseEntity<HttpResponse> create(@AuthenticationPrincipal UserDTO userDTO, @RequestBody Customer customer) {
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUserByEmail(userDTO.getEmail()),
                                "customer", customerService.create(customer))
                        )
                        .message("Customer registered successfully.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PutMapping("/update")
    public ResponseEntity<HttpResponse> update(@AuthenticationPrincipal UserDTO userDTO, @RequestBody Customer customer) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUserByEmail(userDTO.getEmail()),
                                "customers", customerService.update(customer))
                                // TODO: customers
                        )
                        .message("Customer updated successfully")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/download/report")
    public ResponseEntity<Resource> downloadReport() {
        System.out.println("Here");
        List<Customer> customers = new ArrayList<>();
        customerService.getCustomers().iterator().forEachRemaining(customers::add);
        CustomerReport report = new CustomerReport(customers);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("File-Name", "customer-report.xlsx");
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;File-Name=customer-report.xlsx");
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .headers(httpHeaders).body(report.export());
    }
}
