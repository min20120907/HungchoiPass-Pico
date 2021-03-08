"""Example script.

Edit the I2C interface constant to match the one you have
connected the sensor to.
"""

from ina219 import INA219
from utime import sleep_ms
import machine

# Edit to match interface the sensor is connect to (1 or 2).

SHUNT_OHMS = 0.1

i2c=machine.I2C(0)
i2c.scan()
sleep_ms(100)
# i2c.readfrom_mem(0x40, 0x03, 2)
ina = INA219(SHUNT_OHMS, i2c)
ina.configure()
print("Bus Voltage: %.3f V" % ina.voltage())
print("Current: %.3f mA" % ina.current())
print("Power: %.3f mW" % ina.power())
