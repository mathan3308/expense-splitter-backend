package com.mathan.expensesplitter.dto.auth.group;

public class GroupResponse {

    private Long id;
    private String name;
    private String description;

    public GroupResponse() {
    }

    public GroupResponse(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public static GroupResponseBuilder builder() {
        return new GroupResponseBuilder();
    }

    public static class GroupResponseBuilder {
        private Long id;
        private String name;
        private String description;

        public GroupResponseBuilder id(Long id) { this.id = id; return this; }
        public GroupResponseBuilder name(String name) { this.name = name; return this; }
        public GroupResponseBuilder description(String description) { this.description = description; return this; }

        public GroupResponse build() {
            return new GroupResponse(id, name, description);
        }
    }
}