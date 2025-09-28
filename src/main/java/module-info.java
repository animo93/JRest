/**
 * jRest - A simple and lightweight Java REST client library.
 * <p>
 * Provides an easy-to-use API for making HTTP requests and handling responses.
 * </p>
 */
module jRest {
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;
    requires lombok;
    requires org.apache.commons.codec;
    requires org.apache.logging.log4j;
    requires java.net.http;

    exports io.github.animo93.jrest.api;
    exports io.github.animo93.jrest.api.annotation;
    opens io.github.animo93.jrest.core to com.google.gson;
}