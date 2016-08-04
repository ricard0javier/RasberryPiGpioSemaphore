package com.ricardovz.gpio.semaphore;

import com.pi4j.io.gpio.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

import static java.lang.System.exit;

@Slf4j
public class Application {

    public static void main(String[] args) {

        log.info("Application ready!");

        final GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput ledRed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_14, "My Red LED", PinState.LOW);      // PIN STARTUP STATE (optional)
        GpioPinDigitalOutput ledYellow = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_13, "My Yellow LED", PinState.LOW);      // PIN STARTUP STATE (optional)
        GpioPinDigitalOutput ledGreen = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, "My Green LED", PinState.LOW);      // PIN STARTUP STATE (optional)

        Runnable semaphore = () -> {

            try {

                log.info("Green");
                ledGreen.pulse(5000);
                TimeUnit.SECONDS.sleep(3);

                log.info("Yellow");
                ledYellow.blink(250, 2000);
                TimeUnit.SECONDS.sleep(2);

                ledYellow.pulse(2000);
                TimeUnit.SECONDS.sleep(2);

                log.info("Red");
                ledRed.pulse(5000);
                TimeUnit.SECONDS.sleep(5);


            } catch (InterruptedException e) {
                log.debug("Interrupted", e);
                log.warn("Execution interrupted");
            }

        };


        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(2);
        ScheduledFuture<?> future = scheduledThreadPool.scheduleWithFixedDelay(semaphore, 0, 1, TimeUnit.MILLISECONDS);

        Callable<Boolean> stopper = () -> {
            log.info("Stopping the semaphore");

            future.cancel(true);
            exit(0);

            return null;
        };

//        scheduledThreadPool.schedule(stopper, 30, TimeUnit.SECONDS);

    }
}
