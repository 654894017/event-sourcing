package com.damon.eventsourcing.sample.train.aggregate;


import cn.hutool.core.collection.CollUtil;
import com.damon.eventsourcing.domain.AggregateRoot;
import com.damon.eventsourcing.sample.train.aggregate.value_object.*;
import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.*;
import com.damon.eventsourcing.sample.train.command.TicketBuyCommand;
import com.damon.eventsourcing.sample.train.command.TicketCancelCommand;
import com.damon.eventsourcing.sample.train.command.TicketProtectCancelCommand;
import com.damon.eventsourcing.sample.train.command.TicketProtectCommand;
import com.damon.eventsourcing.sample.train.event.*;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

/**
 * 车次聚合根
 * <p>
 * <p>
 * 实现：购票、取消购票、站点区间购票数量保护（最多、最少可购票数量）、自定义选择座位(多人购买)
 *
 * @author xianpinglu
 */
@Data
public class TrainStock extends AggregateRoot {

    private static final long serialVersionUID = -7293431876516831042L;
    /**
     * 站点区间放大因子
     */
    private final Integer AMPLIFICATION_FACTOR = 10000;

    /**
     * 座位类型与车厢的映射关系
     */
    private Map<SEAT_TYPE, List<TrainCarriage>> trainCarriageMap;
    /**
     * 站点到站点间的座位数量
     */
    private Map<SEAT_TYPE, ConcurrentSkipListMap<Integer, BitSet>> s2sSeatCountMap;
    /**
     * 用户购买的车票信息
     */
    private Map<Long, UserSeatInfo> userTicketMap;
    /**
     * 当前车次的总共座位数量
     */
    private Map<SEAT_TYPE, Integer> seatCountMap;

    private Map<SEAT_TYPE, Map<Integer, S2SMaxTicketCountProtectInfo>> s2sSeatStrictProtectMap;


    public TrainStock(Long id,
                      List<Integer> station2StationBusinessList, List<TrainCarriage> businessTrainCarriageList,
                      List<Integer> station2StationFirstList, List<TrainCarriage> firstTrainCarriageList,
                      List<Integer> station2StationSecondList, List<TrainCarriage> secondTrainCarriageList,
                      List<Integer> station2StationStandingList, List<TrainCarriage> standingTrainCarriageList
    ) {
        super(id);
        TrainCreatedEvent event = new TrainCreatedEvent();
        event.setBusinessTrainCarriageList(businessTrainCarriageList);
        event.setFirstTrainCarriageList(firstTrainCarriageList);
        event.setSecondTrainCarriageList(secondTrainCarriageList);
        event.setStandingTrainCarriageList(standingTrainCarriageList);
        event.setBusinessSeatCount(businessTrainCarriageList.stream().collect(Collectors.summingInt(tc -> tc.getEndNumber() - tc.getStartNumber() + 1)));
        event.setFirstSeatCount(firstTrainCarriageList.stream().collect(Collectors.summingInt(tc -> tc.getEndNumber() - tc.getStartNumber() + 1)));
        event.setSecondSeatCount(secondTrainCarriageList.stream().collect(Collectors.summingInt(tc -> tc.getEndNumber() - tc.getStartNumber() + 1)));
        event.setStandingCount(standingTrainCarriageList.stream().collect(Collectors.summingInt(tc -> tc.getEndNumber() - tc.getStartNumber() + 1)));
        event.setStation2StationBusinessList(station2StationBusinessList != null ? station2StationBusinessList : new ArrayList<>(0));
        event.setStation2StationFirstList(station2StationFirstList != null ? station2StationFirstList : new ArrayList<>(0));
        event.setStation2StationSecondList(station2StationSecondList != null ? station2StationSecondList : new ArrayList<>(0));
        event.setStation2StationStandingList(station2StationStandingList != null ? station2StationStandingList : new ArrayList<>(0));
        super.applyNewEvent(event);
    }

