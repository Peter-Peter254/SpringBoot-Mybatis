package com.example.demo.entities.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CustomerDtos {

    public static class CreateRequest {
        @NotBlank @Size(max = 150) public String name;
        @NotBlank @Email @Size(max = 255) public String email;
        @Size(max = 30) public String phone;
    }

    public static class UpdateRequest {
        @NotBlank @Size(max = 150) public String name;
        @NotBlank @Email @Size(max = 255) public String email;
        @Size(max = 30) public String phone;
    }

    public static class PageResponse<T> {
        public List<T> items;
        public long total;
        public int limit;
        public int offset;
        public PageResponse(List<T> items, long total, int limit, int offset) {
            this.items = items; this.total = total; this.limit = limit; this.offset = offset;
        }
    }
}
