package com.damon.eventsourcing.sample.metting.api.command;

import com.damon.eventsourcing.domain.Command;
import com.damon.eventsourcing.sample.metting.domain.aggregate.MettingTime;
import lombok.NonNull;

public class MettingReserveCommand extends Command {

    private Long userId;
    private String mettingDate;
    private MettingTime mettingTime;
    private String mettingTopic;
    private String mettingContent;
    private String attachmentUrl;

    private String mettingNumber;

    public MettingReserveCommand(
            @NonNull Long aggregateId,
            @NonNull Long userId,
            @NonNull MettingTime mettingTime,
            @NonNull String mettingTopic,
            @NonNull String mettingDate,
            @NonNull String mettingNumber,
            String mettingContent,
            String attachmentUrl
    ) {
        super(aggregateId);
        this.mettingTime = mettingTime;
        this.userId = userId;
        this.mettingTopic = mettingTopic;
        this.mettingContent = mettingContent;
        this.attachmentUrl = attachmentUrl;
        this.mettingDate = mettingDate;
        this.mettingNumber = mettingNumber;
    }

    public MettingTime getMettingTime() {
        return mettingTime;
    }

    public String getMettingTopic() {
        return mettingTopic;
    }

    public String getMettingContent() {
        return mettingContent;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public String getMettingDate() {
        return mettingDate;
    }

    public String getMettingNumber() {
        return mettingNumber;
    }
}