    /**
     * 计算站点间卖了多少张票
     *
     * @param from
     * @param to
     * @return
     */
    private Map<SEAT_PROTECT_TYPE, List<UserSeatInfo>> calculateS2SSoldTicketCount(Integer from, Integer to, SEAT_TYPE seatType) {
        Map<SEAT_PROTECT_TYPE, List<UserSeatInfo>> map = new HashMap<>();
        map.put(SEAT_PROTECT_TYPE.STRICT_PROTECT, new ArrayList<>());
        map.put(SEAT_PROTECT_TYPE.GENERAL, new ArrayList<>());
        userTicketMap.values().stream().filter(info ->
                info.getStartStationNumber().equals(from) && info.getEndStationNumber().equals(to) && info.getSeatType().equals(seatType)
        ).forEach(info -> {
            if (SEAT_PROTECT_TYPE.STRICT_PROTECT.equals(info.getSeatProtectType())) {
                List<UserSeatInfo> protectList = map.get(info.getSeatProtectType());
                protectList.add(info);
            } else {
                List<UserSeatInfo> generalList = map.get(info.getSeatProtectType());
                generalList.add(info);
            }
        });
        return map;
    }

    /**
     * 取消站点车票预留
     *
     * @param command
     */
    public S2S_TICKET_PROTECT_CANCEL_STATUS cancelProtectTicket(TicketProtectCancelCommand command) {
        S2SMaxTicketCountProtectInfo protect = s2sSeatStrictProtectMap.get(command.getSeatType()).get(key(command.getStartStationNumber(), command.getEndStationNumber()));
        if (protect != null) {
            TicketProtectCanceledEvent event = new TicketProtectCanceledEvent();
            event.setStartStationNumber(command.getStartStationNumber());
            event.setEndStationNumber(command.getEndStationNumber());
            event.setSeatType(command.getSeatType());
            super.applyNewEvent(event);
            return S2S_TICKET_PROTECT_CANCEL_STATUS.SUCCEED;
        }
        return S2S_TICKET_PROTECT_CANCEL_STATUS.NOT_EXIST;
    }

    private Integer key(Integer from, Integer to) {
        return from * AMPLIFICATION_FACTOR + to;
    }

    private Integer fromKey(Integer from) {
        return from * AMPLIFICATION_FACTOR;
    }

    private Integer toKey(Integer to) {
        return (to - 1) * AMPLIFICATION_FACTOR + to;
    }

    /**
     * 预留站点票
     *
     * @param command
     * @return
     */
    public S2S_TICKET_PROTECT_STATUS protectS2STicket(TicketProtectCommand command) {

        if (command.getMaxCanBuyTicketCount() < 0) {
            throw new IllegalArgumentException("The maximum number of tickets that can be purchased between stations cannot be less than 0");
        }
        if (command.getMinCanBuyTicketCount() < 0) {
            throw new IllegalArgumentException("The number of reserved tickets between stations should not be less than 0");
        }

        //区间已被预留不允许重复预留
        if (s2sSeatStrictProtectMap.get(command.getSeatType()).get(key(command.getStartStationNumber(), command.getEndStationNumber())) != null) {
            return S2S_TICKET_PROTECT_STATUS.PROTECTED;
        }
        BitSet bitSet = new BitSet();
        for (BitSet set : s2sSeatCountMap.get(command.getSeatType()).subMap(
                fromKey(command.getStartStationNumber()),
                Boolean.FALSE,
                toKey(command.getEndStationNumber()),
                Boolean.TRUE
        ).values()) {
            bitSet.or(set);
        }

        s2sSeatStrictProtectMap.get(command.getSeatType()).values().forEach(strict -> {
            strict.getS2sProtectSeatIndexBitSet().subMap(
                    fromKey(command.getStartStationNumber()),
                    Boolean.FALSE,
                    toKey(command.getEndStationNumber()),
                    Boolean.TRUE
            ).values().forEach(set ->
                    bitSet.or(set)
            );
        });

        //bitset or运算后，如果索引对应的值是true的说明已经被预留了。
        int count = 0, tempIndex = 0;
        BitSet protectSeatIndexBitSet = new BitSet();
        for (; ; ) {
            //判断当前空余的票是否满足预留要求。返回第一个为false的索引位置
            int index = bitSet.nextClearBit(tempIndex);
            if (index < 0 || (index > seatCountMap.get(command.getSeatType()) - 1)) {
                return S2S_TICKET_PROTECT_STATUS.NOT_ENOUGH;
            }
            protectSeatIndexBitSet.set(index);
            ++count;
            tempIndex = index + 1;
            //剩余的票满足预留要求，跳出循环
            if (count == command.getMinCanBuyTicketCount()) {
                break;
            }
        }

        TicketProtectSucceedEvent event = new TicketProtectSucceedEvent();
        event.setProtectCanBuyTicketCount(command.getMinCanBuyTicketCount());
        event.setMaxCanBuyTicketCount(command.getMaxCanBuyTicketCount());
        event.setStartStationNumber(command.getStartStationNumber());
        event.setEndStationNumber(command.getEndStationNumber());
        event.setProtectSeatIndex(protectSeatIndexBitSet.toLongArray());
        event.setSeatType(command.getSeatType());
        super.applyNewEvent(event);
        return S2S_TICKET_PROTECT_STATUS.SUCCEED;
    }

