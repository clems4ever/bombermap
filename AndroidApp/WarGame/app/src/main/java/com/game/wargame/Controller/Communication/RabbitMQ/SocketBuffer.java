package com.game.wargame.Controller.Communication.RabbitMQ;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by developer on 3/14/16.
 */
public class SocketBuffer {

    boolean mFrozen;

    private BlockingQueue<RabbitMQMessage> mFrozenCommandQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<RabbitMQMessage> mCommandQueue;


    public SocketBuffer(BlockingQueue<RabbitMQMessage> commandQueue) {
        mFrozen = false;
        mCommandQueue = commandQueue;
    }

    public void freeze() {
        mFrozen = true;
    }

    public void unfreeze() {
        while(!mFrozenCommandQueue.isEmpty()) {
            mCommandQueue.add(mFrozenCommandQueue.poll());
        }
        mFrozen = false;
    }

    public boolean frozen() {
        return mFrozen;
    }

    public void consume(RabbitMQMessage message) {
        if(mFrozen) {
            mFrozenCommandQueue.add(message);
        }
        else {
            mCommandQueue.add(message);
        }
    }
}
