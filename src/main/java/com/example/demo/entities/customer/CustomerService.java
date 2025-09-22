package com.example.demo.entities.customer;

import org.springframework.stereotype.Service;
import com.example.demo.utils.security.TenantContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerMapper mapper;
    public CustomerService(CustomerMapper mapper) { this.mapper = mapper; }

    @Transactional
    public Customer create(Customer c) {
        c.setOrgId(TenantContext.getOrgId());   // <-- inject orgId on create
        mapper.insert(c);
        return c;
    }

    public Customer get(Long id) {
        return mapper.findById(TenantContext.getOrgId(), id);
    }

    public List<Customer> list(int limit, int offset) {
        return mapper.findAll(TenantContext.getOrgId(), limit, offset);
    }

    public long count() {
        return mapper.countAll(TenantContext.getOrgId());
    }

    @Transactional
    public boolean update(Customer c) {
        c.setOrgId(TenantContext.getOrgId());
        return mapper.update(c) > 0;
    }

    @Transactional
    public boolean delete(Long id) {
        return mapper.delete(TenantContext.getOrgId(), id) > 0;
    }
}
