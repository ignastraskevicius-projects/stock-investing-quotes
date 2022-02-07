package org.ignast.stockinvesting.api.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class RootController {

    @GetMapping(value = "/", produces = VersionedApiMediaTypes.V1)
    public HttpEntity<Root> getRoot() {
        Root root = new Root();
        root.add(linkTo(methodOn(CompanyController.class).defineCompany(new CompanyDTO())).withRel("stocks:company"));
        return new ResponseEntity<>(root, HttpStatus.OK);
    }
}