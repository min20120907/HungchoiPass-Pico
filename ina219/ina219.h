#ifndef _INA219_H
#define _INA219_H

#define I2C_PORT i2c0

#define RANGE_16V                           0 // Range 0-16 volts
#define RANGE_32V                           1 // Range 0-32 volts

#define GAIN_1_40MV                         0 // Maximum shunt voltage 40mV
#define GAIN_2_80MV                         1 // Maximum shunt voltage 80mV
#define GAIN_4_160MV                        2 // Maximum shunt voltage 160mV
#define GAIN_8_320MV                        3 // Maximum shunt voltage 320mV
// #define GAIN_AUTO                          -1 // Determine gain automatically

#define ADC_9BIT                            0  // 9-bit conversion time  84us.
#define ADC_10BIT                           1  // 10-bit conversion time 148us.
#define ADC_11BIT                           2  // 11-bit conversion time 2766us.
#define ADC_12BIT                           3  // 12-bit conversion time 532us.
#define ADC_2SAMP                           9  // 2 samples at 12-bit, conversion time 1.06ms.
#define ADC_4SAMP                           10 // 4 samples at 12-bit, conversion time 2.13ms.
#define ADC_8SAMP                           11 // 8 samples at 12-bit, conversion time 4.26ms.
#define ADC_16SAMP                          12 // 16 samples at 12-bit,conversion time 8.51ms
#define ADC_32SAMP                          13 // 32 samples at 12-bit, conversion time 17.02ms.
#define ADC_64SAMP                          14 // 64 samples at 12-bit, conversion time 34.05ms.
#define ADC_128SAMP                         15 // 128 samples at 12-bit, conversion time 68.10ms.

#define __ADDRESS                           0x40

#define __REG_CONFIG                        0x00
#define __REG_SHUNTVOLTAGE                  0x01
#define __REG_BUSVOLTAGE                    0x02
#define __REG_POWER                         0x03
#define __REG_CURRENT                       0x04
#define __REG_CALIBRATION                   0x05

#define __RST                               15
#define __BRNG                              13
#define __PG1                               12
#define __PG0                               11
#define __BADC4                             10
#define __BADC3                             9
#define __BADC2                             8
#define __BADC1                             7
#define __SADC4                             6
#define __SADC3                             5
#define __SADC2                             4
#define __SADC1                             3
#define __MODE3                             2
#define __MODE2                             1
#define __MODE1                             0

#define __OVF                               1
#define __CNVR                              2

#define __CONT_SH_BUS                       7

#define __SHUNT_MILLIVOLTS_LSB              0.01    // 10uV
#define __BUS_MILLIVOLTS_LSB                4       // 4mV
#define __CALIBRATION_FACTOR                0.04096
#define __MAX_CALIBRATION_VALUE             0xFFFE  // Max value supported (65534 decimal)

// In the spec (p17) the current LSB factor for the minimum LSB is
// documented as 32767, but a larger value (100.1% of 32767) is used
// to guarantee that current overflow can always be detected.
#define __CURRENT_LSB_FACTOR                32770


#include <stdint.h>
//public variables

// public functions
void configure(int voltage_range, int gain, int bus_adc, int shunt_adc);
void ina_sleep();
void wake();
void reset();
float voltage();
float current();
float shunt_voltage();
float supply_voltage();
float power();

// constructors
void init(float shunt_resistance, float max_expected_amps);

// private varaibles
int     _file_descriptor;
float   _shunt_ohms;
float   _max_expected_amps;
float   _min_device_current_lsb;
int     _voltage_range;
int     _gain;
float   _current_lsb;
float   _power_lsb;

//private functions
uint16_t    read_register(uint8_t register_value);
void        write_register(uint8_t register_address, uint16_t register_value);
float       determine_current_lsb(float max_expected_amps, float max_possible_amps);
void        calibrate(int bus_volts_max, float shunt_volts_max, float max_expected_amps);

#endif
