package com.moxun.s2v.utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StdColorUtilTest {

    @Test
    public void formatColor() {
        assertThat(StdColorUtil.formatColor("#abcdef"), is("#ABCDEF"));
        assertThat(StdColorUtil.formatColor("#abc"), is("#AABBCC"));
        assertThat(StdColorUtil.formatColor("rgb(255,15,5)"), is("#FF0F05"));
        assertThat(StdColorUtil.formatColor("rgb(100%,0%,0%)"), is("#FF0000"));
        assertThat(StdColorUtil.formatColor("blue"), is("#0000FF")); // FIXME: Should be #0000FF.
    }
}