    /**
     * 指定座位购买
     *
     * @return
     */
    private TicketBuyStatus selectSeatBuyTicket(TicketBuyCommand command) {
        Map<SEAT_PROTECT_TYPE, List<UserSeatInfo>> map = calculateS2SSoldTicketCount(
                command.getStartStationNumber(),
                command.getEndStationNumber(),
                command.getSeatType());

        BitSet bitSet = new BitSet();
        for (BitSet set : s2sSeatCountMap.get(command.getSeatType()).subMap(
                fromKey(command.getStartStationNumber()),
                Boolean.FALSE,
                toKey(command.getEndStationNumber()),
                Boolean.TRUE
        ).values()) {
            bitSet.or(set);
        }

        List<Integer> seatIndexs = command.getSeatIndexs();
        List<TrainCarriage> trainCarriages = trainCarriageMap.get(command.getSeatType());
        List<List<Integer>> indexs = new ArrayList<>();
        for (TrainCarriage tc : trainCarriages) {
            Integer start = tc.getStartNumber(), end = tc.getEndNumber();
            for (int index = start; index <= end; index = index + 5) {
                List<Integer> actualSeatIndexs = new ArrayList<>();
                boolean flag = false;
                for (Integer seatIndex : seatIndexs) {
                    int actualSeatIndex = seatIndex + index;
                    if (bitSet.get(actualSeatIndex)) {
                        flag = true;
                        break;
                    }
                    actualSeatIndexs.add(actualSeatIndex);
                }
                if (!flag) {
                    indexs.add(actualSeatIndexs);
                }
            }
        }
        if (indexs.isEmpty()) {
            return new TicketBuyStatus(TICKET_BUY_STATUS.NOT_ENOUGH);
        }

        S2SMaxTicketCountProtectInfo protect = s2sSeatStrictProtectMap.get(
                command.getSeatType()
        ).get(key(command.getStartStationNumber(), command.getEndStationNumber()));

        PriorityQueue<SeatIndexSelected> queue = new PriorityQueue<>((sis1, sis2) -> sis2.getWeight() - sis1.getWeight());
        int maxCanBuySeatCount = protect.getMaxCanBuySeatCount();

        for (List<Integer> is : indexs) {
            //计算权重，优先售卖保留的座位，然后再售卖没有预留的座位，不一定精确。
            int weight = 0;
            Map<Integer, SEAT_PROTECT_TYPE> seatIndexMap = new HashMap<>();
            for (Integer index : is) {
                if (protect != null && protect.getSeatIndexBitSet().get(index)) {
                    weight++;
                    seatIndexMap.put(index, SEAT_PROTECT_TYPE.STRICT_PROTECT);
                } else {
                    seatIndexMap.put(index, SEAT_PROTECT_TYPE.GENERAL);
                }
            }
            //判断不是预留票的总是是否超过区间最大可以卖的数量
            int generalSeatCount = (int) seatIndexMap.values().stream().filter(value -> value.equals(SEAT_PROTECT_TYPE.GENERAL)).count();
            int protectSeatCount = (int) seatIndexMap.values().stream().filter(value -> value.equals(SEAT_PROTECT_TYPE.STRICT_PROTECT)).count();
            if ((map.get(SEAT_PROTECT_TYPE.GENERAL).size() + generalSeatCount) <= (maxCanBuySeatCount - protect.getProtectCanBuySeatCount())
                    && (map.get(SEAT_PROTECT_TYPE.STRICT_PROTECT).size() + protectSeatCount) <= protect.getProtectCanBuySeatCount()) {
                queue.add(new SeatIndexSelected(seatIndexMap, weight));
            }
        }
        //权重最高的大部分情况是依次顺序：1.严格匹配预留票 > 2. 严格匹配预留票 + 普通非预留票 >  3.普通非预留票
        // 取出权重度最高的多个座位
        SeatIndexSelected selected = queue.poll();
        if (selected == null) {
            return new TicketBuyStatus(TICKET_BUY_STATUS.NOT_ENOUGH);
        }
        TicketBoughtEvent event = new TicketBoughtEvent();
        event.setUserIds(command.getUserIds());
        event.setStartStationNumber(command.getStartStationNumber());
        event.setEndStationNumber(command.getEndStationNumber());
        event.setSeatIndexs(selected.getSeatIndexs());
        event.setSeatType(command.getSeatType());
        super.applyNewEvent(event);
        return new TicketBuyStatus(TICKET_BUY_STATUS.SUCCEED, command.getUserIds(), selected.getSeatIndexs());
    }

