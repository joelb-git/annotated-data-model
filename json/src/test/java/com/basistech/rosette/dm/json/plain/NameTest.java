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

package com.basistech.rosette.dm.json.plain;

import com.basistech.rosette.dm.Name;
import com.basistech.util.ISO15924;
import com.basistech.util.LanguageCode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

/**
 * Test for serialization etc. on {@link com.basistech.rosette.dm.Name}.
 */
public class NameTest extends AdmAssert {
    @Test
    public void name() throws Exception {
        List<Name> names = Lists.newArrayList();
        Name.Builder builder = new Name.Builder("Fred");
        names.add(builder.build());
        builder = new Name.Builder("George");
        builder.languageOfOrigin(LanguageCode.ENGLISH).script(ISO15924.Latn).languageOfUse(LanguageCode.FRENCH);
        names.add(builder.build());
        ObjectMapper mapper = objectMapper();
        String json = mapper.writeValueAsString(names);
        // one way to inspect the works is to read it back in _without_ our customized mapper.
        ObjectMapper plainMapper = new ObjectMapper();
        JsonNode tree = plainMapper.readTree(json);
        assertTrue(tree.isArray());
        assertEquals(2, tree.size());
        JsonNode node = tree.get(0);
        assertTrue(node.has("text"));
        assertEquals("Fred", node.get("text").asText());
        assertFalse(node.has("script"));
        assertFalse(node.has("languageOfOrigin"));
        assertFalse(node.has("languageOfUse"));

        List<Name> readBack = mapper.readValue(json, new TypeReference<List<Name>>() { });
        assertEquals(names, readBack);
    }
}
