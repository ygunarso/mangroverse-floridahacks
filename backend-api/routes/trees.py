from flask import current_app, Blueprint, render_template, request, jsonify
from database import get_database
import json
import qrcode
from io import BytesIO
import base64
from list import adjectives
import random
import time
from objectstorage import upload_image_bucket, get_cos_client
from routes.users import getUser, getLevel, getDateDiff, getDays, removeTreeFromUser

trees = Blueprint('trees', __name__, static_folder='static')
db = get_database('trees')

# user_db = get_database('users')
# def getUser(userID):
#     return user_db[userID]

bucket_name = "mangrove-tracker-bucket"
cos = get_cos_client()

# Create a tree in tree DB
@trees.route('/api/trees/create', methods=['POST'])
def createTreeRoute():
    return createTree(data['donor'], data['special'])

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
    doc = db.create_document(tree)
    tree['_id'] = doc['_id']
    print(tree)
    return jsonify(tree)

# Get all trees
@trees.route('/api/trees', methods=['GET'])
def getAllTrees():
    arr = list(map(lambda doc: doc, db))
    for i in range(len(arr)):
        arr[i]['qrcode'] = generateQRCode(arr[i]['_id'])
    return jsonify(arr)

def getTree(treeID):
    return db[treeID]

# Get tree by Tree ID
@trees.route('/api/trees/<treeID>', methods=['GET'])
def getTreeRoute(treeID):
    tree = getTree(treeID)
    donor = getUser(tree['donor'])
    tree['donorName'] = donor['firstName']
    tree['donorPicture'] = donor['picture']
    if tree['planter']:
        planter = getUser(tree['planter'])
        tree['planterName'] = planter['firstName']
        tree['planterPicture'] = planter['picture']
        tree['level'] = getLevel(tree['plantedTime'])
        tree['dateDiff'] = getDateDiff(tree['plantedTime'])
        tree['days'] = getDays(tree['plantedTime'])
    else:
        tree['planterName'] = ""
        tree['planterPicture'] = ""
        tree['level'] = 0
        tree['dateDiff'] = {
            "years": 0,
            "months": 0,
            "days": 0
        }
        tree['days'] = 0
    return tree

# Check if tree exists in Trees DB
@trees.route('/api/trees/check/<treeID>', methods=['GET'])
def checkIfTreeExists(treeID):
    if treeID in db:
        return "true"
    return "false"

# Delete tree from Trees DB
@trees.route('/api/trees/delete/<treeID>', methods=['GET'])
def deleteTree(treeID):
    if treeID in db:
        tree = db[treeID]
        # Remove from donor
        removeTreeFromUser(treeID, tree['donor'])
        tree.delete()
        return "true"
    return "false"

def plantTree(treeID, userID):
    # Change tree status
    doc = db[treeID]
    doc['status'] = "planted"
    # Change tree planter
    doc['planter'] = userID
    doc.save()
    return "true"

# Add contribution ID to tree
def addContributionToTree(contributionID, treeID):
    doc = db[treeID]
    doc['contributions'].append(contributionID)
    doc.save()
    return "true"

def generateQRCode(id):
    # Generate QR Code
    pil_img = qrcode.make(id).resize((150,150))
    print(type(pil_img))
    img_io = BytesIO()
    pil_img.save(img_io, 'PNG')
    img_base64 = base64.b64encode(img_io.getvalue()).decode("utf-8")
    return img_base64
