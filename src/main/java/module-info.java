module jRest {
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;
    requires lombok;
    requires org.apache.commons.codec;
    requires org.apache.logging.log4j;
    requires java.net.http;

    exports com.jrest.java.api;
    exports com.jrest.java.api.annotation;
    opens com.jrest.java.core to com.google.gson;
}