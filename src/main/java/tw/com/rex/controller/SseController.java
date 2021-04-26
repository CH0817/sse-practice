package tw.com.rex.controller;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
@RequestMapping("/sse")
public class SseController {

    private static final Map<String, SseEmitter> sseCache = new ConcurrentHashMap<>();

    /**
     * 訂閱
     *
     * @param id 訂閱 ID
     * @return SseEmitter
     */
    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(String id) {
        // 建立 SseEmitter 物件並設定超時時間
        SseEmitter sseEmitter = new SseEmitter(1000L * 60L * 60L);
        // id 加入 cache
        sseCache.put(id, sseEmitter);
        // 超時處理
        sseEmitter.onTimeout(() -> {
            log.info("{} 超時", id);
            sseCache.remove(id);
        });
        // 連線成功處理
        sseEmitter.onCompletion(() -> log.info("訂閱完成"));
        return sseEmitter;
    }

    /**
     * 推播
     *
     * @param id   推播目標
     * @param data 推播內容
     */
    @GetMapping(path = "/push", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void produce(String id, String data) throws IOException {
        // 取出 SseEmitter
        SseEmitter sseEmitter = sseCache.get(id);
        if (Objects.nonNull(sseEmitter)) {
            // 推播
            sseEmitter.send(data);
        }
    }

    /**
     * 中斷 SSE
     *
     * @param id 目標
     */
    @GetMapping(path = "/over", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void over(String id) {
        if (Objects.nonNull(sseCache.get(id))) {
            sseCache.remove(id);
        }
    }

}