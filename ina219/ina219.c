#include "ina219.h"

// standard libraries
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <fcntl.h>
#include <unistd.h>
// Pico Libraries
#include "pico/stdlib.h"
#include "hardware/i2c.h"

// Method to write value into the register
void write_register(uint8_t register_address, uint16_t register_value)
{
	uint8_t buf[3];
	buf[0] = register_address;
	buf[1] = register_value >> 8;
	buf[2] = register_value & 0xFF;

	if (i2c_write_blocking(I2C_PORT, _file_descriptor, buf, 3,true) != 3)
	{
		perror("Failed to write to the i2c bus");
	}
}

// Reset funciton
void reset(){
	write_register(__REG_CONFIG,__RST);
}

// Get volatage from I2C bus
float voltage() {
	uint16_t value = read_register(__REG_BUSVOLTAGE) >> 3;
	return (float) (value) * __BUS_MILLIVOLTS_LSB / 1000.0;
}

// shunt voltage function
float shunt_voltage(){
	uint16_t shunt_voltage = read_register(__REG_SHUNTVOLTAGE);
	return __SHUNT_MILLIVOLTS_LSB * (int16_t) shunt_voltage;
}

// supply voltage function
float supply_voltage(){
	return voltage() + (shunt_voltage() / 1000.0);
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

// determine current_lsb
float determine_current_lsb(float max_expected_amps, float max_possible_amps)
{
	float current_lsb;

	float nearest = roundf(max_possible_amps * 1000.0) / 1000.0;
	if (max_expected_amps > nearest) {
		char buffer[65];
		sprintf(buffer, "Expected current %f A is greater than max possible current %f A", max_expected_amps, max_possible_amps);
		perror(buffer);
	}

	if (max_expected_amps < max_possible_amps) {
		current_lsb = max_expected_amps / __CURRENT_LSB_FACTOR;
	} else {
		current_lsb = max_possible_amps / __CURRENT_LSB_FACTOR;
	}
	
	if (current_lsb < _min_device_current_lsb) {
		current_lsb = _min_device_current_lsb;
	}
	return current_lsb;
}

void calibrate(int bus_volts_max, float shunt_volts_max, float max_expected_amps)
{
	float max_possible_amps = shunt_volts_max / _shunt_ohms;
	_current_lsb = determine_current_lsb(max_expected_amps, max_possible_amps);
	_power_lsb = _current_lsb * 20.0;
	uint16_t calibration = (uint16_t) trunc(__CALIBRATION_FACTOR / (_current_lsb * _shunt_ohms));
	write_register(__REG_CALIBRATION, calibration);
}

void configure(int voltage_range, int gain, int bus_adc, int shunt_adc)
{
	reset();
	
	int len = sizeof(__BUS_RANGE) / sizeof(__BUS_RANGE[0]);
	if (voltage_range > len-1) {
		perror("Invalid voltage range, must be one of: RANGE_16V, RANGE_32");
	}
	_voltage_range = voltage_range;
	_gain = gain;

	calibrate(__BUS_RANGE[voltage_range], __GAIN_VOLTS[gain], _max_expected_amps);
	uint16_t calibration = (voltage_range << __BRNG | _gain << __PG0 | bus_adc << __BADC1 | shunt_adc << __SADC1 | __CONT_SH_BUS);
	write_register(__REG_CONFIG, calibration);
}

uint16_t read_register(uint8_t register_address)
{
	uint8_t buf[3];
	buf[0] = register_address;
	if (write(_file_descriptor, buf, 1) != 1) {
		perror("Failed to set register");
	}
	sleep_ms(1000);
	if (read(_file_descriptor, buf, 2) != 2) {
		perror("Failed to read register value");
	}
	return (buf[0] << 8) | buf[1];
}
void ina_sleep() {
	uint16_t config = read_register(__REG_CONFIG);
	write_register(__REG_CONFIG, config & 0xFFF8);
}
void init(float shunt_resistance, float max_expected_amps){
	_shunt_ohms = shunt_resistance;
	_max_expected_amps = max_expected_amps;
	_min_device_current_lsb = __CALIBRATION_FACTOR / (_shunt_ohms * __MAX_CALIBRATION_VALUE);
}
