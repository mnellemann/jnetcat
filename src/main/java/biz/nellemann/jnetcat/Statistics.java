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

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Statistics {

    private final int MAX_TICKS_AVG = 300;
    private final int LOG_AVG_MODULO = 30;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    private long bytesTransferred, bytesTransferredTotal = 0;
    private long bytesPerSec;
    private int tickIterations = 0;

    private final long[] bytesPerSecAvgTmp = new long[MAX_TICKS_AVG];
    private final long[] packetsPerSecAvgTmp = new long[MAX_TICKS_AVG];

    private Instant timestamp1 = Instant.now();


    public void reset() {
        timestamp1 = Instant.now();
    }


    public void tick() {

        Instant timestamp2 = Instant.now();
        if(Duration.between(timestamp1, timestamp2).toMillis() >= 1000) {

            // Because we do this every second ...
            bytesPerSec = bytesTransferred;
            bytesPerSecAvgTmp[tickIterations] = bytesTransferred;

            timestamp1 = timestamp2;
            printStatus();

            bytesTransferred = 0;

            if(++tickIterations >= MAX_TICKS_AVG) {
                tickIterations = 0;
            }

            if(tickIterations % LOG_AVG_MODULO == 0) {
                printAverage();
            }
        }

    }


    public void printStatus() {
        System.err.printf("%-19s -  Status: %14d B/s %12d KB/s %8d MB/s\n", formatter.format(Instant.now()), bytesPerSec, bytesPerSec/1_000, bytesPerSec/1_000_000);
    }


    public void printSummary() {
        System.err.println();
        System.err.printf("%-19s - Summary: %14d B   %12d KB   %8d MB   %8.2f GB\n", formatter.format(Instant.now()), bytesTransferredTotal, bytesTransferredTotal /1_000, bytesTransferredTotal /1_000_000, (double) bytesTransferredTotal/1_000_000_000);
    }


    public void printAverage() {
        long bytesPerSecAvg = getAverage(bytesPerSecAvgTmp, bytesTransferred);
        System.err.printf("%-19s - Average: %14d B/s %12d KB/s %8d MB/s %8.2f GB/s\n", formatter.format(Instant.now()), bytesPerSecAvg, bytesPerSecAvg /1_000, bytesPerSecAvg /1_000_000, (double) bytesPerSecAvg /1_000_000_000);
    }


   public void transferBytes(long bytes) {
        bytesTransferred += bytes;
        bytesTransferredTotal += bytes;
    }


    private long getAverage(long[] array, long fallback) {
        long avg = getAverage(array);
        if(avg < 1) {
            return fallback;
        }
        return avg;
    }


    private long getAverage(long[] array) {
        if(array.length <= 1) {
            return 0;
        }
        int len = 0;
        long sum = 0;
        for (long l : array) {
            if(l > 0) {
                sum += l;
                len++;
            }
        }

        long avg = 0;
        if(len > 0) {
            avg = sum / len;
        }
        return avg;
    }

}
