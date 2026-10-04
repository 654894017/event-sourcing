package com.damon.eventsourcing.config;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.damon.eventsourcing.cache.IAggregateCache;
import com.damon.eventsourcing.domain.Event;
import com.damon.eventsourcing.event.EventCommittingService;
import com.damon.eventsourcing.snapshot.IAggregateSnapshootProcessor;
import com.damon.eventsourcing.store.IEventStore;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Data
@Slf4j
public class EventSourcingConfig {

    private final static String SVUID = "serialVersionUID";

    static {
        // 检测event是否拥有无参构造方法、属性是否具备set get方法
        eventEntityStandardInspect();
    }

    private IAggregateCache aggregateCache;
    private IEventStore eventStore;
    private IAggregateSnapshootProcessor aggregateSnapshootService;
    private EventCommittingService eventCommittingService;
    private AggregateSlotLock aggregateSlotLock;

    private static boolean isValidField(String fieldName, Set<String> methodNames) {
        if (fieldName.equals(SVUID)) {
            return true;
        }
        return methodNames.contains("GET" + fieldName.toUpperCase()) && methodNames.contains("SET" + fieldName.toUpperCase());
    }

    private static void eventEntityStandardInspect() {
        Set<Class<?>> classSet = ClassUtil.scanPackageBySuper(StrUtil.EMPTY, Event.class);
        List<String> errors = new ArrayList<>();
        for (Class<?> cla : classSet) {
            try {
                cla.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                String message = String.format("class: %s, need to provide a parameterless constructor.\n", cla.getTypeName());
                errors.add(message);
            }
            Field[] fields = cla.getDeclaredFields();
            Set<String> methodNames = Arrays.stream(cla.getMethods())
                    .map(method -> method.getName().toUpperCase())
                    .collect(Collectors.toSet());
            for (Field field : fields) {
                if (!isValidField(field.getName(), methodNames)) {
                    String message = String.format("class: %s, field : %s, missing get set method.\n", cla.getTypeName(), field.getName());
                    errors.add(message);
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }
    }
}
