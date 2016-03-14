package com.game.wargame.Controller.Communication.RabbitMQ;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Created by developer on 3/14/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SocketBufferTest {

    @Mock
    private RabbitMQMessage mMockRabbitMQMessage;

    @Test
    public void testThatConsumedMessageShouldBeForwardedWhenSocketNotInFrozenMode() {
        BlockingQueue<RabbitMQMessage> commands = new LinkedBlockingQueue<>();

        SocketBuffer socketBuffer = new SocketBuffer(commands);

        socketBuffer.consume(mMockRabbitMQMessage);

        assertEquals(1, commands.size());
    }

    @Test
    public void testThatSocketBufferIsFrozenByDefault() {
        BlockingQueue<RabbitMQMessage> commands = new LinkedBlockingQueue<>();
        SocketBuffer socketBuffer = new SocketBuffer(commands);

        assertEquals(false, socketBuffer.frozen());
    }

    @Test
    public void testThatConsumedMessageShouldBeBufferedWhenFrozenModeEnabled() {
        BlockingQueue<RabbitMQMessage> commands = new LinkedBlockingQueue<>();

        SocketBuffer socketBuffer = new SocketBuffer(commands);
        socketBuffer.freeze();

        socketBuffer.consume(mMockRabbitMQMessage);
        socketBuffer.consume(mMockRabbitMQMessage);

        assertEquals(0, commands.size());
    }

    @Test
    public void testBufferedMessagesMustBeForwardedWhenBufferIsUnfrozen() {
        BlockingQueue<RabbitMQMessage> commands = new LinkedBlockingQueue<>();

        SocketBuffer socketBuffer = new SocketBuffer(commands);
        socketBuffer.freeze();

        socketBuffer.consume(mMockRabbitMQMessage);
        socketBuffer.consume(mMockRabbitMQMessage);

        assertEquals(0, commands.size());

        socketBuffer.unfreeze();

        assertEquals(2, commands.size());
    }

}
