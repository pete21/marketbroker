package com.piotr.marketbroker.infrastructure.persistence.tickstate

import com.piotr.marketbroker.domain.tick.Tick
import com.piotr.marketbroker.domain.tick.port.TickState
import org.springframework.stereotype.Repository

@Repository
class InMemoryTickStateRepository : TickState {

    private val keysMap: MutableMap<Int, Tick> = HashMap()

    //    public static Tick emptyTick = new Tick(0, 0, 0, 0, 0, "");
    override fun put(quoteId: Int, tick: Tick) {
        keysMap[quoteId] = tick
    }

    override fun get(quoteId: Int): Tick? {
        return keysMap.getOrDefault(quoteId, null)
    }

    override fun remove(quoteId: Int) {
        keysMap.remove(quoteId)
    }

    override fun getAll(): List<Tick> {
        return keysMap.values.toList()
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
