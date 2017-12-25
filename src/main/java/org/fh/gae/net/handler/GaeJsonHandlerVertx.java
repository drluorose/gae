package org.fh.gae.net.handler;

import com.alibaba.fastjson.JSON;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.net.vo.BidRequest;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Component
@Slf4j
public class GaeJsonHandlerVertx implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        try {
            parseJson(ctx);

        } catch (Exception e) {
            ctx.put("_err", e);
            ctx.fail(400);
        }
    }

    private void parseJson(RoutingContext ctx) {
        // 取出body
        byte[] body = ctx.getBody().getBytes();

        if (log.isDebugEnabled()) {
            log.debug("gae_request\t{}", new String(body));
        }

        // 反序列化
        BidRequest requestObj = JSON.parseObject(body, 0, body.length, Charset.forName("utf-8"), BidRequest.class);

        ctx.put("_req", requestObj);
        ctx.next();

    }
}
