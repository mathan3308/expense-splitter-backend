package com.mathan.expensesplitter.dto.auth.group;

public class MemberResponse {

    private Long id;
    private String name;
    private String email;

    public MemberResponse() {
    }

    public MemberResponse(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public static MemberResponseBuilder builder() {
        return new MemberResponseBuilder();
    }

    public static class MemberResponseBuilder {
        private Long id;
        private String name;
        private String email;

        public MemberResponseBuilder id(Long id) { this.id = id; return this; }
        public MemberResponseBuilder name(String name) { this.name = name; return this; }
        public MemberResponseBuilder email(String email) { this.email = email; return this; }

        public MemberResponse build() {
            return new MemberResponse(id, name, email);
        }
    }
}
