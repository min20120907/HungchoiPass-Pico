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

// shunt voltage function
float shunt_voltage(){
	uint16_t shunt_voltage = read_register(__REG_SHUNTVOLTAGE);
	return __SHUNT_MILLIVOLTS_LSB * (int16_t) shunt_voltage;
}

// supply voltage function
float supply_voltage(){
	return voltage() + (shunt_voltage() / 1000.0
}

// Get the current function
float current() {
	uint16_t current_raw = read_register(__REG_CURRENT);
	int16_t current = (int16_t)current_raw;
	if (current > 32767) current -= 65536;
	return  current * _current_lsb * 1000.0;
}

// Power function
float power() {
	uint16_t power_raw = read_register(__REG_POWER);
	int16_t power = (int16_t)power_raw;
	return power * _power_lsb * 1000.0;
}


