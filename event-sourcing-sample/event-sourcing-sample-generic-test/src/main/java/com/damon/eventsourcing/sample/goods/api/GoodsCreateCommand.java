package com.damon.eventsourcing.sample.goods.api;

import com.damon.eventsourcing.domain.Command;

public class GoodsCreateCommand extends Command {

    /**
     *
     */
    private static final long serialVersionUID = -452309907057579164L;
    private String name;
    private int number;

    public GoodsCreateCommand(long aggregateId, String name, int number) {
        super(aggregateId);
        this.name = name;
        this.number = number;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}