    /**
     * 不指定座位购买
     *
     * @return
     */
    private TicketBuyStatus randomSeatBuyTikcet(TicketBuyCommand command) {
        //判断当前站点到目的站点是否有预留票，如果有预留票优先扣减预留票
        S2SMaxTicketCountProtectInfo s2sMaxSeatCountStrictProtect = s2sSeatStrictProtectMap.get(
                command.getSeatType()
        ).get(key(command.getStartStationNumber(), command.getEndStationNumber()));
        List<Integer> seatIndexs = new ArrayList<>();
        if (s2sMaxSeatCountStrictProtect != null) {
            BitSet bs = new BitSet();
            bs.or(s2sMaxSeatCountStrictProtect.getSeatIndexBitSet());
            int protectCanBuySeatCount = s2sMaxSeatCountStrictProtect.getProtectCanBuySeatCount();
            s2sMaxSeatCountStrictProtect.getS2sProtectSeatIndexBitSet().subMap(
                    fromKey(command.getStartStationNumber()),
                    Boolean.FALSE,
                    toKey(command.getEndStationNumber()),
                    Boolean.TRUE
            ).values().forEach(set -> bs.andNot(set));
            if (bs.cardinality() < protectCanBuySeatCount) {
                BitSet bitSet = new BitSet();
                s2sMaxSeatCountStrictProtect.getS2sProtectSeatIndexBitSet().values().forEach(set ->
                        bitSet.or(set)
                );
                BitSet seatIndexBitSet = (BitSet) s2sMaxSeatCountStrictProtect.getSeatIndexBitSet().clone();
                seatIndexBitSet.andNot(bitSet);
                int number = command.getUserIds().size();
                int seatIndex = 0;
                for (int i = 0; i < number; i++) {
                    seatIndex = seatIndexBitSet.nextSetBit(seatIndex);
                    if (seatIndex < 0) {
                        break;
                    }
                    seatIndexs.add(seatIndex);
                    seatIndex++;
                }
            }
        }

        if (seatIndexs.size() == command.getUserIds().size()) {
            TicketBoughtEvent event = new TicketBoughtEvent();
            event.setUserIds(command.getUserIds());
            event.setStartStationNumber(command.getStartStationNumber());
            event.setEndStationNumber(command.getEndStationNumber());
            event.setSeatIndexs(event.getSeatIndexs());
            event.setSeatType(command.getSeatType());
            super.applyNewEvent(event);
            return new TicketBuyStatus(TICKET_BUY_STATUS.SUCCEED, command.getUserIds(), event.getSeatIndexs());
        }

        BitSet bitSet = new BitSet();
        for (BitSet set : s2sSeatCountMap.get(command.getSeatType()).subMap(
                fromKey(command.getStartStationNumber()),
                Boolean.FALSE,
                toKey(command.getEndStationNumber()),
                Boolean.TRUE
        ).values()) {
            bitSet.or(set);
        }

        s2sSeatStrictProtectMap.get(command.getSeatType()).values().forEach(protect -> {
            protect.getS2sProtectSeatIndexBitSet().subMap(
                    fromKey(command.getStartStationNumber()),
                    Boolean.FALSE,
                    toKey(command.getEndStationNumber()),
                    Boolean.TRUE
            ).values().forEach(set -> {
                bitSet.or(set);
            });
        });

        int number = command.getUserIds().size();
        int seatIndex = 0;
        for (int i = 0; i < number; i++) {
            //返回第一个设置为 false 的位的索引
            seatIndex = bitSet.nextClearBit(seatIndex);
            //如果找不到座位索引或者座位的索引大于座位的总数-1，说明没有票了。
            if (seatIndex < 0 || seatIndex > seatCountMap.get(command.getSeatType()) - 1) {
                return new TicketBuyStatus(TICKET_BUY_STATUS.NOT_ENOUGH);
            }
            seatIndexs.add(seatIndex);
            seatIndex++;
        }

        if (seatIndexs.size() == command.getUserIds().size()) {
            TicketBoughtEvent event = new TicketBoughtEvent();
            event.setUserIds(command.getUserIds());
            event.setStartStationNumber(command.getStartStationNumber());
            event.setEndStationNumber(command.getEndStationNumber());
            Map<Integer, SEAT_PROTECT_TYPE> seatIndexMap = new HashMap<>();
            seatIndexs.forEach(index -> seatIndexMap.put(index, SEAT_PROTECT_TYPE.GENERAL));
            event.setSeatIndexs(seatIndexMap);
            event.setSeatType(command.getSeatType());
            super.applyNewEvent(event);
            return new TicketBuyStatus(TICKET_BUY_STATUS.SUCCEED, command.getUserIds(), seatIndexMap);
        } else {
            return new TicketBuyStatus(TICKET_BUY_STATUS.NOT_ENOUGH);
        }
    }


