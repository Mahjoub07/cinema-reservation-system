package com.cinema.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SeatEventController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SeatLockService seatLockService;

    public SeatEventController(SimpMessagingTemplate messagingTemplate, SeatLockService seatLockService) {
        this.messagingTemplate = messagingTemplate;
        this.seatLockService = seatLockService;
    }

    @MessageMapping("/seat/select")
    public void selectSeat(@Payload SeatEvent event) {
        seatLockService.lockSeat(event.getMovieId(), event.getShowTime(), event.getSeatNumber(), event.getSessionId());
        SeatEvent lockedEvent = new SeatEvent(
                event.getMovieId(), event.getShowTime(), event.getSeatNumber(),
                SeatEvent.Action.LOCKED, event.getSessionId()
        );
        messagingTemplate.convertAndSend(
                "/topic/seats/" + event.getMovieId() + "/" + event.getShowTime(),
                lockedEvent
        );
    }

    @MessageMapping("/seat/release")
    public void releaseSeat(@Payload SeatEvent event) {
        seatLockService.unlockSeat(event.getMovieId(), event.getShowTime(), event.getSeatNumber(), event.getSessionId());
        SeatEvent releasedEvent = new SeatEvent(
                event.getMovieId(), event.getShowTime(), event.getSeatNumber(),
                SeatEvent.Action.RELEASED, event.getSessionId()
        );
        messagingTemplate.convertAndSend(
                "/topic/seats/" + event.getMovieId() + "/" + event.getShowTime(),
                releasedEvent
        );
    }
}
