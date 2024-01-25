package com.piotr.marketbroker.infrastructure.persistence.keys

import com.piotr.marketbroker.infrastructure.persistence.tick.Tick
import org.springframework.stereotype.Repository

@Repository
class KeysRepository {

    private val keysMap: MutableMap<Int, Tick?> = HashMap<Int, Tick?>()

    //    public static Tick emptyTick = new Tick(0, 0, 0, 0, 0, "");
    fun put(quoteId: Int, tick: Tick?) {
        keysMap[quoteId] = tick
    }

    fun get(quoteId: Int): Tick? {
        return keysMap.getOrDefault(quoteId, null)
    }

    fun remove(quoteId: Int) {
        keysMap.remove(quoteId)

    }
}

/*
    @Getter
    @Setter
    @AllArgsConstructor
    class Key {
        private float bid;
        private float ask;
        private String key;
        private long timestamp;

        public Key(Tick tick) {
            bid = tick.getBid();
            ask = tick.getAsk();
            key = tick.getKey();
            timestamp = tick.getLongtime();
        }
    }
*/
