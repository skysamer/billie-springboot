package com.lab.smartmobility.billie.controller;

import com.lab.smartmobility.billie.config.JwtTokenProvider;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class SseController {
    public static Map<String, SseEmitter> SSE_EMITTERS = new ConcurrentHashMap<>();
    private final JwtTokenProvider jwtTokenProvider;
    private final Log log= LogFactory.getLog(getClass());

    @GetMapping(value = "/sub", consumes = MediaType.ALL_VALUE)
    @ApiOperation(value = "실시간 데이터를 받기위한 api", notes = "로그인과 동시에 해당 api 구독 필요")
    @ApiResponses({
            @ApiResponse(code = 200, message = "notification 키를 이벤트 리스너로 제어해야함")
    })
    public SseEmitter subscribe(@ApiParam(value = "토큰값을 'token' 키로 매핑하여 전송") @RequestParam(value = "token") String token) {
        // 토큰에서 user의 pk값 파싱
        String email = jwtTokenProvider.getUserPk(token);

        // 현재 클라이언트를 위한 SseEmitter 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            sseEmitter.send(SseEmitter.event().name("connect"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // user의 pk값을 key값으로 해서 SseEmitter를 저장
        SSE_EMITTERS.put(email, sseEmitter);

        sseEmitter.onCompletion(() -> SSE_EMITTERS.remove(email));
        sseEmitter.onTimeout(() -> SSE_EMITTERS.remove(email));
        sseEmitter.onError((e) -> SSE_EMITTERS.remove(email));

        log.info(sseEmitter.toString());
        return sseEmitter;
    }
}
