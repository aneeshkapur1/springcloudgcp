package com.springcloudgcp.pubsubexample.controller;


import com.springcloudgcp.pubsubexample.PubsubexampleApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class WebAppController {


    @Autowired
    private PubsubexampleApplication.PubSubOutboundGateway pubSubOutboundGateway;

    @PostMapping("/publishMessage")
    public RedirectView publishMessage(@RequestParam("message") String message){

        pubSubOutboundGateway.sendToPubsub(message);
        return new RedirectView("/");
    }
}
