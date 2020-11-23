package com.moxun.s2v.utils;

import com.intellij.openapi.util.text.StringUtil;
import org.junit.Assert;
import org.junit.Test;

public class StringProcessTest {
    @Test
    public void validLineSeparators() {
        Assert.assertTrue(assertPass("\ntestcase\n"));
        Assert.assertFalse(assertPass("\r\ntestcase\r\n"));
    }

    private boolean assertPass(String input) {
        try {
            StringUtil.assertValidSeparators(input);
        } catch (AssertionError error) {
            return false;
        }
        return true;
    }
}
