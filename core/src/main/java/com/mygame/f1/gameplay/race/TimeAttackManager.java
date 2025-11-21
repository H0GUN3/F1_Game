package com.mygame.f1.gameplay.race;

public class TimeAttackManager {
    private long lapStartNanos;
    private long bestLapMs = -1;
    private int lapCount = 0;
    private long lastFinishTimeNanos = 0;
    private static final long COOLDOWN_NANOS = 10_000_000_000L; // 10s debounce
    private long pausedStartNanos = 0L;
    private long pausedAccumNanos = 0L;
    private boolean paused = false;

    public void start() {
        lapStartNanos = System.nanoTime();
        bestLapMs = -1;
        lapCount = 0;
        lastFinishTimeNanos = 0;
        pausedStartNanos = 0L;
        pausedAccumNanos = 0L;
        paused = false;
    }

    public int getLapCount() {
        return lapCount;
    }

    public long getBestLapMs() {
        return bestLapMs;
    }

    public long getCurrentLapMs() {
        long now = System.nanoTime();
        long effectiveNow = paused ? pausedStartNanos : now;
        return (effectiveNow - lapStartNanos - pausedAccumNanos) / 1_000_000L;
    }

    /**
     * Call when finish line is crossed in the correct direction.
     * Returns the completed lap time in ms, or -1 if ignored (cooldown).
     */
    public long onFinishCrossed() {
        long now = System.nanoTime();
        if (now - lastFinishTimeNanos < COOLDOWN_NANOS) return -1;
        lastFinishTimeNanos = now;
        long lapMs = (now - lapStartNanos - pausedAccumNanos) / 1_000_000L;
        lapStartNanos = now; // start next lap
        pausedAccumNanos = 0L;
        paused = false;
        pausedStartNanos = 0L;
        lapCount++;
        if (bestLapMs < 0 || lapMs < bestLapMs) bestLapMs = lapMs;
        return lapMs;
    }

    public void pause() {
        if (!paused) {
            paused = true;
            pausedStartNanos = System.nanoTime();
        }
    }

    public void resume() {
        if (paused) {
            paused = false;
            long now = System.nanoTime();
            if (pausedStartNanos != 0L) {
                pausedAccumNanos += (now - pausedStartNanos);
                pausedStartNanos = 0L;
            }
        }
    }
}
