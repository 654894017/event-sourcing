package com.damon.eventsourcing.sample.train.damain_service;


import com.damon.eventsourcing.command.CommandProcessor;
import com.damon.eventsourcing.config.EventSourcingConfig;
import com.damon.eventsourcing.sample.train.aggregate.TrainStock;
import com.damon.eventsourcing.sample.train.aggregate.value_object.TicketBuyStatus;
import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.S2S_TICKET_PROTECT_CANCEL_STATUS;
import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.S2S_TICKET_PROTECT_STATUS;
import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.SEAT_TYPE;
import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.TICKET_CANCEL_STATUS;
import com.damon.eventsourcing.sample.train.command.*;
import com.damon.eventsourcing.sample.train.dto.TrainStockDTO;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class TrainStockCommandProcessor extends CommandProcessor<TrainStock> {

    public TrainStockCommandProcessor(EventSourcingConfig eventSourcingConfig) {
        super(eventSourcingConfig);
    }

    @Override
    public TrainStock getAggregateSnapshot(long aggregateId, Class<TrainStock> classes) {
        return super.getAggregateSnapshot(aggregateId, classes);
    }

    public void createTrain(TrainCreateCommand command) {
        super.process(command, () ->
                new TrainStock(
                        command.getAggregateId(),
                        command.getStation2StationBusinessList(), command.getBusinessTrainCarriageList(),
                        command.getStation2StationFirstList(), command.getFirstTrainCarriageList(),
                        command.getStation2StationSecondList(), command.getSecondTrainCarriageList(),
                        command.getStation2StationStandingList(), command.getStandingTrainCarriageList()
                )
        ).join();
    }


    public TrainStockDTO getTrain(TrainStockGetCommand command) {
        return super.process(command, ts -> {
            TrainStockDTO stock = new TrainStockDTO();
            stock.setId(ts.getId());
            Map<SEAT_TYPE, ConcurrentSkipListMap<Integer, BitSet>> s2sSeatCountMap = ts.getS2sSeatCountMap();
            Map<SEAT_TYPE, ConcurrentSkipListMap<Integer, Integer>> s2ssc = new HashMap<>();

            s2sSeatCountMap.forEach((seatType, skipListMap) -> {
                ConcurrentSkipListMap<Integer, Integer> map = new ConcurrentSkipListMap<>();
                skipListMap.forEach((key, value) -> {
                    map.put(key, ts.getSeatCountMap().get(seatType) - value.cardinality());
                });
                s2ssc.put(seatType, map);
            });
            stock.setS2sSeatCountMap(s2ssc);
            return stock;
        }).join();
    }

    public S2S_TICKET_PROTECT_STATUS protectTicket(TicketProtectCommand command) {
        return super.process(command, ts ->
                ts.protectS2STicket(command)
        ).join();
    }

    public S2S_TICKET_PROTECT_CANCEL_STATUS cancelProtectTicket(TicketProtectCancelCommand command) {
        return super.process(command, ts ->
                ts.cancelProtectTicket(command)
        ).join();
    }

    public TicketBuyStatus buyTicket(TicketBuyCommand command) {
        return super.process(command, ts ->
                ts.buyTicket(command)
        ).join();
    }

    public TICKET_CANCEL_STATUS cancelTicket(TicketCancelCommand command) {
        return super.process(command, ts ->
                ts.cancelTicket(command)
        ).join();
    }

}

