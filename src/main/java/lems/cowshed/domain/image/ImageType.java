package lems.cowshed.domain.image;

public enum ImageType {
    PUBLIC("공개", "public/"),
    PRIVATE("비공개", "private/");

    private final String description;
    private final String prefix;

    ImageType(String description, String prefix) {
        this.description = description;
        this.prefix = prefix;
    }

    public String getDescription() {
        return description;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isPublic() {
        return this == PUBLIC;
    }

    public boolean isPrivate() {
        return this == PRIVATE;
    }
}
