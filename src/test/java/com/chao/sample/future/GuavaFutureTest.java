package com.chao.sample.future;

import com.google.common.util.concurrent.*;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class GuavaFutureTest {


    @Test
    public void should_test_furture() throws Exception {

        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

        ListenableFuture future1 = service.submit(() -> {
            Thread.sleep(1000);
            System.out.println("call future 1.");
            return 1;
        });

        ListenableFuture future2 = service.submit(new Callable<Integer>() {
            public Integer call() throws InterruptedException {
                Thread.sleep(1000);
                System.out.println("call future 2.");
                //       throw new RuntimeException("----call future 2.");
                return 2;
            }
        });

        final ListenableFuture allFutures = Futures.allAsList(future1, future2);

        final ListenableFuture transform = Futures.transformAsync(allFutures, new AsyncFunction<List<Integer>, Boolean>() {
            @Override
            public ListenableFuture apply(List<Integer> results) throws Exception {
                return Futures.immediateFuture(String.format("success future:%d", results.size()));
            }
        }, service);

//
//        final ListenableFuture transform2 = Futures.transform(allFutures, new Function<List<Integer>, Boolean>() {
//
//            @Override
//            public ListenableFuture apply(List<Integer> results) throws Exception {
//                return Futures.immediateFuture(String.format("success future:%d", results.size()));
//            }
//        },null);


        Futures.addCallback(transform, new FutureCallback<Object>() {

            public void onSuccess(Object result) {
                System.out.println(result.getClass());
                System.out.printf("success with: %s%n", result);
            }

            public void onFailure(Throwable thrown) {
                System.out.printf("onFailure%s%n", thrown.getMessage());
            }
        }, service);

        System.out.println(transform.get());
    }
}
