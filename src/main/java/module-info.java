module uk.ac.mdx {
    requires javafx.controls;
    requires javafx.fxml;

    opens uk.ac.mdx to javafx.fxml;
    exports uk.ac.mdx;
}