package tw.com.rex.sse.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-Send Events test controller
 */
@RestController
@RequestMapping("/sse")
public class SseController {

    private Logger logger = LoggerFactory.getLogger(SseController.class);

    private static final Map<String, SseEmitter> sseCache = new ConcurrentHashMap<>();

    /**
     * 訂閱
     *
     * @param id 訂閱 ID
     * @return SseEmitter
     */
    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(String id) {
        if (!isIdSubscribed(id)) {
            // 建立 SseEmitter 物件並設定超時時間
            SseEmitter sseEmitter = new SseEmitter(5000L * 60L * 60L);
            // id 加入 cache
            sseCache.put(id, sseEmitter);
            // 超時處理
            sseEmitter.onTimeout(() -> {
                logger.info("id: {} subscribe time out", id);
                sseCache.remove(id);
            });
            logger.info("id: {} subscribe success", id);
            return sseEmitter;
        }
        logger.info("id: {} already subscribe", id);
        return sseCache.get(id);
    }

    /**
     * 推播
     *
     * @param id   推播目標
     * @param data 推播內容
     */
    @GetMapping(path = "/push", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void produce(String id, String data) throws IOException {
        if (isIdSubscribed(id)) {
            // 推播
            sseCache.get(id).send(data);
            logger.info("send data to id: {} success", id);
        } else {
            logger.info("not found id: {}", id);
        }
    }

    /**
     * 中斷 SSE
     *
     * @param id 目標
     */
    @GetMapping(path = "/unsubscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void unsubscribe(String id) {
        if (isIdSubscribed(id)) {
            sseCache.remove(id);
            logger.info("id: {} unsubscribe", id);
        } else {
            logger.info("id: {} has no subscribe", id);
        }
    }

    private boolean isIdSubscribed(String id) {
        return Objects.nonNull(sseCache.get(id));
    }

}