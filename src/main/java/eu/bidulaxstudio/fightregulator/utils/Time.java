package eu.bidulaxstudio.fightregulator.utils;

import java.sql.Timestamp;
import java.time.Instant;

public class Time {
    public static long getTime() {
        return Timestamp.from(Instant.now()).getTime();
    }

}
