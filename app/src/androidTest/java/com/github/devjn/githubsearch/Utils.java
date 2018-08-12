package com.github.devjn.githubsearch;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by @author Jahongir on 11-Jul-18
 * devjn@jn-arts.com
 * Utils
 */
public class Utils {

    public static Executor immediateExecutor() {
        return command -> command.run();
    }

    public static ExecutorService immediateExecutorService() {
        return new AbstractExecutorService() {
            @Override public void shutdown() {

            }

            @Override public List<Runnable> shutdownNow() {
                return null;
            }

            @Override public boolean isShutdown() {
                return false;
            }

            @Override public boolean isTerminated() {
                return false;
            }

            @Override public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
                return false;
            }

            @Override public void execute(Runnable runnable) {
                runnable.run();
            }
        };
    }

    public static class TestExecutor implements Executor {

        private ConcurrentLinkedQueue<Runnable> commands = new ConcurrentLinkedQueue<>();

        @Override public void execute(@NotNull Runnable command) {
            commands.add(command);
        }

        public void triggerActions() {
            for (Runnable command : commands) {
                command.run();
            }
        }
    }

}
