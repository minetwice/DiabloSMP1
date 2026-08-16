package com.diablosmp.plugin.ability;

import com.diablosmp.plugin.DiabloSMP;
import com.diablosmp.plugin.model.DiabloStoneType;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DiabloAbility {
    protected final DiabloSMP plugin;
    protected final DiabloStoneType stoneType;
    protected final Set<BukkitTask> activeTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());
    protected final Set<Display> spawnedDisplays = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public DiabloAbility(DiabloSMP plugin, DiabloStoneType stoneType) {
        this.plugin = plugin;
        this.stoneType = stoneType;
    }

    public DiabloStoneType getStoneType() {
        return stoneType;
    }

    public abstract boolean cast(Player player);

    protected void scheduleTask(Runnable runnable, long delayTicks) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                activeTasks.remove(this);
                runnable.run();
            }
        }.runTaskLater(plugin, delayTicks);
        activeTasks.add(task);
    }

    protected void scheduleRepeatingTask(Runnable runnable, long delayTicks, long periodTicks, long totalDurationTicks) {
        final long endTick = System.currentTimeMillis() + (totalDurationTicks * 50);
        BukkitTask[] taskRef = new BukkitTask[1];
        taskRef[0] = new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() >= endTick) {
                    activeTasks.remove(taskRef[0]);
                    cancel();
                    return;
                }
                runnable.run();
            }
        }.runTaskTimer(plugin, delayTicks, periodTicks);
        activeTasks.add(taskRef[0]);
    }

    protected void trackDisplay(Display display) {
        if (display != null) {
            spawnedDisplays.add(display);
        }
    }

    public void cleanup() {
        for (BukkitTask task : activeTasks) {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }
        activeTasks.clear();

        for (Display display : spawnedDisplays) {
            if (display != null && display.isValid()) {
                display.remove();
            }
        }
        spawnedDisplays.clear();
    }
}
