package uk.ac.mdx;

/**
 * The entry point to the application. A separate class that doesn't implement Application is needed
 * to avoid problems in packaging an uber-jar. See the following post:
 * <a href = "https://stackoverflow.com/questions/52653836/maven-shade-javafx-runtime-components-are-missing/52654791#52654791">Stackoverflow post</a>
 * @author Matthew M. Jones
 */
public class StartPoint {

    public static void main(String[] args) {
        App.main(args);
    }

}
