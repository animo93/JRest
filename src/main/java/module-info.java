module jRest {
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;
    requires lombok;
    requires org.apache.commons.codec;
    requires org.apache.logging.log4j;
    requires java.net.http;

    opens com.animo.jRest.util to com.google.gson;
    exports com.animo.jRest.util;
    exports com.animo.jRest.annotation;
    exports com.animo.jRest.model;
}