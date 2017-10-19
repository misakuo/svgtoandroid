package com.moxun.s2v.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class StdColorUtilTest {

    @Test
    public void formatColor() {
        assertThat(StdColorUtil.formatColor("#abcdef"), is("#ABCDEF"));
        assertThat(StdColorUtil.formatColor("#abc"), is("#AABBCC"));
        assertThat(StdColorUtil.formatColor("rgb(255,15,5)"), is("#FF0F05"));
        assertThat(StdColorUtil.formatColor("rgb(100%,0%,0%)"), is("#FF0000"));
        assertThat(StdColorUtil.formatColor("blue"), is("#000000")); // FIXME: Should be #0000FF.
    }
}
