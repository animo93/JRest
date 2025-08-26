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

    opens com.animo.jRest.test to com.google.gson;
    exports com.animo.jRest.test to org.junit.platform.commons;
}