from flask import current_app, Blueprint, render_template, request, jsonify
from database import get_client, get_database
import time
from routes.nurseries import addOrderToNursery, removeOrderFromNursery
from routes.users import addOrderToUser, removeOrderFromUser
import json

orders = Blueprint('orders', __name__, static_folder='static')
db = get_database('orders')

# Create an order in orders DB
@orders.route('/api/orders/create', methods=['POST'])
def createOrder():
    data = request.json
    print(data)
    order = {
        "nurseryID": data['nurseryID'],
        "userID": data['userID'],
        "items": data['items'],
        "timestamp": time.time(),
        "status": "processing",
        "type": data['type']
    }
    # Add order to orders DB
    doc = db.create_document(order)
    order['_id'] = doc['_id']
    # Add orderID to user
    addOrderToUser(order['_id'], order['userID'])
    # Add orderID to nursery
    addOrderToNursery(order['_id'], order['nurseryID'])
    return jsonify(order)

# Get all orders from orders DB
@orders.route('/api/orders', methods=['GET'])
def getAllOrders():
    arr = list(map(lambda doc: doc, db))
    return jsonify(arr)

def getOrder(orderID):
    return db[orderID]

# Get order by Order ID
@orders.route('/api/orders/<orderID>', methods=['GET'])
def getOrderRoute(orderID):
    return getOrder(orderID)

# Check if order exists in Orders DB
@orders.route('/api/orders/check/<orderID>', methods=['GET'])
def checkIfOrderExists(orderID):
    if orderID in db:
        return "true"
    return "false"

# Delete order from Orders DB
@orders.route('/api/orders/delete/<orderID>', methods=['GET'])
def deleteOrder(orderID):
    if orderID in db:
        order = db[orderID]
        # Remove orderID from user
        removeOrderFromUser(order['_id'], order['userID'])
        # Remove orderID from nursery
        removeOrderFromNursery(order['_id'], order['nurseryID'])
        # Remove order from orders DB
        order.delete()
        return "true"
    return "false"

# Change order status
@orders.route('/api/orders/changestatus', methods=['POST'])
def changeStatus():
    data = request.json
    print(data)
    orderID, status = data['orderID'], data['status']
    doc = db[orderID]
    doc['status'] = status
    doc.save()
    return "true"
