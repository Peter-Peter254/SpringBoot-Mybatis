package com.example.demo.customer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerMapper mapper;
    public CustomerService(CustomerMapper mapper) { this.mapper = mapper; }

    @Transactional
    public Customer create(Customer c) {
        mapper.insert(c);
        return c;
    }

    public Customer get(Long id) { return mapper.findById(id); }

    public List<Customer> list(int limit, int offset) {
        return mapper.findAll(limit, offset);
    }

    public long count() { return mapper.countAll(); }

    @Transactional
    public boolean update(Customer c) { return mapper.update(c) > 0; }

    @Transactional
    public boolean delete(Long id) { return mapper.delete(id) > 0; }
}
