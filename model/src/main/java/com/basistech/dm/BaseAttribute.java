/******************************************************************************
 ** This data and information is proprietary to, and a valuable trade secret
 ** of, Basis Technology Corp.  It is given in confidence by Basis Technology
 ** and may only be used as permitted under the license agreement under which
 ** it has been distributed, and in no other way.
 **
 ** Copyright (c) 2014 Basis Technology Corporation All rights reserved.
 **
 ** The technical data and information provided herein are provided with
 ** `limited rights', and the computer software provided herein is provided
 ** with `restricted rights' as those terms are defined in DAR and ASPR
 ** 7-104.9(a).
 ******************************************************************************/

package com.basistech.dm;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;

/**
 * The simplest possible attribute.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseAttribute {
    protected Map<String, Object> extendedProperties;

    public BaseAttribute() {
        Map<String, Object> emptyMap = Maps.newHashMap();
        extendedProperties = ImmutableMap.<String, Object>builder().putAll(emptyMap).build();
    }

    public BaseAttribute(Map<String, Object> extendedProperties) {
        this.extendedProperties = ImmutableMap.<String, Object>builder().putAll(extendedProperties).build();
    }

    /**
     * All attributes allow for properties that aren't in the data model yet.
     * @return home for wayward properties.
     */
    @JsonAnyGetter
    public Map<String, Object> getExtendedProperties() {
        return extendedProperties;
    }

    @JsonAnySetter
    public void setExtendedProperty(String name, Object value) {
        /* This is only called in deserialization. So we do something
        * to work around the read-only collection. */
        Map<String, Object> newExtendedProperties = Maps.newHashMap();
        newExtendedProperties.putAll(extendedProperties);
        newExtendedProperties.put(name, value);
        extendedProperties = Collections.unmodifiableMap(newExtendedProperties);
    }

    public static class Builder {
        protected final Map<String, Object> extendedProperties;

        public Builder() {
            this.extendedProperties = Maps.newHashMap();
        }

        public Builder(BaseAttribute toCopy) {
            this.extendedProperties = Maps.newHashMap();
            this.extendedProperties.putAll(toCopy.extendedProperties);
        }

        public void extendedProperty(String key, Object value) {
            this.extendedProperties.put(key, value);
        }

        public BaseAttribute build() {
            return new BaseAttribute(extendedProperties);
        }

    }
}