    /**
     * 购票
     *
     * @param command
     * @return
     */
    public TicketBuyStatus buyTicket(TicketBuyCommand command) {
        // 判断用户是否已购买过车票
        for (Long userId : command.getUserIds()) {
            if (userTicketMap.get(userId) != null) {
                return new TicketBuyStatus(TICKET_BUY_STATUS.BOUGHT);
            }
        }

        //判断当前站点到目的站点是否有预留票，如果有预留票优先扣减预留票
        S2SMaxTicketCountProtectInfo s2sMaxSeatCountStrictProtect = s2sSeatStrictProtectMap.get(
                command.getSeatType()
        ).get(key(command.getStartStationNumber(), command.getEndStationNumber()));
        if (s2sMaxSeatCountStrictProtect != null) {
            Map<SEAT_PROTECT_TYPE, List<UserSeatInfo>> map = calculateS2SSoldTicketCount(
                    command.getStartStationNumber(),
                    command.getEndStationNumber(),
                    command.getSeatType());
            int maxCanBuySeatCount = s2sMaxSeatCountStrictProtect.getMaxCanBuySeatCount();
            int protectCount = map.get(SEAT_PROTECT_TYPE.STRICT_PROTECT).size();
            int generalCount = map.get(SEAT_PROTECT_TYPE.GENERAL).size();
            if ((protectCount + generalCount + command.getUserIds().size()) > maxCanBuySeatCount) {
                return new TicketBuyStatus(TICKET_BUY_STATUS.NOT_ENOUGH);
            }
        }

        if (CollUtil.isNotEmpty(command.getSeatIndexs())) {
            //说明是指定座位的票，因为指定座位的计算逻辑与按顺序购买有一定区别，分开两个逻辑处理
            TicketBuyStatus status = selectSeatBuyTicket(command);
            if (TICKET_BUY_STATUS.SUCCEED.equals(status.getStauts())) {
                return status;
            }
        }
        return randomSeatBuyTikcet(command);
    }

