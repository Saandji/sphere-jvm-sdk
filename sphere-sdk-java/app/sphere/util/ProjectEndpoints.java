package sphere.util;

/** Centralizes construction of backend API urls. */
public class ProjectEndpoints {
    private String projectUrl;

    /** Package private constructor. */
    ProjectEndpoints(String projectUrl) {
        this.projectUrl = projectUrl;
    }
    
    public String products() {
        return projectUrl + "/products";
    }
    public String product(String id) {
        return projectUrl + "/products/" + id;
    }

    public String productDefinitions() {
        return projectUrl + "/product-definitions";
    }
    public String productDefinition(String id) {
        return projectUrl + "/product-definitions/" + id;
    }

    public String categories() {
        return projectUrl + "/categories";
    }
    public String category(String id) {
        return projectUrl + "/categories/" + id;
    }
}
