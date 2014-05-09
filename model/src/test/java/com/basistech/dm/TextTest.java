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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Maps;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TextTest {
    // I like to use this while debugging, but checkstyle and pmd flag as
    // unused.
    //CHECKSTYLE:OFF
    @SuppressWarnings("PMD")
    private String toJson(Object object) {
        ObjectWriter writer = new ObjectMapper().writer();
        try {
            return writer.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    //CHECKSTYLE:ON

    @Test
    public void testSentenceBoundaries() {
        //                0123456789012
        String rawText = "One.  Two.  ";
        int[] tokenBoundaries = {2, 4};
        int[] charBoundaries = {6, 12};
        Text text = new Text(rawText, Maps.<String, List<String>>newHashMap());
        SentenceBoundaries.Builder builder = new SentenceBoundaries.Builder();
        builder.tokenBoundaries(tokenBoundaries);
        builder.charBoundaries(charBoundaries);
        text.getAttributes().put(SentenceBoundaries.class.getName(), builder.build());

        SentenceBoundaries sentenceBoundaries = text.getSentenceBoundaries();
        assertEquals(2, sentenceBoundaries.getTokenBoundaries().length);
        assertEquals(2, sentenceBoundaries.getTokenBoundaries()[0]);
        assertEquals(4, sentenceBoundaries.getTokenBoundaries()[1]);
        assertEquals(2, sentenceBoundaries.getCharBoundaries().length);
        assertEquals(6, sentenceBoundaries.getCharBoundaries()[0]);
        assertEquals(12, sentenceBoundaries.getCharBoundaries()[1]);
    }

    // TODO: fixing this will require an interface change.  We should not provide
    // public access to internal arrays, right?  See COMN-41.
    @Ignore
    @Test
    public void testSentenceBoundariesImmutable() {
        //                0123456
        String rawText = "One.  ";
        int[] tokenBoundaries = {2};
        int[] charBoundaries = {6};
        Text text = new Text(rawText, Maps.<String, List<String>>newHashMap());
        SentenceBoundaries.Builder builder = new SentenceBoundaries.Builder();
        builder.tokenBoundaries(tokenBoundaries);
        builder.charBoundaries(charBoundaries);
        text.getAttributes().put(SentenceBoundaries.class.getName(), builder.build());

        SentenceBoundaries sentenceBoundaries = text.getSentenceBoundaries();
        sentenceBoundaries.getTokenBoundaries()[0] = 123;
        sentenceBoundaries.getCharBoundaries()[0] = 456;
        assertEquals(2, sentenceBoundaries.getTokenBoundaries()[0]);
        assertEquals(6, sentenceBoundaries.getCharBoundaries()[0]);
    }

    @Test
    public void testBaseNounPhrases() {
        //                012345678901
        String rawText = "Dog.  Book.";
        // The ARA gives bnp values in terms of token indexes.
        int[] bnpFromARA = {0, 1, 2, 3};
        int[] tokenOffsets = {0, 3, 3, 4, 6, 10, 10, 11};
        Text text = new Text(rawText, Maps.<String, List<String>>newHashMap());
        ListAttribute.Builder<BaseNounPhrase> attrBuilder = new ListAttribute.Builder<BaseNounPhrase>(BaseNounPhrase.class);
        for (int i = 0; i < bnpFromARA.length; i += 2) {
            BaseNounPhrase.Builder builder = new BaseNounPhrase.Builder(tokenOffsets, bnpFromARA[i], bnpFromARA[i + 1]);
            attrBuilder.add(builder.build());
        }
        text.getAttributes().put(BaseNounPhrase.class.getName(), attrBuilder.build());

        BaseNounPhrase bnp;
        assertEquals(2, text.getBaseNounPhrases().getItems().size());
        bnp = text.getBaseNounPhrases().getItems().get(0);
        assertEquals(0, bnp.getStartOffset());
        assertEquals(3, bnp.getEndOffset());
        bnp = text.getBaseNounPhrases().getItems().get(1);
        assertEquals(6, bnp.getStartOffset());
        assertEquals(10, bnp.getEndOffset());
    }

    @Test
    public void testEntityMentionsWithoutChains() {
        //                012345678901234
        String rawText = "Bill.  George.";
        Text text = new Text(rawText, Maps.<String, List<String>>newHashMap());
        int[] entities = {0, 1, 65536, 2, 3, 65536};
        int[] tokenOffsets = {0, 3, 7, 13};
        ListAttribute.Builder<EntityMention> entityListBuilder = new ListAttribute.Builder<EntityMention>(EntityMention.class);
        for (int i = 0; i < entities.length / 3; i++) {
            int startOffset = tokenOffsets[i * 2];
            int endOffset = tokenOffsets[i * 2 + 1];
            EntityMention.Builder builder = new EntityMention.Builder(startOffset, endOffset, "PERSON");
            entityListBuilder.add(builder.build());
        }
        text.getAttributes().put(EntityMention.class.getName(), entityListBuilder.build());

        int chainForBill = text.getEntityMentions().getItems().get(0).getCoreferenceChainId();
        int chainForGeorge = text.getEntityMentions().getItems().get(1).getCoreferenceChainId();
        assertEquals(-1, chainForBill);
        assertEquals(-1, chainForGeorge);
    }
}