package me.simplq.service.message;

public interface Message {
  static String FOOTER =
      "Thanks for using simplq.me, a free and open source queue management software.\n\n"
          + "Regards,\n"
          + "Team SimplQ\n"
          + "https://www.simplq.me/";
  ;

  String subject();

  String body();
}
