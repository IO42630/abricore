package com.olexyn.abricore;

import com.olexyn.abricore.navi.mwatch.MWatch;
import com.olexyn.abricore.navi.mwatch.MWatchable;
import com.olexyn.abricore.navi.sq.SqSession;
import com.olexyn.abricore.navi.tw.TwSession;
import com.olexyn.abricore.util.log.LogU;
import lombok.experimental.UtilityClass;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@UtilityClass
public final class MainUtil {

    public static void logout(ConfigurableApplicationContext ctx) {
        LogU.infoPlain("LOGOUT the Navigators.");

        List<Class<? extends MWatchable>> navs = List.of(SqSession.class, TwSession.class);
        for (var nav : navs) {
            if (MWatch.isAlive(nav)) { MWatch.setDead(nav); }
        }
    }

    public static <T> void sleepWhile(T value, Predicate<T> predicate) {
        while (predicate.test(value)) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                LogU.infoPlain("INTERRUPTED");
            }

        }
    }

    public static boolean contains(Collection<String> set, String... what) {
        for (var o : what) {
            if (set.contains(o)) {
                return true;
            }
        }
        return false;
    }

}
