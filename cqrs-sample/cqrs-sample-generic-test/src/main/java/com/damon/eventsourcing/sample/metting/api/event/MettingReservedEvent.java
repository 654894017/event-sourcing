package com.damon.eventsourcing.sample.metting.api.event;

import com.damon.eventsourcing.domain.Event;
import lombok.Data;

@Data
public class MettingReservedEvent extends Event {

    private int start;
    private int end;
    private Long userId;
    private String reserveFlag;
    private String mettingTopic;
    private String mettingContent;
    private String attachmentUrl;

    public MettingReservedEvent(int start, int end, Long userId, String reserveFlag,
                                String mettingTopic,
                                String mettingContent,
                                String attachmentUrl) {
        this.start = start;
        this.end = end;
        this.userId = userId;
        this.reserveFlag = reserveFlag;
        this.mettingTopic = mettingTopic;
        this.mettingContent = mettingContent;
        this.attachmentUrl = attachmentUrl;
    }

    public MettingReservedEvent() {

    }


}
