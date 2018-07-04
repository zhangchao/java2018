package com.chao.sample;

import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import io.netty.util.concurrent.ThreadProperties;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;

public class TestNettySingleThread {

  public static void main(String[] args) {
    final AtomicReference<Thread> threadRef = new AtomicReference();
    SingleThreadEventExecutor executor = new SingleThreadEventExecutor(
        null, new DefaultThreadFactory("test"), false) {
      @Override
      protected void run() {
        threadRef.set(Thread.currentThread());
        while (!confirmShutdown()) {
          Runnable task = takeTask();
          if (task != null) {
            task.run();
          }
        }
      }
    };
    ThreadProperties threadProperties = executor.threadProperties();

    Thread thread = threadRef.get();
    Assert.assertEquals(thread.getId(), threadProperties.id());
    Assert.assertEquals(thread.getName(), threadProperties.name());
    Assert.assertEquals(thread.getPriority(), threadProperties.priority());
    Assert.assertEquals(thread.isAlive(), threadProperties.isAlive());
    Assert.assertEquals(thread.isDaemon(), threadProperties.isDaemon());
    Assert.assertTrue(threadProperties.stackTrace().length > 0);
    executor.shutdownGracefully();
  }

}
