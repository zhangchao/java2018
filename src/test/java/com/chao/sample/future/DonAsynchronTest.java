package com.chao.sample.future;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DonAsynchronTest {


    ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));


    @Test
    public void testA() throws InterruptedException {

        ListenableFuture future2 = service.submit(new Callable<Integer>() {
            public Integer call() throws InterruptedException {
                Thread.sleep(100);
                System.out.println("call future 2.");
                //       throw new RuntimeException("----call future 2.");
                return 2;
            }
        });

        DonAsynchron.withCallback(
                future2,
                values -> {
                    System.out.println(values);
                },
                e -> {
                    System.out.println("Failed to fetch missed updates." + e.getMessage());
                },
                service
        );


        TimeUnit.SECONDS.sleep(2);


    }
}
