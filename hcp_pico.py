from mfrc522 import MFRC522
from machine import Pin, UART, I2C
from rp2 import PIO, StateMachine, asm_pio

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
i2c = I2C(7, scl=Pin(9), sda=Pin(8), freq=100000) 
i2c.scan()
i2c.writeto(76, b'123')
i2c.readfrom(76, 4)

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
                totalPower+=round(power()*100)/1000/100/3600
                print_dual("Card detected %s" % uidToString(uid))
                print_dual("INA219 Sensor ",c, ":")
                print_dual("Current: ", round(current()*1000)/1000000,"A")
                print_dual("Voltage: ", round(voltage() *100)/1000,"V")
                print_dual("Power: ", round(power()*1000)/100000)
                c+=1
            else:
                print("Authentication error")
            
except KeyboardInterrupt:
    print("Bye")

