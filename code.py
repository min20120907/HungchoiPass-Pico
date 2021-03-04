import time
from ina219 import INA219
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

# I2C INA219 initialization
# Edit to match interface the sensor is connect to (1 or 2).
I2C_INTERFACE_NO = 1
SHUNT_OHMS = 0.1

ina = INA219(SHUNT_OHMS, I2C(I2C_INTERFACE_NO))
ina.configure()

# UART initialization
uart = UART(0, 9600)

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
                totalPower+=ina.power()*3600/1000
                print_dual("Card detected %s" % uidToString(uid))
                print_dual("INA219 Sensor ",c, ":")
                print_dual("Current :{:7.4f} A".format(ina.current() / 1000))
                print_dual("Voltage  :{:5.2f} V".format(ina.bus_voltage() + ina.shunt_voltage()))
                print_dual("Power   :{:5.2f} W".format(ina.power()))
                print_dual("Electricity: ", totalPower)
                c+=1
                sleep(1)
            else:
                print("Authentication error")
            
except KeyboardInterrupt:
    print("Bye")



