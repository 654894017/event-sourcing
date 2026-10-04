package com.damon.eventsourcing.sample.metting.domain;

import com.damon.eventsourcing.command.CommandProcessor;
import com.damon.eventsourcing.config.EventSourcingConfig;
import com.damon.eventsourcing.sample.metting.api.IMettingCommandService;
import com.damon.eventsourcing.sample.metting.api.command.MettingCancelCommand;
import com.damon.eventsourcing.sample.metting.api.command.MettingDTO;
import com.damon.eventsourcing.sample.metting.api.command.MettingGetCommand;
import com.damon.eventsourcing.sample.metting.api.command.MettingReserveCommand;
import com.damon.eventsourcing.sample.metting.domain.aggregate.CancelReservationStatusEnum;
import com.damon.eventsourcing.sample.metting.domain.aggregate.MeetingId;
import com.damon.eventsourcing.sample.metting.domain.aggregate.Metting;
import com.damon.eventsourcing.sample.metting.domain.aggregate.ReseveStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MettingCommandProcessor extends CommandProcessor<Metting> implements IMettingCommandService {

    public MettingCommandProcessor(EventSourcingConfig eventSourcingConfig) {
        super(eventSourcingConfig);
    }

    @Override
    public ReseveStatus reserve(MettingReserveCommand command) {
        return super.process(command, metting -> metting.reserve(command)).join();
    }

    @Override
    public CancelReservationStatusEnum cancel(MettingCancelCommand cancel) {
        return super.process(cancel, metting ->
                metting.cancel(cancel)
        ).join();
    }

    @Override
    public MettingDTO get(MettingGetCommand get) {
        return super.process(get, metting ->
                new MettingDTO(metting.getSchedule(), metting.getMeetingDate(), metting.getReserveRecord())
        ).join();
    }

    @Override
    public Metting getAggregateSnapshot(long aggregateId, Class<Metting> classes) {
        Metting metting = new Metting(new MeetingId(aggregateId));
        return metting;
    }
}
