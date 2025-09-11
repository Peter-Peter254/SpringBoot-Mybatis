package com.example.demo.customer;

import com.example.demo.customer.CustomerDtos.CreateRequest;
import com.example.demo.customer.CustomerDtos.UpdateRequest;
import com.example.demo.customer.CustomerDtos.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;
    public CustomerController(CustomerService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Customer> create(@Valid @RequestBody CreateRequest req) {
        Customer c = new Customer();
        c.setName(req.name); c.setEmail(req.email); c.setPhone(req.phone);
        Customer created = service.create(c);
        return ResponseEntity.created(URI.create("/api/customers/" + created.getId())).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> get(@PathVariable Long id) {
        Customer c = service.get(id);
        return (c == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(c);
    }

    @GetMapping
    public ResponseEntity<PageResponse<Customer>> list(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        limit = Math.min(Math.max(limit, 1), 100);
        offset = Math.max(offset, 0);
        List<Customer> items = service.list(limit, offset);
        long total = service.count();
        return ResponseEntity.ok(new PageResponse<>(items, total, limit, offset));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateRequest req) {
        Customer existing = service.get(id);
        if (existing == null) return ResponseEntity.notFound().build();
        existing.setName(req.name); existing.setEmail(req.email); existing.setPhone(req.phone);
        return service.update(existing) ? ResponseEntity.ok(existing) : ResponseEntity.internalServerError().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
