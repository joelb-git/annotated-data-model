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

import com.basistech.rosette.dm.jackson.AnnotatedDataModelModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;

/**
 * Common utilities for tests.
 */
public class AdmAssert extends Assert {
    public static ObjectMapper objectMapper() {
        return AnnotatedDataModelModule.setupObjectMapper(new ObjectMapper());
    }
}