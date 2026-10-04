package com.damon.eventsourcing.domain;

import java.io.Serializable;

/**
 * 子类需要实现无参构造方法，不然bean复制时报错
 *
 * @author xianping_lu
 */
public interface Entity extends Serializable {
    Long getId();

    void setId(Long id);

}