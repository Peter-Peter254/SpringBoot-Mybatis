package com.example.demo.utils.security;

public final class TenantContext {
    private static final ThreadLocal<Long> ORG_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    private TenantContext() {}

    public static void setOrgId(Long orgId) { ORG_ID.set(orgId); }
    public static Long getOrgId() { return ORG_ID.get(); }
    public static void setUserId(Long userId) { USER_ID.set(userId); }
    public static Long getUserId() { return USER_ID.get(); }

    public static void clear() {
        ORG_ID.remove();
        USER_ID.remove();
    }
}
