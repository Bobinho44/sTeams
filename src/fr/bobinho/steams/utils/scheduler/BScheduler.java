package fr.bobinho.steams.utils.scheduler;

import fr.bobinho.steams.sTeamsCore;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BScheduler {

    /**
     * Plugin instance
     */
    private static final sTeamsCore PLUGIN = sTeamsCore.getInstance();

    /**
     * Scheduler types
     */
    public enum Type {
        SYNC,
        ASYNC
    }

    /**
     * Fields
     */
    private final Type type;
    private int delay;
    private TimeUnit delayType;
    private int repeatingDelay;
    private TimeUnit repeatingDelayType;
    private Runnable cachedRunnable;
    private int bukkitTaskId = -1;
    private boolean shouldWait = false;

    /**
     * Creates scheduler
     *
     * @param type the type
     */
    public BScheduler(@Nonnull Type type) {
        Validate.notNull(type, "type is null");

        //Root construction
        this.type = type;
    }

    /**
     * Creates sync scheduler builder.
     *
     * @return the sync scheduler builder.
     */
    @Nonnull
    public static BScheduler syncScheduler() {
        return new BScheduler(BScheduler.Type.SYNC);
    }

    /**
     * Creates async scheduler builder
     *
     * @return the async scheduler builder
     */
    @Nonnull
    public static BScheduler asyncScheduler() {
        return new BScheduler(BScheduler.Type.ASYNC);
    }

    /**
     * Runs scheduler after declared time
     *
     * @param delay the scheduler after delay
     * @return the scheduler builder
     */
    @Nonnull
    public BScheduler after(int delay) {
        this.delay = delay * 50;
        this.delayType = TimeUnit.MILLISECONDS;
        return this;
    }

    /**
     * Runs scheduler after declared time
     *
     * @param delay     the scheduler after delay
     * @param delayType the scheduler after delay type
     * @return the scheduler builder
     */
    @Nonnull
    public BScheduler after(int delay, @Nonnull TimeUnit delayType) {
        Validate.notNull(delayType, "delayType is null");

        this.delay = delay;
        this.delayType = delayType;
        return this;
    }

    /**
     * Runs scheduler every declared time
     *
     * @param repeatingDelay the scheduler repeating time
     * @return the scheduler builder
     */
    @Nonnull
    public BScheduler every(int repeatingDelay) {
        this.repeatingDelay = repeatingDelay * 50;
        this.repeatingDelayType = TimeUnit.MILLISECONDS;
        return this;
    }

    /**
     * Runs scheduler every declared time
     *
     * @param repeatingDelay     the scheduler repeating time
     * @param repeatingDelayType the scheduler repeating time type
     * @return the scheduler builder
     */
    @Nonnull
    public BScheduler every(int repeatingDelay, @Nonnull TimeUnit repeatingDelayType) {
        Validate.notNull(repeatingDelayType, "repeatingDelayType is null");

        this.repeatingDelay = repeatingDelay;
        this.repeatingDelayType = repeatingDelayType;
        return this;
    }

    /**
     * Gets cached runnable
     *
     * @return the runnable
     */
    public Runnable getCachedRunnable() {
        return cachedRunnable;
    }

    /**
     * Sets cached runnable
     *
     * @param cachedRunnable the runnable
     * @return the scheduler builder
     */
    @Nonnull
    public BScheduler setCachedRunnable(@Nonnull Runnable cachedRunnable) {
        Validate.notNull(cachedRunnable, "cachedRunnable is null!");

        this.cachedRunnable = cachedRunnable;
        return this;
    }

    /**
     * Waits task to complete
     *
     * @return the scheduler builder
     */
    @Nonnull
    public BScheduler block() {
        shouldWait = true;
        return this;
    }

    /**
     * Runs cached scheduler
     *
     * @return the bukkit task id
     */
    public int runCached() {
        return run(cachedRunnable);
    }

    /**
     * If there is an ongoing task, it will stop it
     */
    public void stop() {
        if (bukkitTaskId == -1) {
            return;
        }
        Bukkit.getScheduler().cancelTask(bukkitTaskId);
    }

    /**
     * Runs configured scheduler
     *
     * @param runnable the runnable
     * @return the bukkit task id.
     */
    public synchronized int run(@Nonnull Runnable runnable) throws IllegalArgumentException {
        Validate.notNull(runnable, "runnable is null");

        long delay = delayType == null ? 0 : Math.max(delayType.toMillis(this.delay) / 50, 0);
        long repeating_delay = repeatingDelayType == null ? 0 : Math.max(repeatingDelayType.toMillis(repeatingDelay) / 50, 0);

        if (type == Type.SYNC) {
            if (repeating_delay != 0)
                bukkitTaskId = Bukkit.getScheduler().runTaskTimer(PLUGIN, runnable, delay, repeating_delay).getTaskId();
            else if (delay != 0)
                bukkitTaskId = Bukkit.getScheduler().runTaskLater(PLUGIN, runnable, delay).getTaskId();
            else
                bukkitTaskId = Bukkit.getScheduler().runTask(PLUGIN, runnable).getTaskId();
        } else {
            if (repeating_delay != 0)
                bukkitTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(PLUGIN, runnable, delay, repeating_delay).getTaskId();
            else if (delay != 0)
                bukkitTaskId = Bukkit.getScheduler().runTaskLaterAsynchronously(PLUGIN, runnable, delay).getTaskId();
            else
                bukkitTaskId = Bukkit.getScheduler().runTaskAsynchronously(PLUGIN, runnable).getTaskId();
        }

        //Waits task to complete
        if (shouldWait) {
            while (true) {
                if (Bukkit.getScheduler().isCurrentlyRunning(bukkitTaskId) || Bukkit.getScheduler().isQueued(bukkitTaskId))
                    continue;
                break;
            }
        }

        return bukkitTaskId;
    }

    /**
     * Runs configured scheduler
     *
     * @param task the bukkit task
     */
    public synchronized void run(@Nonnull Consumer<BukkitTask> task) throws IllegalArgumentException {
        Validate.notNull(task, "task is null");

        long delay = delayType == null ? 0 : Math.max(delayType.toMillis(this.delay) / 50, 0);
        long repeating_delay = repeatingDelayType == null ? 0 : Math.max(repeatingDelayType.toMillis(this.repeatingDelay) / 50, 0);

        if (type == Type.SYNC) {
            if (repeating_delay != 0)
                Bukkit.getScheduler().runTaskTimer(PLUGIN, task, delay, repeating_delay);
            else if (delay != 0)
                Bukkit.getScheduler().runTaskLater(PLUGIN, task, delay);
            else
                Bukkit.getScheduler().runTask(PLUGIN, task);
        } else {
            if (repeating_delay != 0)
                Bukkit.getScheduler().runTaskTimerAsynchronously(PLUGIN, task, delay, repeating_delay);
            else if (delay != 0)
                Bukkit.getScheduler().runTaskLaterAsynchronously(PLUGIN, task, delay);
            else
                Bukkit.getScheduler().runTaskAsynchronously(PLUGIN, task);
        }
    }

}
