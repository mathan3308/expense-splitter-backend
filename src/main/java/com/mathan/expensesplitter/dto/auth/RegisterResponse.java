package com.mathan.expensesplitter.dto.auth;

public class RegisterResponse {

    private Long id;
    private String name;
    private String email;
    private String message;

    public RegisterResponse() {
    }

    public RegisterResponse(Long id, String name, String email, String message) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.message = message;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static RegisterResponseBuilder builder() {
        return new RegisterResponseBuilder();
    }

    public static class RegisterResponseBuilder {
        private Long id;
        private String name;
        private String email;
        private String message;

        public RegisterResponseBuilder id(Long id) { this.id = id; return this; }
        public RegisterResponseBuilder name(String name) { this.name = name; return this; }
        public RegisterResponseBuilder email(String email) { this.email = email; return this; }
        public RegisterResponseBuilder message(String message) { this.message = message; return this; }

        public RegisterResponse build() {
            return new RegisterResponse(id, name, email, message);
        }
    }
}