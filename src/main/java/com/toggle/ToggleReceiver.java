package com.toggle;

import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class ToggleReceiver {

    private CountDownLatch latch = new CountDownLatch(1);

    public void receiveMessage(String message) {
        System.out.println("Add listener to toggle status change <" + message + ">");
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

}