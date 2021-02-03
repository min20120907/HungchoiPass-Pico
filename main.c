
/**
 * Copyright (c) 2020 Raspberry Pi (Trading) Ltd.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

#include <stdio.h>
#include <math.h>
#include "pico/stdlib.h"
#include "ina219/ina219.h"
#include "hardware/i2c.h"
int main() {
    stdio_init_all();
    // This example will use I2C0 on GPIO4 (SDA) and GPIO5 (SCL) running at 400kHz.
    i2c_init(I2C_PORT, 400 * 1000);
    gpio_set_function(4, GPIO_FUNC_I2C);
    gpio_set_function(5, GPIO_FUNC_I2C);
    gpio_pull_up(4);
    gpio_pull_up(5);
    //const uint LED_PIN = 25;
    //gpio_init(LED_PIN);
    //gpio_set_dir(LED_PIN, GPIO_OUT);
    
    float SHUNT_OHMS = 0.1;
    float MAX_EXPECTED_AMPS = 3.2;
    float INITIAL_CHARGE = 15000.0;
    float INTERVAL = 1000.0;

    // Initilization and Configuration of INA219 chip
    configure(RANGE_16V, GAIN_8_320MV, ADC_12BIT, ADC_12BIT);
    init(SHUNT_OHMS, MAX_EXPECTED_AMPS);
    reset();
    printf("Hello, world!\n");
    int c = 0;
    float totalPower = 0;
    while (true) {
	// Example test codes
	//printf("Hello World");
	// Blink Example Text
        //gpio_put(LED_PIN, 1);
        //sleep_ms(250);
        //gpio_put(LED_PIN, 0);
        //sleep_ms(250);
	
	// INA219 Sensor codes
	totalPower+=roundf(power() * 100) /1000/ 100/3600;
	//Voltage current serial
	printf("INA219 Sensor %d:\n", c);
	printf("Current: %lfA\n", roundf(current() * 1000) / 1000000);
	printf("Voltage: %lfV\n", (roundf(voltage() * 1000) / 1000));
	printf("Power: %lfW\n", roundf(power() * 100) / 100000);
	printf("Electricity: %lfWh\n",totalPower);
	c++;
	sleep_ms(1000);
    }
    return 0;
}

