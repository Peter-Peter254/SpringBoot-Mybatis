package com.example.demo.customer;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CustomerMapper {
    int insert(Customer c);
    Customer findById(@Param("orgId") Long orgId, @Param("id") Long id);
    List<Customer> findAll(@Param("orgId") Long orgId, @Param("limit") int limit, @Param("offset") int offset);
    long countAll(@Param("orgId") Long orgId);
    int update(Customer c);            // will use c.orgId in XML
    int delete(@Param("orgId") Long orgId, @Param("id") Long id);
}