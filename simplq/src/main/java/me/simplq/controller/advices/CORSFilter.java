// package me.simplq.controller.advices;

// import java.io.IOException;

// import javax.servlet.Filter;
// import javax.servlet.FilterChain;
// import javax.servlet.ServletException;
// import javax.servlet.ServletRequest;
// import javax.servlet.ServletResponse;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

// import org.apache.http.HttpStatus;
// import org.springframework.core.Ordered;
// import org.springframework.core.annotation.Order;
// import org.springframework.stereotype.Component;

// import lombok.extern.slf4j.Slf4j;

// @Component
// @Order(Ordered.HIGHEST_PRECEDENCE)
// @Slf4j
// public class CORSFilter implements Filter {

//   @Override
//   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//       throws IOException, ServletException {
    
//         log.info("CORS headers");

//         var httpResponse = (HttpServletResponse) response;
//         var httpRequest = (HttpServletRequest) request;

//         httpResponse.addHeader("Access-Control-Allow-Origin", "*");
//         httpResponse.addHeader("Access-Control-Allow-Methods", "*");
//         httpResponse.addHeader("Access-Control-Allow-Headers", "*");

//         if ("OPTIONS".equals(httpRequest.getMethod())) {
//           httpResponse.setStatus(HttpStatus.SC_OK);
//           return;
//         }

//         chain.doFilter(request, response);
//   }
  
// }
