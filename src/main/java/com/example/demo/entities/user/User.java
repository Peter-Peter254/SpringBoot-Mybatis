package com.example.demo.entities.user;

import java.time.OffsetDateTime;

public class User {
    private Long id;
    private Long orgId;
    private String email;
    private String passwordHash;
    private String fullName;
    private String role;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // getters/setters...
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getOrgId() { return orgId; } public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; } public void setPasswordHash(String h) { this.passwordHash = h; }
    public String getFullName() { return fullName; } public void setFullName(String n) { this.fullName = n; }
    public String getRole() { return role; } public void setRole(String role) { this.role = role; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime t) { this.createdAt = t; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(OffsetDateTime t) { this.updatedAt = t; }
}
