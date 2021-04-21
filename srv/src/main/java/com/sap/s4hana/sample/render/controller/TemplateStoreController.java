package com.sap.s4hana.sample.render.controller;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.*;

import com.sap.s4hana.sample.render.model.GetFormResponse;
import com.sap.s4hana.sample.render.service.AdsService;

import lombok.extern.slf4j.Slf4j;

@Path(TemplateStoreController.PATH)
@Slf4j
public class TemplateStoreController {

	static final String PATH = "/Store";

	@Inject
	private AdsService adsService;

	@GET
    public List<GetFormResponse> getForms() {
		log.info("GET {}", PATH);
		return adsService.getForms();
	}

}
