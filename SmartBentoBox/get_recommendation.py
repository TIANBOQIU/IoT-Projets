from klein import run, route
import json
import requests

APP_RECIPE_ID = '###'
APP_RECIPE_KEY = '###'

@route('/', methods=['POST'])
def do_post(request):
    content = json.loads(request.content.read())
    response = json.dumps(dict(the_data=content), indent=4)
    calorie = content['calorie']
    choice = content['choice']


    diet_label = 'balanced'
    #diet_label = 'low-fat'

    calorie = str(int(float(calorie)))
    calories = '1-'+calorie
    #calories = calorie
    print "received:",calories

    #APP_RECIPE_URL = 'https://api.edamam.com/search?q=&app_id={}&app_key={}&from=0&to=1&calories={}&diet={}'.format(APP_RECIPE_ID,APP_RECIPE_KEY,calories,diet_label)
    APP_RECIPE_URL = 'https://api.edamam.com/search?q={}&app_id={}&app_key={}&calories={}&diet={}'.format(choice,APP_RECIPE_ID,APP_RECIPE_KEY,calories,diet_label)
    r = requests.get(APP_RECIPE_URL)
    re = r.json()

    food_name = re['hits'][0]['recipe']['label']
    img = re['hits'][0]['recipe']['image']
    cal = str(re['hits'][0]['recipe']['calories'] / re['hits'][0]['recipe']['yield'])
    fat = str(re['hits'][0]['recipe']['totalNutrients']['FAT']['quantity'])
    protein = str(re['hits'][0]['recipe']['totalNutrients']['PROCNT']['quantity'])



    print calorie,'received'
    print food_name,img,cal,fat,protein
    return json.dumps("{}#{}#{}#{}#{}".format(food_name,img,cal,fat,protein))
    #return "ok"

run("", 1203)
