module jRest.test {
    requires jRest;
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;
    requires lombok;
    requires org.apache.commons.codec;
    requires org.apache.logging.log4j;
    requires java.net.http;
    requires org.junit.jupiter.api;
    requires org.mockito;

    exports io.github.animo93.jrest.e2e to org.junit.platform.commons;
    opens io.github.animo93.jrest.e2e to com.google.gson;
}