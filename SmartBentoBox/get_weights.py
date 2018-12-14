import time
import sys
import requests
import json
import datetime
from hx711 import HX711


hx = HX711(5, 6)
hx2 = HX711(17,27)
hx3 = HX711(12,16)
hx.set_reading_format("MSB", "MSB")
hx2.set_reading_format("MSB","MSB")
hx3.set_reading_format("MSB","MSB")
hx.set_reference_unit(360)
hx2.set_reference_unit(-292)
hx3.set_reference_unit(383.3)
hx.reset()
hx2.reset()
hx3.reset()



while True:
    val = max(hx.get_weight(5) - 134 - 28, 0)
    val2 = max(hx2.get_weight(17)-28527 -25, 0)
    val3 = max(hx3.get_weight(23)-486-46 +64 -81, 0)
    print "weight1",val,"g"
    print "weight2",val2,"g"
    print "weight3",val3,"g"
    print ""

    payload = {'weight1':val,"weight2":val2,"weight3":val3}
    r = requests.post('http://54.145.179.157:1201/', data=json.dumps(payload))
    #print r.json()
    #time.sleep(0.5)
