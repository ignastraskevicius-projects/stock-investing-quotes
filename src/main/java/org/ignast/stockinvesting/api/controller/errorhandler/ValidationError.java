package org.ignast.stockinvesting.api.controller.errorhandler;

import static java.util.Objects.requireNonNull;

public class ValidationError {
    private String jsonPath;
    private String message;
    private ViolationType type;

    public ValidationError(String path, String message, ViolationType type) {
        if (path == null || path.isEmpty()) {
            this.jsonPath = "$";
        } else {
            this.jsonPath = String.format("$.%s", path);
        }
        this.message = message;
        requireNonNull(type);
        this.type = type;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public String getMessage() {
        return message;
    }

    public ViolationType getType() {
        return type;
    }
}
