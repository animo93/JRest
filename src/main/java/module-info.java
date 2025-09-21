module jRest {
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;
    requires lombok;
    requires org.apache.commons.codec;
    requires org.apache.logging.log4j;
    requires java.net.http;

    opens com.jrest.java.util to com.google.gson;
    exports com.jrest.java.util;
    exports com.jrest.java.annotation;
    exports com.jrest.java.model;
}