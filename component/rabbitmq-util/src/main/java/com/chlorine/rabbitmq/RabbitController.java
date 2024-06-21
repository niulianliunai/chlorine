package com.chlorine.rabbitmq;

import com.chlorine.rabbitmq.delay.DelaySender;
import com.chlorine.rabbitmq.direct.DirectSender;
import com.chlorine.rabbitmq.fanout.FanoutSender;
import com.chlorine.base.util.ThreadUtil;
import com.chlorine.rabbitmq.simple.SimpleSender;
import com.chlorine.rabbitmq.topic.TopicSender;
import com.chlorine.rabbitmq.work.WorkSender;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "RabbitController", description = "RabbitMQ功能测试")
@RestController
@RequestMapping("/rabbit")
public class RabbitController {

    @Autowired
    private SimpleSender simpleSender;
    @Autowired
    private WorkSender workSender;
    @Autowired
    private FanoutSender fanoutSender;
    @Autowired
    private DirectSender directSender;
    @Autowired
    private TopicSender topicSender;
    @Autowired
    private DelaySender delaySender;

    @ApiOperation("延迟模式")
    @RequestMapping(value = "/delay", method = RequestMethod.GET)
    public void delayTest() {
        delaySender.send();
    }

    @ApiOperation("简单模式")
    @RequestMapping(value = "/simple", method = RequestMethod.GET)
    public void simpleTest() {
        for (int i = 0; i < 10; i++) {
            simpleSender.send();
            ThreadUtil.sleep(1000L);
        }
    }

    @ApiOperation("工作模式")
    @RequestMapping(value = "/work", method = RequestMethod.GET)
    @ResponseBody
    public void workTest() {
        for (int i = 0; i < 10; i++) {
            workSender.send(i);
            ThreadUtil.sleep(1000L);
        }
    }

    @ApiOperation("发布/订阅模式")
    @RequestMapping(value = "/fanout", method = RequestMethod.GET)
    @ResponseBody
    public void fanoutTest() {
        for (int i = 0; i < 10; i++) {
            fanoutSender.send(i);
            ThreadUtil.sleep(1000L);
        }
    }

    @ApiOperation("路由模式")
    @RequestMapping(value = "/direct", method = RequestMethod.GET)
    @ResponseBody
    public void directTest() {
        for (int i = 0; i < 10; i++) {
            directSender.send(i);
            ThreadUtil.sleep(1000L);
        }
    }

    @ApiOperation("通配符模式")
    @RequestMapping(value = "/topic", method = RequestMethod.GET)
    @ResponseBody
    public void topicTest() {
        for (int i = 0; i < 10; i++) {
            topicSender.send(i);
            ThreadUtil.sleep(1000L);
        }
    }
}
