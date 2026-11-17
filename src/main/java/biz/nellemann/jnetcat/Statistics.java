/*
   Copyright 2023 mark.nellemann@gmail.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package biz.nellemann.jnetcat;

import java.io.PrintStream;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Statistics implements AutoCloseable {

    private static final int MAX_TICKS_AVG = 300;
    private static final int LOG_AVG_MODULO = 30;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    private final PrintStream printStream;
    private final Clock clock;

    private final long[] bytesPerSecAvgTmp = new long[MAX_TICKS_AVG];

    private ScheduledExecutorService scheduler;

    private long bytesTransferred = 0;
    private long bytesTransferredTotal = 0;
    private long bytesPerSec = 0;
    private int tickIterations = 0;
    private int samples = 0;
    private Instant timestamp1;

    public Statistics() {
        this(System.err);
    }

    Statistics(PrintStream printStream) {
        this(printStream, Clock.systemDefaultZone());
    }

    Statistics(PrintStream printStream, Clock clock) {
        this.printStream = printStream;
        this.clock = clock;
        this.timestamp1 = clock.instant();
    }


    /**
     * Begin scheduling statistics output once per second.
     */
    public void start() {
        synchronized (this) {
            timestamp1 = clock.instant();
            bytesTransferred = 0;
        }

        scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "jnetcat-statistics");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(this::tick, 1, 1, TimeUnit.SECONDS);
    }


    @Override
    public void close() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
        printSummary();
    }


    /**
     * Compute and print statistics for the interval since the last tick.
     * Called once per second by the scheduler started in {@link #start()},
     * or directly by tests.
     */
    synchronized void tick() {

        Instant timestamp2 = clock.instant();
        long millis = Duration.between(timestamp1, timestamp2).toMillis();
        if (millis < 1) {
            return;
        }

        bytesPerSec = bytesTransferred * 1000 / millis;
        bytesPerSecAvgTmp[tickIterations] = bytesPerSec;
        if (samples < MAX_TICKS_AVG) {
            samples++;
        }

        timestamp1 = timestamp2;
        printStatus();
        bytesTransferred = 0;

        tickIterations++;
        if (tickIterations % LOG_AVG_MODULO == 0) {
            printAverage();
        }
        if (tickIterations >= MAX_TICKS_AVG) {
            // Reset the rolling window, so stale samples are not
            // mixed into future averages.
            tickIterations = 0;
            samples = 0;
            Arrays.fill(bytesPerSecAvgTmp, 0L);
        }
    }


    public synchronized void transferBytes(long bytes) {
        bytesTransferred += bytes;
        bytesTransferredTotal += bytes;
    }


    public synchronized long getBytesTransferredTotal() {
        return bytesTransferredTotal;
    }

    public synchronized long getBytesPerSec() {
        return bytesPerSec;
    }

    public synchronized long getAverageBytesPerSec() {
        if (samples == 0) {
            return 0;
        }
        long sum = 0;
        for (int i = 0; i < samples; i++) {
            sum += bytesPerSecAvgTmp[i];
        }
        return sum / samples;
    }


    private void printStatus() {
        printStream.printf("%-19s -  Status: %14d B/s %12d KB/s %8d MB/s%n", formatter.format(clock.instant()), bytesPerSec, bytesPerSec /1_000, bytesPerSec /1_000_000);
    }


    public synchronized void printAverage() {
        long bytesPerSecAvg = getAverageBytesPerSec();
        printStream.printf("%-19s - Average: %14d B/s %12d KB/s %8d MB/s %8.2f GB/s%n", formatter.format(clock.instant()), bytesPerSecAvg, bytesPerSecAvg /1_000, bytesPerSecAvg /1_000_000, (double) bytesPerSecAvg /1_000_000_000);
    }


    public synchronized void printSummary() {
        printStream.println();
        printStream.printf("%-19s - Summary: %14d B   %12d KB   %8d MB   %8.2f GB%n", formatter.format(clock.instant()), bytesTransferredTotal, bytesTransferredTotal /1_000, bytesTransferredTotal /1_000_000, (double) bytesTransferredTotal /1_000_000_000);
    }

}
