package com.damon.eventsourcing.sample.red_packet.domain.aggregate;

import com.damon.eventsourcing.domain.AggregateRoot;
import com.damon.eventsourcing.sample.red_packet.api.command.RedPacketCreateCommand;
import com.damon.eventsourcing.sample.red_packet.api.command.RedPacketGrabCommand;
import com.damon.eventsourcing.sample.red_packet.api.event.RedPacketCreatedEvent;
import com.damon.eventsourcing.sample.red_packet.api.event.RedPacketGrabSucceedEvent;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

/**
 * 微信红包聚合根
 *
 * @author xianpinglu
 */
@Data
public class WeixinRedPacket extends AggregateRoot {

    /**
     *
     */
    private static final long serialVersionUID = -1820552753606925363L;
    /**
     * key 用户id value 抢到的金额
     */
    private Map<Long, BigDecimal> map;
    /**
     * 待抢红包堆栈
     */
    private Stack<BigDecimal> redpacketStack;
    /**
     * 红包发起人
     */
    private Long sponsorId;
    /**
     * 红包金额
     **/
    private BigDecimal money;
    /**
     * 红包个数
     */
    private BigDecimal number;

    private BigDecimal minMoney;

    public WeixinRedPacket() {

    }

    /**
     * 创建红包
     *
     * @param command
     */
    public WeixinRedPacket(RedPacketCreateCommand command) {
        super(command.getAggregateId());
        Stack<BigDecimal> stack = generateRedPacket(command.getMoney(), command.getMinMoney(), command.getNumber());
        RedPacketCreatedEvent event = new RedPacketCreatedEvent(stack);
        //event.setAggregateId(command.getAggregateId());
        event.setSponsorId(command.getSponsorId());
        event.setMoney(command.getMoney());
        event.setNumber(command.getNumber());
        event.setMinMoney(command.getMinMoney());
        super.applyNewEvent(event);
    }

    /**
     * 抢红包
     *
     * @param command
     * @return
     */
    public int grabRedPackage(RedPacketGrabCommand command) {
        // 红包已抢完
        if (redpacketStack.size() == 0) {
            return 0;
        }
        // 用户已抢过红包
        if (map.get(command.getUserId()) != null) {
            return -1;
        }
        // 抢红包成功
        super.applyNewEvent(new RedPacketGrabSucceedEvent(redpacketStack.peek(), command.getUserId()));
        return 1;
    }

    @SuppressWarnings("unused")
    private void apply(RedPacketCreatedEvent event) {
        this.redpacketStack = event.getRedpacketStack();
        this.sponsorId = event.getSponsorId();
        this.map = new HashMap<>(event.getNumber().intValue());
        this.number = event.getNumber();
        this.money = event.getMoney();
        this.minMoney = event.getMinMoney();
    }

    @SuppressWarnings("unused")
    private void apply(RedPacketGrabSucceedEvent event) {
        map.put(event.getUserId(), redpacketStack.pop());
    }

    /**
     * 随件根据指定金额创建指定个数的红包列表
     *
     * @param amount
     * @param min
     * @param num
     * @return
     */
    private Stack<BigDecimal> generateRedPacket(BigDecimal amount, BigDecimal min, BigDecimal num) {
        Stack<BigDecimal> stack = new Stack<>();
        BigDecimal remain = amount.subtract(min.multiply(num));
        final Random random = new Random();
        final BigDecimal hundred = new BigDecimal("100");
        final BigDecimal two = new BigDecimal("2");
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal redpeck;
        for (int i = 0; i < num.intValue(); i++) {
            final int nextInt = random.nextInt(100);
            if (i == num.intValue() - 1) {
                redpeck = remain;
            } else {
                redpeck = new BigDecimal(nextInt).multiply(remain.multiply(two).divide(num.subtract(new BigDecimal(i)), 2, RoundingMode.CEILING)).divide(hundred, 2, RoundingMode.FLOOR);
            }
            if (remain.compareTo(redpeck) > 0) {
                remain = remain.subtract(redpeck);
            } else {
                remain = BigDecimal.ZERO;
            }
            sum = sum.add(min.add(redpeck));
            stack.add(min.add(redpeck));
        }
        if (amount.compareTo(sum) != 0) {
            throw new IllegalArgumentException("红包累计额度不等于红包总额");
        }
        return stack;
    }

}
