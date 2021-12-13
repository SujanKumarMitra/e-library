package com.github.sujankumarmitra.notificationservice.v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.RedirectView;

/**
 * @author skmitra
 * @since Dec 13/12/21, 2021
 */
@Controller
public class SwaggerUiController {

    @GetMapping
    @Operation(hidden = true)
    public RedirectView redirectToSwaggerUi() {
        return new RedirectView("swagger-ui.html");
    }
}
