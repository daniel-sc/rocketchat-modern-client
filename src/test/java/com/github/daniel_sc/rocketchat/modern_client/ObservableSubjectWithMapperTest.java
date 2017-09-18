package com.github.daniel_sc.rocketchat.modern_client;

import org.junit.Assert;
import org.junit.Test;

public class ObservableSubjectWithMapperTest {

    @Test
    public void testToString() {
        Assert.assertEquals("ObservableSubjectWithMapper{mapper=null, observable=null}", new ObservableSubjectWithMapper(null, null, null).toString());
    }
}
