package com.moxun.s2v.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SVGAttrParserTest {

    @Test
    public void rectToPath() {
        assertThat(SVGAttrParser.rectToPath(10, 10.5, 100, 100, 0, 0),
                is("M 10,10.5 L 110,10.5 L 110,110.5 L 10,110.5 z"));

        assertThat(SVGAttrParser.rectToPath(10, 10.5, 100, 100, 15, 15), is(""
                + "M 95,10.5 Q 110,10.5 110,25.5 "
                + "L 110,95.5 Q 110,110.5 95,110.5 "
                + "L 25,110.5 Q 10,110.5 10,95.5 "
                + "L 10,25.5 Q 10,10.5 25,10.5 "
                + "z"));
    }

    @Test
    public void circleToPath() {
        assertThat(SVGAttrParser.circleToPath(16.852, 7.376, 5), is(""
                + "M 11.85,7.38 "
                + "a 5,5 0 0 1 10,0 "
                + "a 5,5 0 0 1 -10,0 "
                + "z"));
    }

    @Test
    public void polygonToPath() {
        assertThat(SVGAttrParser.polygonToPath("0 0 0 100 100 100 100 0 0 0"),
                is("M 0,0 L 0,100 L 100,100 L 100,0 L 0,0 z"));

        assertThat(SVGAttrParser.polygonToPath(" 60,20  100,40 100,80 60,100 20,80 20,40 "),
                is("M 60,20 L 100,40 L 100,80 L 60,100 L 20,80 L 20,40 z"));

        assertThat(SVGAttrParser.polygonToPath("0 0 1,1"),
                is("M 0,0 L 1,1 z"));
    }

    @Test
    public void ellipseToPath() {
        assertThat(SVGAttrParser.ellipseToPath(20, 16, 20, 16), is(""
                + "M 20,0 "
                + "C 31.05,0 40,7.16 40,16 "
                + "C 40,24.84 31.05,32 20,32 "
                + "C 8.95,32 0,24.84 0,16 "
                + "C 0,7.16 8.95,0 20,0 "
                + "z"));
    }
}