    /**
     * 取消购票
     *
     * @param command
     * @return
     */
    public TICKET_CANCEL_STATUS cancelTicket(TicketCancelCommand command) {
        UserSeatInfo info = userTicketMap.get(command.getUserId());
        if (info == null) {
            return TICKET_CANCEL_STATUS.NOT_EXIST;
        }

        TicketCanceledEvent event = new TicketCanceledEvent();
        event.setUserId(command.getUserId());
        event.setStartStationNumber(command.getStartStationNumber());
        event.setEndStationNumber(command.getEndStationNumber());
        event.setSeatIndex(info.getSeatIndex());
        event.setSeatProtectType(info.getSeatProtectType());
        super.applyNewEvent(event);
        return TICKET_CANCEL_STATUS.SUCCEED;
    }

    @SuppressWarnings("unused")
    private void apply(TicketBoughtEvent event) {
        Map<Integer, SEAT_PROTECT_TYPE> map = event.getSeatIndexs();
        int i = 0;
        for (Map.Entry<Integer, SEAT_PROTECT_TYPE> entry : map.entrySet()) {
            int seatIndex = entry.getKey();
            SEAT_PROTECT_TYPE type = entry.getValue();
            if (type.equals(SEAT_PROTECT_TYPE.STRICT_PROTECT)) {
                S2SMaxTicketCountProtectInfo s2sMaxSeatCountProtect = s2sSeatStrictProtectMap.get(event.getSeatType()).get(key(event.getStartStationNumber(), event.getEndStationNumber()));
                s2sMaxSeatCountProtect.getS2sProtectSeatIndexBitSet().values().forEach(set ->
                        set.set(seatIndex, Boolean.TRUE)
                );
            }

            s2sSeatCountMap.get(event.getSeatType()).subMap(
                    fromKey(event.getStartStationNumber()),
                    Boolean.FALSE,
                    toKey(event.getEndStationNumber()),
                    Boolean.TRUE
            ).forEach((number, set) -> {
                set.set(seatIndex);
            });

            UserSeatInfo trainSeatInfo = new UserSeatInfo();
            trainSeatInfo.setStartStationNumber(event.getStartStationNumber());
            trainSeatInfo.setEndStationNumber(event.getEndStationNumber());
            trainSeatInfo.setSeatIndex(seatIndex);
            trainSeatInfo.setSeatProtectType(type);
            trainSeatInfo.setSeatType(event.getSeatType());
            userTicketMap.put(event.getUserIds().get(i), trainSeatInfo);
            i++;
        }
    }

