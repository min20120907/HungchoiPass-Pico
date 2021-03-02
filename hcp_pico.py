import board
import busio
import time
from adafruit_ina219 import ADCResolution, BusVoltageRange, INA219
from mfrc522 import MFRC522
from machine import Pin, UART, I2C
from rp2 import PIO, StateMachine, asm_pio
from time import sleep

def uidToString(uid):
    mystring = ""
    for i in uid:
        mystring = "%02X" % i + mystring
    return mystring

# The function that can print both in bluetooth mode and serial mode
def print_dual(string):
    print(string)
    uart.write(string+"\n")

# LED pin initialization
led = Pin(11, Pin.OUT)

# I2C initialization
SCL = board.GP15
SDA = board.GP14
i2c_bus = busio.I2C(SCL, SDA)

ina219 = INA219(i2c_bus, 0x40) # Accessory Supply
range = ina219.bus_voltage_range

# optional : change configuration to use 32 samples averaging for both bus voltage and shunt voltage
ina219.bus_adc_resolution = ADCResolution.ADCRES_12BIT_32S
ina219.shunt_adc_resolution = ADCResolution.ADCRES_12BIT_32S

# optional : change voltage range to 16V
ina219.bus_voltage_range = BusVoltageRange.RANGE_16V

# UART initialization
uart = UART(0, 9600)

# Set default values of INA219 sensor
SHUNT_OHMS = 0.1
MAX_EXPECTED_AMPS = 3.2
INITIAL_CHARGE = 15000.0
INTERVAL = 1000.0

# RC522 SPI initialization
reader = MFRC522(spi_id=0,sck=2,miso=4,mosi=3,cs=1,rst=0)

print("")
print("Place card before reader to read from address 0x08")
print("")

print_dual("Welcome to Hungchoi Pass Pico!")

try:
    while True:
        command = uart.readline()
        
        (stat, tag_type) = reader.request(reader.REQIDL)
        
        if stat == reader.OK:

            (stat, uid) = reader.SelectTagSN()

            if stat == reader.OK:
                totalPower+=power*3600/1000
                print_dual("Card detected %s" % uidToString(uid))
                print_dual("INA219 Sensor ",c, ":")
                print_dual("Current :{:7.4f} A".format(current / 1000))
                print_dual("Voltage  :{:5.2f} V".format(bus_voltage + shunt_voltage))
                print_dual("Power   :{:5.2f} W".format(power))
                print_dual("Electricity: ", totalPower)
                if ina219.overflow:
                    print_dual("Internal Math Overflow Detected!")
                c+=1
                sleep(1)
            else:
                print("Authentication error")
            
except KeyboardInterrupt:
    print("Bye")

