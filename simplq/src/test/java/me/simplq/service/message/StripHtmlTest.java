package me.simplq.service.message;

import static me.simplq.service.message.StripHtml.stripHtml;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StripHtmlTest {
  @Test
  void testStripHtml() {
    var htmlString = "<p>Hello</p><b>this is bold text</b>";
    var strippedText = stripHtml(htmlString);
    assertEquals("\nHello\nthis is bold text", strippedText);
  }
}
