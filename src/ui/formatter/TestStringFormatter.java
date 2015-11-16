package ui.formatter;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestStringFormatter {

    /*
     * Test for align left, success.
     */
    @Test
    public void testLeftOk() {
        String result = StringFormatter.formatString("haha", StringFormatter.Alignment.ALIGN_LEFT,
                10);
        assertEquals("haha      ", result);
    }

    
    /*
     * Test for align center, success. Space can be distributed evenly
     * between the left part and the right part.
     */
    @Test
    public void testCenterSuccessEven() {
        String result = StringFormatter.formatString("pencil", StringFormatter.Alignment.ALIGN_CENTER,
                10);
        assertEquals(result, "  pencil  ");
    }
    
    /*
     * Test for align center, success. Space can't be distributed evenly
     * between the left part and the right part. The space on the
     * right should be 1 more than the left.
     */
    @Test
    public void testCenterSuccessNotEven() {
        String result = StringFormatter.formatString("mattress", StringFormatter.Alignment.ALIGN_CENTER,
                11);
        assertEquals(result, " mattress  ");
    }
    
    /*
     * Test for align right, success.
     */
    @Test
    public void testRightOk() {
        String result = StringFormatter.formatString("cable", StringFormatter.Alignment.ALIGN_RIGHT,
                7);
        assertEquals(result, "  cable");
    }

}
