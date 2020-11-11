package com.ottpay.paysdk.commons;

import com.ottpay.paysdk.models.ProcessReqBO;
import com.ottpay.paysdk.models.ProcessRespBO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Component
public class ProcessFeign {
    private static Log logger = LogFactory.getLog(ProcessFeign.class);

    @Value("${app.process.read.timeout}")
    private Integer readTimeout;

    @Value("${app.process.connection.timeout}")
    private Integer connectionTimeout;

    //@Value("${app.process.front.api.url}")
    //private String url;

    protected RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);
        restTemplate = new RestTemplate(factory);
    }

    public ProcessRespBO process(@RequestBody ProcessReqBO data, String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<Object>(data, headers);
        ResponseEntity<ProcessRespBO> responseEntity = restTemplate.postForEntity(url, entity, ProcessRespBO.class, data);
        return responseEntity.getBody();
    }
}
