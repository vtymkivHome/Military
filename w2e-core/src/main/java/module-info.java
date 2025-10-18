module w2e.core {
    requires static lombok;
    requires org.apache.poi.ooxml;
    requires org.slf4j;
    requires org.apache.commons.collections4;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.databind;
    requires org.apache.commons.text;
    requires org.apache.commons.lang3;
    requires org.apache.poi.scratchpad;
    exports com.w2e.core;
    exports com.w2e.core.config;
    exports com.w2e.core.model;
}