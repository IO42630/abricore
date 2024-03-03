package com.olexyn.abricore.util;

import lombok.Synchronized;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public final class LockKeeper {

    private static final Map<UUID, Lock> LOCKS = new HashMap<>();

    @Synchronized
    public static Lock getLock(UUID uuid) {
        if (LOCKS.containsKey(uuid)) {
            return LOCKS.get(uuid);
        }
        var newLock = new Lock();
        LOCKS.put(uuid, newLock);
        return newLock;
    }

}
