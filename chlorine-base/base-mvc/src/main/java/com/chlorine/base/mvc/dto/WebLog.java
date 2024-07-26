package com.chlorine.base.mvc.dto;

import lombok.Data;

@Data
public class WebLog {

    private String description;

    private String username;

    private Long startTime;

    private Integer spendTime;

    private String uri;

    private String url;

    private String method;

    private String ip;

    private Object parameter;

    private Object result;
}
