package com.mathan.expensesplitter.dto.auth;

public class LoginResponse {

    private String token;
    private String type;

    public LoginResponse() {
    }

    public LoginResponse(String token, String type) {
        this.token = token;
        this.type = type;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public static LoginResponseBuilder builder() {
        return new LoginResponseBuilder();
    }

    public static class LoginResponseBuilder {
        private String token;
        private String type;

        public LoginResponseBuilder token(String token) { this.token = token; return this; }
        public LoginResponseBuilder type(String type) { this.type = type; return this; }

        public LoginResponse build() {
            return new LoginResponse(token, type);
        }
    }
}