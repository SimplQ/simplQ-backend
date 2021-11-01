package me.simplq.service.message;

public interface Message {
  String FOOTER =
      "<p>Thanks for using simplq.me, a free and open source queue management software.</p>"
          + "<p>Regards,</p>"
          + "<p>Team SimplQ</p>"
          + "<p>https://www.simplq.me/</p>";

  String subject();

  String body();

  String bodyHtml();

  String shortBody();

  Boolean isPriority();
}
