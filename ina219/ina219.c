#include <stdio.h>
#include <string.h>
#include "pico/stdlib.h"
#include "hardware/i2c.h"

// Method to write value into the register
void write_register(uint8_t register_address, uint16_t register_value)
{
	uint8_t buf[3];
	buf[0] = register_address;
	buf[1] = register_value >> 8;
	buf[2] = register_value & 0xFF;
	
	if (i2c_write_blocking(I2c_PORT, _file_descriptor, buf, 3) != 3)
	{
		perror("Failed to write to the i2c bus");
	}
}

// Reset funciton
void reset(){
	write_register(__REGCONFIG,__RST);
}

// Get volatage from I2C bus
float voltage() {
	uint16_t value = read_register(__REG_BUSVOLTAGE) >> 3;
	return float(value) * __BUS_MILLIVOLTS_LSB / 1000.0;
}

// shunt_voltage function
float shunt_volat
