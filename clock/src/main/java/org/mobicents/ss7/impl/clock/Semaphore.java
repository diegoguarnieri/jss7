/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.ss7.impl.clock;

import org.mobicents.ss7.spi.clock.Timer;

/**
 *
 * @author kulikov
 */
public class Semaphore {

    // timer instance
    private Timer timer;

    //precision for time measurement
    private long precision;
    
    //time interval around finish time
    private long low;
    
    //current time
    private long now;
    
    private long ems;
    
    private volatile boolean nothingTodo = true;
    /**
     * Creates new instance of semaphore.
     * 
     * @param timer timer which will be used for time measurement.
     * @param precision  the precision for time measurement in microseconds
     */
    @SuppressWarnings("static-access")
    public Semaphore(Timer timer, long precision) {
        this.timer = timer;
        this.precision = precision;
        
        //determine worst sleep() execution
        
        long error = 0; //max detected error.
        long T = 10; //sleep amount
        
        for (int i = 0; i < 100; i++) {
            long s = timer.getTimestamp();
            try {
                Thread.currentThread().sleep(T);
            } catch (InterruptedException e) {
    }
            long e = Math.abs((timer.getTimestamp() - s) - T * 1000000L);
    
            //checking error
            if (e > error) error = e;
        }
        
        //round result to the milliseconds
        ems = error/1000000L + 1;
        ems *= 2;
    }
    
    /**
     * Blocks thread for specified amount of time.
     * 
     * @param amount the amount of time in milliseconds. must be positive.
     */
    public void block(long amount) {
        nothingTodo = true;
        low = timer.getTimestamp() + amount * 1000000L - precision * 1000L;
        
        //try sleep first
        if (amount - ems > ems) {
            synchronized(this) {
                try {
                    wait(amount - ems);
                } catch (InterruptedException e) {
                }
            }
        }
        
        now = timer.getTimestamp();
        //poll timer in cycle till required time will be reached
        while ((now < low) && nothingTodo) {
            now = timer.getTimestamp();
        }
    }
    
    protected void ping() {
        nothingTodo = false;
        synchronized(this) {
            notify();
        }
    }
    
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(new TimerImpl(), 10);
        long s = System.nanoTime();
        semaphore.block(22);
        long d = System.nanoTime() - s;
        System.out.println(d);
}
}
