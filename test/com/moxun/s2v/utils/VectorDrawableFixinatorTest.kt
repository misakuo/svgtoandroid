package com.moxun.s2v.utils

import com.moxun.s2v.kt.VectorDrawableFixinator
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

/**
 * Created by moxun on 09/02/2018.
 */

class VectorDrawableFixinatorTest {
    @Test
    fun testFloatPoints() {
        assertThat(parse("M.566"), `is`("M 0.566"))
    }

    fun parse(src: String): String {
        return VectorDrawableFixinator.getContentWithFixedFloatingPoints(src)
    }
}