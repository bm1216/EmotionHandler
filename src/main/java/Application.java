/**
 * Created by dev on 28/01/18.
 */

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

  @Controller
  @EnableAutoConfiguration
  public class Application {

    @RequestMapping("/")
    @ResponseBody
    String home() {
      Tracker tracker = new Tracker();
      String output = tracker.run();
      return output.replace("\n", "<br />\n");
    }

    public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
    }

  }


