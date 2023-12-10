package org.davistiba.gui;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class CheckerButtonTest {

    @Parameterized.Parameter
    public String fileName;

    @Parameterized.Parameters(name = "{index}: checkFile {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"images/blackchecker.gif"},
                {"images/blackking.png"},
                {"images/whiteking.png"},
                {"images/whitechecker.gif"}
        });
    }

    @Test
    public void test_getImageResource() {
        URL path = CheckerButton.getImageResource(fileName);
        Assert.assertNotNull("Image file missing", path);
    }


}