package com.example.demo.customer;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CustomerMapper {
    int insert(Customer c);
    Customer findById(@Param("id") Long id);
    List<Customer> findAll(@Param("limit") int limit, @Param("offset") int offset);
    long countAll();
    int update(Customer c);
    int delete(@Param("id") Long id);
}
