package com.damon.eventsourcing.sample.train.aggregate.value_object;

import java.util.BitSet;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 站点到站点座位的限制
 */
public class S2SMaxTicketCountProtectInfo {
    /**
     * 开始站点
     */
    private Integer fromStation;
    /**
     * 结束站点
     */
    private Integer toStation;
    /**
     * 站在间预留的座位索引，从0开始
     */
    private BitSet seatIndexBitSet;
    /**
     * 站点间可以购买座位的保留数量
     */
    private Integer protectCanBuySeatCount;
    /**
     * 站点间可以购买座位的最大数量
     */
    private Integer maxCanBuySeatCount;
    /**
     * 站到站点间预留的座位索引  例如：key:10002  value: 00001111 表示站点1至2预留索引4-7 索引的座位（总计4个座位）
     */
    private ConcurrentSkipListMap<Integer, BitSet> s2sProtectSeatIndexBitSet;

    public S2SMaxTicketCountProtectInfo(Integer fromStation,
                                        Integer toStation,
                                        Integer protectCanBuySeat,
                                        Integer maxCanBuySeatCount,
                                        BitSet seatIndexBitSet,
                                        ConcurrentSkipListMap<Integer, BitSet> s2sSeatIndexBitSet) {
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.maxCanBuySeatCount = maxCanBuySeatCount;
        this.protectCanBuySeatCount = protectCanBuySeat;
        this.seatIndexBitSet = seatIndexBitSet;
        this.s2sProtectSeatIndexBitSet = s2sSeatIndexBitSet;
    }

    public ConcurrentSkipListMap<Integer, BitSet> getS2sProtectSeatIndexBitSet() {
        return s2sProtectSeatIndexBitSet;
    }

    public void setS2sProtectSeatIndexBitSet(ConcurrentSkipListMap<Integer, BitSet> s2sProtectSeatIndexBitSet) {
        this.s2sProtectSeatIndexBitSet = s2sProtectSeatIndexBitSet;
    }

    public Integer getMaxCanBuySeatCount() {
        return maxCanBuySeatCount;
    }

    public void setMaxCanBuySeatCount(Integer maxCanBuySeatCount) {
        this.maxCanBuySeatCount = maxCanBuySeatCount;
    }

    public Integer getFromStation() {
        return fromStation;
    }

    public void setFromStation(Integer fromStation) {
        this.fromStation = fromStation;
    }

    public Integer getToStation() {
        return toStation;
    }

    public void setToStation(Integer toStation) {
        this.toStation = toStation;
    }

    public BitSet getSeatIndexBitSet() {
        return seatIndexBitSet;
    }

    public void setSeatIndexBitSet(BitSet seatIndexBitSet) {
        this.seatIndexBitSet = seatIndexBitSet;
    }

    public Integer getProtectCanBuySeatCount() {
        return protectCanBuySeatCount;
    }

    public void setProtectCanBuySeatCount(Integer protectCanBuySeatCount) {
        this.protectCanBuySeatCount = protectCanBuySeatCount;
    }

}