module w2e.core {
    requires static lombok;
    requires org.apache.poi.ooxml;
    requires org.slf4j;
    requires org.apache.commons.collections4;
    requires org.apache.commons.lang3;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.databind;
    exports com.w2e.core;
    exports com.w2e.core.config;
    exports com.w2e.core.model;
}