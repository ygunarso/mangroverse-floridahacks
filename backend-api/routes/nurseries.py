from flask import current_app, Blueprint, render_template, request, jsonify
from database import get_database
import json
from geopy.geocoders import GoogleV3
import uuid

nurseries = Blueprint('nurseries', __name__, static_folder='static')
db = get_database('nurseries')

geolocator = GoogleV3(api_key='AIzaSyChPWJLYBV1fmZPwST3-rHHZ3AkYIzHinY')

order_db = get_database('orders')
def getOrder(orderID):
    return order_db[orderID]

# Create a nursery in nurseries DB
@nurseries.route('/api/nurseries/create', methods=['POST'])
def createNursery():
    data = request.json
    print(data)
    nursery = {
        "name": data['name'],
        "latitude": "",
        "longitude": "",
        "address": data['address'],
        "city": data['city'],
        "state": data['state'],
        "zipcode": data['zipcode'],
        "inventory": {
            "red": 0,
            "black": 0,
            "white": 0
        },
        "phone": data['phone'],
        "website": data['website'],
        "picture": data['picture'],
        "policy": "",
        "orders": []
    }
    doc = db.create_document(nursery)
    nursery['_id'] = doc['_id']
    return jsonify(nursery)

def getAllNurseries():
    arr = list(map(lambda doc: doc, db))
    for i, nursery in enumerate(arr):
        locations = geolocator.reverse((nursery['latitude'], nursery['longitude']))
        arr[i]['address'] = ""
        if locations:
            arr[i]['address'] = locations[0]
    return arr

# Get all nurseries
@nurseries.route('/api/nurseries', methods=['GET'])
def getAllNurseriesRoute():
    return jsonify(getAllNurseries())

# Get nursery by nursery ID
@nurseries.route('/api/nurseries/<nurseryID>', methods=['GET'])
def getTree(nurseryID):
    return db[nurseryID]

# Check if nursery exists in nursery DB
@nurseries.route('/api/nurseries/check/<nurseryID>', methods=['GET'])
def checkIfNurseryExists(nurseryID):
    if nurseryID in db:
        return "true"
    return "false"

# Delete nursery from Nurseries DB
@nurseries.route('/api/nurseries/delete/<nurseryID>', methods=['GET'])
def deleteNursery(nurseryID):
    if nurseryID in db:
        nursery = db[nurseryID]
        nursery.delete()
        return "true"
    return "false"

# Add order ID to nursery
def addOrderToNursery(orderID, nurseryID):
    doc = db[nurseryID]
    doc['orders'].append(orderID)
    doc.save()
    return "true"

# Remove order ID from nursery
def removeOrderFromNursery(orderID, nurseryID):
    doc = db[nurseryID]
    doc['orders'].remove(orderID)
    doc.save()
    return "true"

@nurseries.route('/api/nurseries/order', methods=['POST'])
def order():
    data = request.json
    # if not enough, then false
    count = 0
    for key in data['items']:
        count += data['items'][key]
    if count <= 0:
        return "false"
    id = "a1c88d8676e67636307041af44b1b1d9"
    my_document = db[id]
    order_id = str(uuid.uuid4())
    order = {
        "_id": order_id,
        "userID": data['userID'],
        "items": [],
        "timestamp": "",
        "status": "order processing",
        "type": "pickup"
    }
    # for key in data['items'].keys():
    #     for i in range(data['items'][key]):
    #         if len(my_document['planted']) <= 0:
    #             return "not enough seedlings"
    #         else:
    #             order['items'].append(my_document['planted'].pop())
    my_document['orders'].append(order_id)
    print(my_document)
    my_document.save()
    return "true"

# Get all orders by nursery
@nurseries.route('/api/nurseries/<nurseryID>/orders', methods=['GET'])
def getOrdersByNurseries(nurseryID):
    doc = db[nurseryID]
    orders = []
    for orderID in doc['orders']:
        orders.append(getOrder(orderID))
    return jsonify(orders)
