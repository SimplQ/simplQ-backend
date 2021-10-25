package me.simplq.service.message;

public class StripHtml {
  public static String stripHtml(String htmlMessage) {
    return htmlMessage
        .replace("<p>", "\n")
        .replace("</p>", "\n")
        .replace("<b>", "")
        .replace("</b>", "");
  }
}
