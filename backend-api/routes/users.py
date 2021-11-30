from flask import current_app, Blueprint, render_template, request, jsonify
from database import get_client, get_database

import time
import datetime
import dateutil.relativedelta
import random
from list import adjectives

# utils
order_db = get_database('orders')
def getOrder(orderID):
    return order_db[orderID]
contribute_db = get_database('contributions')
def getContribution(contributionID):
    return contribute_db[contributionID]
tree_db = get_database('trees')
def getTree(treeID):
    return tree_db[treeID]
def getTreeShort(treeID):
    tree = getTree(treeID)
    pictureURL = ""
    level = 0
    days = 0
    if tree['planter']:
        planter = getUser(tree['planter'])
        pictureURL = planter['picture']
        level = getLevel(tree['plantedTime'])
        days = getDays(tree['plantedTime'])
    tree_short = {
        "_id": tree['_id'],
        "name": tree['name'],
        "level": level,
        "species": tree['species'],
        "special": tree['special'],
        "days": days,
        "profilePicture": pictureURL
    }
    return tree_short
def createTree(donor, special):
    tree = {
        "name": random.choice(adjectives).capitalize() + " Tree",
        "donatedTime": time.time(),
        "plantedTime": 0,
        "latitude": 0,
        "longitude": 0,
        "city": "",
        "state": "",
        "status": "donated",
        "nursery": "",
        "zone": "",
        "species": "",
        "special": special,
        "donor": donor,
        "planter": "",
        "pictures": [],
        "widths": [],
        "heights": []
    }
    doc = tree_db.create_document(tree)
    tree['_id'] = doc['_id']
    print(tree)
    return jsonify(tree)


users = Blueprint('users', __name__, static_folder='static')
db = get_database('users')

@users.route('/api/users/create', methods=['POST'])
def createUser():
    data = request.json
    print(data)
    user = {
        "firstName": data['firstName'],
        "lastName": data['lastName'],
        "email": data['email'],
        "city": data['city'],
        "state": data['state'],
        "donated": [],
        "planted": [],
        "collected": [],
        "orders": []
    }
    doc = db.create_document(user)
    user['_id'] = doc['_id']
    return jsonify(user)

@users.route('/api/users', methods=['GET'])
def getAllUsers():
    return 'OK'

def getUser(userID):
    return db[userID]

@users.route('/api/users/<userID>', methods=['GET'])
def getUserRoute(userID):
    return getUser(userID)

@users.route('/api/users/name/<name>', methods=['GET'])
def getUserByName(name):
    selector = {'firstName':name}
    docs = db.get_query_result(selector)
    for doc in docs:
        print(doc)
    return jsonify(list(docs))

# Add order ID to user
def addOrderToUser(orderID, userID):
    doc = db[userID]
    doc['orders'].append(orderID)
    doc.save()
    return "true"

# Remove order ID from user
def removeOrderFromUser(orderID, userID):
    doc = db[userID]
    doc['orders'].remove(orderID)
    doc.save()
    return "true"

# Add contribution ID to user
def addContributionToUser(contributionID, userID):
    doc = db[userID]
    doc['contributions'].append(contributionID)
    doc.save()
    return "true"

# levels = [5, 20, 50, 150, 365, 730]
# Cumulative
levels = [5, 15, 30, 100, 215, 365]


# Get Level from timestamp
def getLevel(timestamp):
    days = getDays(timestamp)
    # Level
    level = 0
    while days > 0:
        days -= levels[level]
        level+=1
    return level

def getDays(timestamp):
    dt1 = datetime.datetime.fromtimestamp(timestamp) # 1973-11-29 22:33:09
    dt2 = datetime.datetime.fromtimestamp(time.time()) # 1977-06-07 23:44:50
    delta = dt2 - dt1
    return delta.days

def getDateDiff(timestamp):
    # Difference in date
    dt1 = datetime.datetime.fromtimestamp(timestamp) # 1973-11-29 22:33:09
    dt2 = datetime.datetime.fromtimestamp(time.time()) # 1977-06-07 23:44:50
    rd = dateutil.relativedelta.relativedelta(dt2, dt1)
    dateDiff = {
        "years": rd.years,
        "months": rd.months,
        "days": rd.days
    }
    return dateDiff

# Get all trees by user for treeView
@users.route('/api/users/<userID>/trees_short', methods=['GET'])
def getTreesByUserShort(userID):
    doc = db[userID]
    data = {
        "userID": userID,
        "username": doc['firstName'],
        "profilePicture": doc['picture'],
        "donated": [],
        "planted": [],
        "collected": []
    }
    for id in doc['donated']:
        tree = getTreeShort(id)
        data['donated'].append(tree)
    for id in doc['planted']:
        tree = getTreeShort(id)
        data['planted'].append(tree)
    for id in doc['collected']:
        tree = getTreeShort(id)
        data['collected'].append(tree)
    return jsonify(data)

# Donate a specific number of mangrove trees
@users.route('/api/users/donate', methods=['POST'])
def donate():
    data = request.json
    userID, amount = data['userID'], data['amount']
    doc = db[userID]
    special_rate = 0.3
    count = {
        "special": 0,
        "normal": 0
    }
    for i in range(amount):
        special = "true" if random.random() < special_rate else "false"
        if special == "true":
            count['special']+=1
        else:
            count['normal']+=1
        # Create a tree instance
        tree = createTree(userID, special).json
        # Update user donated array
        doc['donated'].append(tree['_id'])
        doc.save()
    return jsonify(count)

# Get all contributions by user
@users.route('/api/users/<userID>/contributions', methods=['GET'])
def getContributionsByUser(userID):
    doc = db[userID]
    contributions = []
    for id in doc['contributions']:
        contribution = getContribution(id)
        contribution['treeName'] = getTree(contribution['treeID'])['nickname']
        contributions.append(contribution)
    return jsonify(contributions)

# Get all orders by user
@users.route('/api/users/<userID>/orders', methods=['GET'])
def getOrdersByUser(userID):
    doc = db[userID]
    orders = []
    for orderID in doc['orders']:
        orders.append(getOrder(orderID))
    return jsonify(orders)

def removeTreeFromUser(treeID, userID):
    doc = db[userID]
    if treeID in doc['donated']:
        print("something")
        doc['donated'].remove(treeID)
        doc.save()
        return "true"
    return "false"
