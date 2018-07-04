package com.chao.sample;

import java.util.ArrayDeque;
import java.util.Deque;

public class TestDequeue {

  public static void main(String[] args) {
    Deque<String> deque = new ArrayDeque(10);
    deque.add("1");
    deque.add("2");
    deque.add("3");

    deque.stream().forEach( t ->{
      System.out.println(deque.poll());
    });

    deque.offer("a");
    deque.offer("b");
    deque.offer("c");

    deque.stream().forEach( t ->{
      System.out.println(deque.pop());
    });

    deque.push("z");
    deque.push("z1");
    deque.push("z2");

    deque.stream().forEach( t ->{
      System.out.println(deque.pop());
    });


  }

}