    @SuppressWarnings("unused")
    private void apply(TrainCreatedEvent event) {
        this.userTicketMap = new HashMap<>();
        this.seatCountMap = new HashMap<>();
        this.trainCarriageMap = new HashMap<>();
        this.seatCountMap.put(SEAT_TYPE.BUSINESS_CLASS, event.getBusinessSeatCount());
        this.seatCountMap.put(SEAT_TYPE.FIRST_CLASS, event.getFirstSeatCount());
        this.seatCountMap.put(SEAT_TYPE.SECOND_CLASS, event.getSecondSeatCount());
        this.seatCountMap.put(SEAT_TYPE.STANDING, event.getStandingCount());

        this.trainCarriageMap.put(SEAT_TYPE.BUSINESS_CLASS, event.getBusinessTrainCarriageList());
        this.trainCarriageMap.put(SEAT_TYPE.FIRST_CLASS, event.getFirstTrainCarriageList());
        this.trainCarriageMap.put(SEAT_TYPE.SECOND_CLASS, event.getSecondTrainCarriageList());
        this.trainCarriageMap.put(SEAT_TYPE.STANDING, event.getStandingTrainCarriageList());

        this.s2sSeatCountMap = new ConcurrentSkipListMap<>();

        this.s2sSeatStrictProtectMap = new HashMap<>();
        this.s2sSeatStrictProtectMap.put(SEAT_TYPE.BUSINESS_CLASS, new HashMap<>());
        this.s2sSeatStrictProtectMap.put(SEAT_TYPE.FIRST_CLASS, new HashMap<>());
        this.s2sSeatStrictProtectMap.put(SEAT_TYPE.SECOND_CLASS, new HashMap<>());
        this.s2sSeatStrictProtectMap.put(SEAT_TYPE.STANDING, new HashMap<>());


        ConcurrentSkipListMap<Integer, BitSet> businessMap = new ConcurrentSkipListMap<>();
        event.getStation2StationBusinessList().forEach(value ->
                businessMap.put(value, new BitSet(event.getBusinessSeatCount()))
        );
        this.s2sSeatCountMap.put(SEAT_TYPE.BUSINESS_CLASS, businessMap);


        ConcurrentSkipListMap<Integer, BitSet> firstMap = new ConcurrentSkipListMap<>();
        event.getStation2StationFirstList().forEach(value ->
                firstMap.put(value, new BitSet(event.getBusinessSeatCount()))
        );
        this.s2sSeatCountMap.put(SEAT_TYPE.FIRST_CLASS, firstMap);


        ConcurrentSkipListMap<Integer, BitSet> secondMap = new ConcurrentSkipListMap<>();
        event.getStation2StationSecondList().forEach(value ->
                secondMap.put(value, new BitSet(event.getBusinessSeatCount()))
        );
        this.s2sSeatCountMap.put(SEAT_TYPE.SECOND_CLASS, secondMap);


        ConcurrentSkipListMap<Integer, BitSet> standingMap = new ConcurrentSkipListMap<>();
        event.getStation2StationStandingList().forEach(value ->
                standingMap.put(value, new BitSet(event.getBusinessSeatCount()))
        );
        this.s2sSeatCountMap.put(SEAT_TYPE.STANDING, standingMap);

    }

    @SuppressWarnings("unused")
    private void apply(TicketProtectCanceledEvent event) {
        s2sSeatStrictProtectMap.get(event.getSeatType()).remove(key(event.getStartStationNumber(), event.getEndStationNumber()));
    }

    @SuppressWarnings("unused")
    private void apply(TicketProtectSucceedEvent event) {
        ConcurrentSkipListMap<Integer, BitSet> s2sProtectSeatIndexBitSet = new ConcurrentSkipListMap<>();
        for (int start = event.getStartStationNumber(); start < event.getEndStationNumber(); start++) {
            s2sProtectSeatIndexBitSet.put(start * AMPLIFICATION_FACTOR + start + 1, new BitSet());
        }
        S2SMaxTicketCountProtectInfo protect = new S2SMaxTicketCountProtectInfo(
                event.getStartStationNumber(),
                event.getEndStationNumber(),
                event.getProtectCanBuyTicketCount(),
                event.getMaxCanBuyTicketCount(),
                BitSet.valueOf(event.getProtectSeatIndex()),
                s2sProtectSeatIndexBitSet
        );
        s2sSeatStrictProtectMap.get(event.getSeatType()).put(key(event.getStartStationNumber(), event.getEndStationNumber()), protect);
    }

    @SuppressWarnings("unused")
    private void apply(TicketCanceledEvent event) {
        UserSeatInfo info = userTicketMap.get(event.getUserId());
        if (info.getSeatProtectType().equals(SEAT_PROTECT_TYPE.STRICT_PROTECT)) {
            s2sSeatStrictProtectMap.get(event.getSeatType()).get(
                    key(event.getStartStationNumber(), event.getEndStationNumber())
            ).getS2sProtectSeatIndexBitSet().subMap(
                    fromKey(event.getStartStationNumber()),
                    Boolean.FALSE,
                    toKey(event.getEndStationNumber()),
                    Boolean.TRUE
            ).values().forEach(set ->
                    set.set(info.getSeatIndex(), Boolean.FALSE)
            );
        }
        s2sSeatCountMap.get(event.getSeatType()).subMap(
                fromKey(event.getStartStationNumber()),
                Boolean.FALSE,
                toKey(event.getEndStationNumber()),
                Boolean.TRUE
        ).values().forEach(set ->
                set.set(info.getSeatIndex(), Boolean.FALSE)
        );
        userTicketMap.remove(event.getUserId());
    }
}

