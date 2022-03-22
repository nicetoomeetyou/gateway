package com.laicr.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/provider")
public class GrayController {



    @GetMapping(value="/test")
    public String testV1(HttpServletRequest request){
        System.out.println("/gray/test:"+request.getHeader("gray"));
        return "gray:"+request.getServerPort();
    }




}
