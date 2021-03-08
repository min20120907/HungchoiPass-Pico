import time
from ina219 import INA219
from mfrc522 import MFRC522
from machine import Pin, UART, I2C
from rp2 import PIO, StateMachine, asm_pio
from time import sleep
# UART initialization
uart = UART(0, 9600,tx=Pin(12), rx=Pin(13))
def uidToString(uid):
    mystring = ""
    for i in uid:
        mystring = "%02X" % i + mystring
    return mystring

# The function that can print both in bluetooth mode and serial mode


# LED pin initialization
led = Pin(25, Pin.OUT)

# I2C INA219 initialization
# Edit to match interface the sensor is connect to (1 or 2).
I2C_INTERFACE_NO = 0
SHUNT_OHMS = 0.1

ina = INA219(SHUNT_OHMS, I2C(I2C_INTERFACE_NO))
ina.configure()



def print_dual(string):
    # print(string)
    uart.write(string+"\r\n")
# RC522 SPI initialization
reader = MFRC522(spi_id=0, sck=2,miso=4,mosi=3,cs=1,rst=0)
reader.init()
print_dual("Welcome to Hungchoi Pass Pico!")
totalPower = 0.0
c=0
try:
    while True:
        (stat, tag_type) = reader.request(reader.REQIDL)
        if stat == reader.OK:
            (stat, uid) = reader.SelectTagSN()
            if stat == reader.OK:
                totalPower+=ina.power()*3600/1000
                print_dual("Card detected %s" % uidToString(uid))
                print_dual("INA219 Sensor "+str(c)+ ":")
                print_dual("Current :{:7.4f} A".format(ina.current() / 1000))
                print_dual("Voltage  :{:5.2f} V".format(ina.voltage() + ina.shunt_voltage()))
                print_dual("Power   :{:5.2f} W".format(ina.power()))
                print_dual("Electricity: "+ str(totalPower))
                c+=1
                
            else:
                print_dual("Authentication error")

except KeyboardInterrupt:
    print_dual("Bye")
            





