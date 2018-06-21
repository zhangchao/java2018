package com.chao.sample;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.jctools.queues.MpscArrayQueue;
import org.jctools.queues.MpscChunkedArrayQueue;
import org.jctools.queues.atomic.MpscLinkedAtomicQueue;

public class TestQueue {
  private static int PRD_THREAD_NUM;
  private static int C_THREAD_NUM=1;

  private static int N = 1<<20;
  private static ExecutorService executor;

  public static void main(String[] args) throws Exception {
    System.out.println("Producer\tConsumer\tcapacity \t LinkedBlockingQueue \t ArrayBlockingQueue \t MpscLinkedAtomicQueue \t MpscChunkedArrayQueue \t MpscArrayQueue");

    for (int j = 1; j < 8; j++) {
      PRD_THREAD_NUM = (int) Math.pow(2, j);
      executor = Executors.newFixedThreadPool(PRD_THREAD_NUM * 2);

      for (int i = 9; i < 12; i++) {
        int length = 1<< i;
        System.out.print(PRD_THREAD_NUM + "\t\t");
        System.out.print(C_THREAD_NUM + "\t\t");
        System.out.print(length + "\t\t");
        System.out.print(doTest2(new LinkedBlockingQueue<Integer>(length), N) + "/s\t\t");
        System.out.print(doTest2(new ArrayBlockingQueue<Integer>(length), N) + "/s\t\t");
        System.out.print(doTest2(new MpscLinkedAtomicQueue<Integer>(), N) + "/s\t\t");
        System.out.print(doTest2(new MpscChunkedArrayQueue<Integer>(length), N) + "/s\t\t");
        System.out.print(doTest2(new MpscArrayQueue<Integer>(length), N) + "/s");
        System.out.println();
      }

      executor.shutdown();
    }
  }

  private static class Producer implements Runnable {
    int n;
    Queue<Integer> q;

    public Producer(int initN, Queue<Integer> initQ) {
      n = initN;
      q = initQ;
    }

    @Override
    public void run() {
      while (n > 0) {
        if (q.offer(n)) {
          n--;
        }
      }
    }
  }

  private static class Consumer implements Callable<Long> {
    int n;
    Queue<Integer> q;

    public Consumer(int initN, Queue<Integer> initQ) {
      n = initN;
      q = initQ;
    }

    public Long call() {
      long sum = 0;
      Integer e = null;
      while (n > 0) {
        if ((e = q.poll()) != null) {
          sum += e;
          n--;
        }

      }
      return sum;
    }
  }

  private static long doTest2(final Queue<Integer> q, final int n)
      throws Exception {
    CompletionService<Long> completionServ = new ExecutorCompletionService<>(executor);

    long t = System.nanoTime();
    for (int i = 0; i < PRD_THREAD_NUM; i++) {
      executor.submit(new Producer(n / PRD_THREAD_NUM, q));
    }
    for (int i = 0; i < C_THREAD_NUM; i++) {
      completionServ.submit(new Consumer(n / C_THREAD_NUM, q));
    }

    for (int i = 0; i < 1; i++) {
      completionServ.take().get();
    }

    t = System.nanoTime() - t;
    return (long) (1000000000.0 * N / t); // Throughput, items/sec
  }
}
