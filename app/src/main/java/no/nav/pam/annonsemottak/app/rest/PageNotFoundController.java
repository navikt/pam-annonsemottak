package no.nav.pam.annonsemottak.app.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class PageNotFoundController implements ErrorController {

    private final static Logger LOG = LoggerFactory.getLogger(PageNotFoundController.class);

    @Override
    public String getErrorPath() {
        return "index.html";
    }

    @RequestMapping("/error")
    public String index() {
        LOG.debug("Got error page called!");
        return "index.html";
    }


}
