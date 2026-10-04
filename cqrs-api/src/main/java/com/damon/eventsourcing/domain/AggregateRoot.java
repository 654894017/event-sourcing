package com.damon.eventsourcing.domain;

import com.damon.eventsourcing.exception.EventApplyException;
import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;


public abstract class AggregateRoot implements Serializable {
    private static final long serialVersionUID = 1750836984371267776L;
    /**
     * 版本初始值，全新聚合根版本为0
     */
    private static final int INIT_VERSION = 0;
    // 聚合唯一标识
    private Long id;
    // 当前聚合版本号，每一条事件+1
    private int version = INIT_VERSION;
    // 单次未提交待持久化事件（仅支持单事件）
    private Event uncommittedEvent;
    // 聚合最后变更时间戳
    private Long lastModifiedTime;
    // 上一次生成快照时间戳
    private Long lastSnapshotTime;

    public AggregateRoot() {

    }

    public AggregateRoot(Long id) {
        Preconditions.checkNotNull(id, "聚合根ID不能为空");
        this.id = id;
        long now = System.currentTimeMillis();
        this.lastModifiedTime = now;
        this.lastSnapshotTime = now;
    }

    public final Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public final int getVersion() {
        return version;
    }

    public final void setVersion(int baseVersion) {
        Preconditions.checkArgument(baseVersion >= INIT_VERSION, "聚合版本不能小于0");
        this.version = baseVersion;
    }

    public Long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Long getLastSnapshotTime() {
        return lastSnapshotTime;
    }

    /**
     * 刷新快照生成时间戳，拉链快照生成时调用
     */
    public void refreshLastSnapTime() {
        this.lastSnapshotTime = System.currentTimeMillis();
    }

    /**
     * 是否为全新聚合（无任何事件/快照）
     */
    public boolean isNew() {
        return version == INIT_VERSION;
    }


    /**
     * 获取待持久化未提交事件
     */
    public Event getUncommittedEvent() {
        return uncommittedEvent;
    }

    /**
     * 清空未提交事件，并同步更新聚合版本（事件持久化成功后调用）
     */
    public void acceptChanges() {
        if (uncommittedEvent == null) {
            return;
        }
        setVersion(uncommittedEvent.getVersion());
        this.uncommittedEvent = null;
    }

    /**
     * 追加未提交事件（内部私有，仅新建业务事件使用）
     */
    private void appendUncommittedEvent(Event event) {
        Preconditions.checkNotNull(event, "待提交事件不能为空");
        this.uncommittedEvent = event;
    }


    /**
     * 业务聚合产生新事件，执行事件更新聚合状态并暂存未提交事件
     *
     * @param event 业务领域事件
     */
    protected void applyNewEvent(Event event) {
        Preconditions.checkNotNull(event, "业务事件不能为空");
        // 执行事件业务逻辑修改聚合
        applyEvent(event);
        // 事件版本 = 当前版本+1
        int nextVersion = getVersion() + 1;
        event.setVersion(nextVersion);
        appendUncommittedEvent(event);
        // 更新聚合变更时间
        this.lastModifiedTime = System.currentTimeMillis();
    }


    public void replayEvents(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        events.forEach(event -> {
            verifyEvent(event);
            applyEvent(event);
            setVersion(event.getVersion());
        });
        long now = System.currentTimeMillis();
        this.lastModifiedTime = now;
        this.lastSnapshotTime = now;
    }


    /**
     * 校验事件版本连续，防止事件乱序/缺失
     */
    private void verifyEvent(Event event) {
        int expectVersion = getVersion() + 1;
        if (event.getVersion() != expectVersion) {
            throw new IllegalStateException(
                    String.format("事件版本不连续，当前聚合类型=%s，聚合ID=%d，当前版本=%d，事件版本=%d，预期版本=%d",
                            this.getClass().getSimpleName(),
                            this.getId(),
                            getVersion(),
                            event.getVersion(),
                            expectVersion)
            );
        }
    }


    private void applyEvent(Event event) {
        try {
            Method method = this.getClass().getDeclaredMethod("apply", event.getClass());
            method.setAccessible(true);
            method.invoke(this, event);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            String className = this.getClass().getSimpleName();
            String eventClassName = event.getClass().getSimpleName();
            String errMsg = String.format("聚合【%s】未实现对应事件处理方法：private void apply(%s event)", className, eventClassName);
            throw new EventApplyException(errMsg, e);
        } catch (Exception e) {
            throw new EventApplyException(e);
        }

    }

    /**
     * 判断是否到达快照生成周期，达到则可生成拉链快照
     *
     * @param snapshotCycle 快照周期 单位：秒
     * @return true=满足快照生成条件
     */
    public boolean reachSnapshotCycle(Long snapshotCycle) {
        if (snapshotCycle <= 0) {
            return false;
        }
        // 时间差转为秒
        long diffSecond = (this.lastModifiedTime - this.lastSnapshotTime) / 1000;
        return diffSecond >= snapshotCycle;
    }
}