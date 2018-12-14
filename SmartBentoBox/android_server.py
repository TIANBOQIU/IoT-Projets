from klein import run, route
import json

import datetime
import pytz
import base64
import cv2
import requests
import pymongo
#import numpy as np

API_KEY_CLOUD_VISION = '###'
URL_CLOUD_VISION = 'https://vision.googleapis.com/v1/images:annotate?key=' + API_KEY_CLOUD_VISION

def get_labels(r):
    labels = []
    for d in r['labelAnnotations']:
        labels.append(d['description'])
    return labels

def get_food_name_and_nutrition(label_string):
    APP_ID = '###'
    API_KEY_NUTRI='###'
    URL_NUTRI='https://trackapi.nutritionix.com/v2/natural/nutrients'
    headers = {'Content-Type': 'application/json','x-app-id':APP_ID,'x-app-key':API_KEY_NUTRI}

    payload = {"query":label_string}
    r = requests.post(URL_NUTRI,headers=headers,json=payload)
    food_name =  r.json()["foods"][0]["food_name"]
    calorie = r.json()["foods"][0]["nf_calories"]
    fat = r.json()["foods"][0]["nf_total_fat"]
    protein = r.json()["foods"][0]["nf_protein"]
    serving_weight_grams = r.json()["foods"][0]["serving_weight_grams"]
    return food_name, calorie, fat, protein, serving_weight_grams

def get_food_labels(imageName):
    name = imageName[:-5]
    img = cv2.imread(imageName)
    row = img.shape[0]
    col = img.shape[1]
    part3 = img[row/2:row,0:col]
    part1 = img[0:row/2,0:col/2]
    part2 = img[0:row/2,col/2:col]
    cv2.imwrite(name+'part3.JPEG',part3)
    cv2.imwrite(name+'part2.JPEG',part2)
    cv2.imwrite(name+'part1.JPEG',part1)
    img3 = open(name+"part3.JPEG",'rb')
    encodedImage3 = base64.b64encode(img3.read())
    img2 = open(name+"part2.JPEG",'rb')
    encodedImage2 = base64.b64encode(img2.read())
    img1 = open(name+"part1.JPEG",'rb')
    encodedImage1 = base64.b64encode(img1.read())
    payload = {
      "requests":[
        {
          "image":{
            "content":encodedImage1
          },
          "features":[
            {
              "type":"LABEL_DETECTION",

            }
          ]
        },
        {
          "image":{
            "content":encodedImage2
          },
          "features":[
            {
              "type":"LABEL_DETECTION",

            }
          ]
        },
        {
          "image":{
            "content":encodedImage3
          },
          "features":[
            {
              "type":"LABEL_DETECTION",

            }
          ]
        }
      ]
    }
    headers_cv = {'Content-Type': 'application/json'}
    r = requests.post(URL_CLOUD_VISION,headers=headers_cv, json=payload)
    print "cv", r.status_code
    response = r.json()['responses']
    #print response
    label_strings = []
    for ret in response:
        labels = get_labels(ret)
        label_string = ""
        for label in labels:
            if not label == "vegetable" and not label == "fruit":
                label_string = label_string + label + " "
        label_strings.append(label_string)
    return label_strings




def get_name():
    nyc = datetime.datetime.now(tz=pytz.timezone('US/Eastern'))
    return "{} {} {} {} {}.JPEG".format(nyc.month,nyc.day,nyc.hour,nyc.minute,nyc.second)

def save_image(encodedImage, name):
    fh =  open(name,"wb")
    fh.write(encodedImage.decode('base64'))
    fh.close()




@route('/', methods=['POST'])
def do_post(request):
    content = json.loads(request.content.read())
    response = json.dumps(dict(the_data=content), indent=4)
    encodedImage = content['image']
    imageName = get_name()
    save_image(encodedImage, imageName)
    print encodedImage

    label_strings = get_food_labels(imageName)

    food_name1, calorie1, fat1, protein1, serving_weight_grams1 =  get_food_name_and_nutrition(label_strings[0])
    food_name2, calorie2, fat2, protein2, serving_weight_grams2 =  get_food_name_and_nutrition(label_strings[1])
    food_name3, calorie3, fat3, protein3, serving_weight_grams3 =  get_food_name_and_nutrition(label_strings[2])

    # get weights
    sort = [('_id',-1)]
    usr = "##USERNAME"
    pin = "###"
    client = pymongo.MongoClient("mongodb://{}:{}@54.145.179.157/iot6".format(usr,pin))
    db = client.iot6
    cur = db.test.find({},limit=1).sort(sort)
    list_weight = []
    for i in cur:
        list_weight.append(i)
    dict_weights = list_weight[0]
    weight1 = dict_weights['weight1']
    weight2 = dict_weights['weight2']
    weight3 = dict_weights['weight3']

    calorie1 = calorie1 / serving_weight_grams1 * weight1
    fat1 = fat1 / serving_weight_grams1 * weight1
    protein1 = protein1 / serving_weight_grams1 * weight1

    calorie2 = calorie2 / serving_weight_grams2 * weight2
    fat2 = fat2 / serving_weight_grams2 * weight2
    protein2 = protein2 / serving_weight_grams2 * weight2

    calorie3 = calorie3 / serving_weight_grams3 * weight3
    fat3 = fat3 / serving_weight_grams3 * weight3
    protein3 = protein3 / serving_weight_grams3 * weight3

    timestamp = imageName[:-5].split(' ')

    ret = {"weights":[weight1,weight2,weight3],"day":timestamp[1],"hour":timestamp[2],"minute":timestamp[3],"second":timestamp[4]}
    db.ret.insert_one(ret)





    #label1 = food_name1 + str(calorie1) + str(fat1) + str(protein1) +"per" +str(serving_weight_grams1)
    #label2 = food_name2 + str(calorie2) + str(fat2) + str(protein2) +"per" +str(serving_weight_grams2)
    #label3 = food_name3 + str(calorie3) + str(fat3) + str(protein3) +"per" +str(serving_weight_grams3)
    #label1 = "{}:\n{}Cal\nfat{}g\nprotein{}g\nper{}\n".format(food_name1, calorie1, fat1, protein1, serving_weight_grams1)
    #label2 = "{}:\n{}Cal\nfat{}g\nprotein{}g\nper{}\n".format(food_name2, calorie2, fat2, protein2, serving_weight_grams2)
    #label3 = "{}:\n{}Cal\nfat{}g\nprotein{}g\nper{}\n".format(food_name3, calorie3, fat3, protein3, serving_weight_grams3)
    #label1 = "{}:\n{}Cal\nfat{}g\nprotein{}g\n".format(food_name1, calorie1, fat1, protein1)
    #label2 = "{}:\n{}Cal\nfat{}g\nprotein{}g\n".format(food_name2, calorie2, fat2, protein2)
    #label3 = "{}:\n{}Cal\nfat{}g\nprotein{}g\n".format(food_name3, calorie3, fat3, protein3)

    return 'Complete!#{}#{}#{}#{}#{}#{}#{}#{}#{}#{}#{}#{}'.format(food_name1, calorie1, fat1, protein1,food_name2, calorie2, fat2, protein2,food_name3, calorie3, fat3, protein3)

run("", 1021)
