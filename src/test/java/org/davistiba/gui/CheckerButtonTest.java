package org.davistiba.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URL;
import java.util.stream.Stream;

public class CheckerButtonTest {

    public static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("images/blackchecker.gif"),
                Arguments.of("images/blackking.png"),
                Arguments.of("images/whiteking.png"),
                Arguments.of("images/whitechecker.gif")
        );
    }

    @ParameterizedTest(name = "{index}: checkFile {0}")
    @MethodSource("data")
    public void test_getImageResource(String fileName) {
        URL path = CheckerButton.getImageResource(fileName);
        Assertions.assertNotNull(path, "Image file missing");
    }
}