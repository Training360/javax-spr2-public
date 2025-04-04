package empapp;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

@Slf4j
public class CacheEventLogger implements CacheEventListener<Object, Object> {

    @Override
    public void onEvent(CacheEvent<?, ?> event) {
        log.info("Key: {}, type: {}, old: {}, new: {}",
                event.getKey(),
                event.getType(),
                event.getOldValue(),
                event.getNewValue()
        );
    }
}
