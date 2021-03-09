import time
from ina219 import INA219
from mfrc522 import MFRC522
from machine import Pin, UART, I2C, PWM
from rp2 import PIO, StateMachine, asm_pio
from utime import sleep
buzzer = PWM(Pin(15))

tones = {
"B0": 31,
"C1": 33,
"CS1": 35,
"D1": 37,
"DS1": 39,
"E1": 41,
"F1": 44,
"FS1": 46,
"G1": 49,
"GS1": 52,
"A1": 55,
"AS1": 58,
"B1": 62,
"C2": 65,
"CS2": 69,
"D2": 73,
"DS2": 78,
"E2": 82,
"F2": 87,
"FS2": 93,
"G2": 98,
"GS2": 104,
"A2": 110,
"AS2": 117,
"B2": 123,
"C3": 131,
"CS3": 139,
"D3": 147,
"DS3": 156,
"E3": 165,
"F3": 175,
"FS3": 185,
"G3": 196,
"GS3": 208,
"A3": 220,
"AS3": 233,
"B3": 247,
"C4": 262,
"CS4": 277,
"D4": 294,
"DS4": 311,
"E4": 330,
"F4": 349,
"FS4": 370,
"G4": 392,
"GS4": 415,
"A4": 440,
"AS4": 466,
"B4": 494,
"C5": 523,
"CS5": 554,
"D5": 587,
"DS5": 622,
"E5": 659,
"F5": 698,
"FS5": 740,
"G5": 784,
"GS5": 831,
"A5": 880,
"AS5": 932,
"B5": 988,
"C6": 1047,
"CS6": 1109,
"D6": 1175,
"DS6": 1245,
"E6": 1319,
"F6": 1397,
"FS6": 1480,
"G6": 1568,
"GS6": 1661,
"A6": 1760,
"AS6": 1865,
"B6": 1976,
"C7": 2093,
"CS7": 2217,
"D7": 2349,
"DS7": 2489,
"E7": 2637,
"F7": 2794,
"FS7": 2960,
"G7": 3136,
"GS7": 3322,
"A7": 3520,
"AS7": 3729,
"B7": 3951,
"C8": 4186,
"CS8": 4435,
"D8": 4699,
"DS8": 4978
}

song = ["G6","A6","AS6","B6"]

def playtone(frequency):
    buzzer.duty_u16(1000)
    buzzer.freq(frequency)

def bequiet():
    buzzer.duty_u16(0)

def playsong(mysong):
    for i in range(len(mysong)):
        if (mysong[i] == "P"):
            bequiet()
        else:
            playtone(tones[mysong[i]])
        sleep(0.1)
    bequiet()
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
played = False
last_card=""
try:
    while True:
        (stat, tag_type) = reader.request(reader.REQIDL)
        if stat == reader.OK:
            (stat, uid) = reader.SelectTagSN()
            if not played or last_card != uidToString(uid) and stat == reader.OK:
                playsong(song)
                last_card=uidToString(uid)
                played=True
            else:
                print_dual("Authentication error")
            
            totalPower+=ina.power()*3600/1000
            print_dual("Card detected %s" % last_card)
            print_dual("INA219 Sensor "+str(c)+ ":")
            print_dual("Current :{:7.4f} A".format(ina.current() / 1000))
            print_dual("Voltage  :{:5.2f} V".format(ina.voltage() + ina.shunt_voltage()))
            print_dual("Power   :{:5.2f} W".format(ina.power()))
            print_dual("Electricity: "+ str(totalPower))
            c+=1

except KeyboardInterrupt:
    print_dual("Bye")
            






