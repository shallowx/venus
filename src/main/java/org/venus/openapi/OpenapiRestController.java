package org.venus.openapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.venus.support.GenericRestApiResponse;
import org.venus.support.VenusRestApiCode;

@RestController
@RequestMapping("/openapi")
@Slf4j
public class OpenapiRestController {

    @Autowired
    private IOpenapiService iOpenapiService;
    private static final String REDIRECT_302 = "302";
    private static final String REDIRECT_301 = "301";

    /**
     * If redirection through this system is not required, this api is suitable. And redirected by the caller
     * However, if the caller does not report the analysis data, the data may be lost.
     *
     * If the caller needs to cache the response, and it needs to register a callback method to update the new data from venus.
     * Register method see 'admin(package)' function
     */
    @GetMapping("/mapping")
    public GenericRestApiResponse<OpenapiResponse> get(@RequestParam String original) {
        try {
            return GenericRestApiResponse.success(OpenapiResponse.form(
                    iOpenapiService.get(original)
            ));
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Get venus openapi original-url and url-encode mapping entity failure", e);
            }
            return GenericRestApiResponse.fail(VenusRestApiCode.VENUS_ADMIN_EXCEPTION, VenusRestApiCode.VENUS_ADMIN_EXCEPTION.message());
        }
    }

    /**
     * If redirection through this system is required.
     */
    @GetMapping("/redirect")
    public RedirectView redirect(@RequestParam String original) {
        RedirectEntity entity = iOpenapiService.redirect(original);
        if (entity == null) {
            return new RedirectView("/error");
        }

        int redirect = entity.getRedirect();
        if (String.valueOf(redirect).equals(REDIRECT_301)) {
            return new RedirectView(entity.getOriginalUrl(), true);
        }

        if (String.valueOf(redirect).equals(REDIRECT_302)) {
            return new RedirectView(entity.getOriginalUrl());
        }
        // default redirect
        return new RedirectView("/error");
    }
}
