import org.junit.Test;
import org.junit.Assert;

public class SlidePuzzleTester {
    @Test
    public void testBasicFunctions() {
        SlidePuzzle p = new SlidePuzzle(4, 7);

        Assert.assertFalse(p.up());
        Assert.assertFalse(p.left());
        Assert.assertTrue(p.right());
        Assert.assertTrue(p.down());

        p.randomize(30, 10);
        Assert.assertEquals("1 8 2 3 4 5 6 7 9 16 10 11 12 13 " +
        "14 15 23 18 19 20 27 21 22 24 17 25 26 0", SlidePuzzle.gridToString(p));
    }
}
