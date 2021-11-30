from flask import current_app, Blueprint, render_template, request, jsonify
from database import get_database
import time
from routes.trees import plantTree, addContributionToTree, getTree
from routes.users import addContributionToUser
from objectstorage import upload_image_bucket, get_cos_client

contributions = Blueprint('contributions', __name__, static_folder='static')
db = get_database('contributions')
cos_base_url = "https://mangrove-tracker-bucket.s3.us-south.cloud-object-storage.appdomain.cloud/"
bucket_name = "mangrove-tracker-bucket"
cos = get_cos_client()

# Create a contribution in contribution DB
@contributions.route('/api/contributions/create', methods=['POST'])
def createContribution():
    # TODO: If you already plant, you can't plant again
    data = request.json
    print(data)
    contribution = {
        "userID": data['userID'],
        "treeID": data['treeID'],
        "timestamp": time.time(),
        "type": data['type'],
        "height": data['height'],
        "picture": data['picture'],
        "description": data['description'],
        # "measurements": data['measurements']
        "measurements": {}
    }
    # Add contribution to DB
    doc = db.create_document(contribution)
    contribution['_id'] = doc['_id']
    # Plant tree if contribution is plant
    if data['type'] == 'plant':
        plantTree(data['treeID'], data['userID'])
    # Add contributionID to tree
    addContributionToTree(contribution['_id'], contribution['treeID'])
    # Add contributionID to user
    addContributionToUser(contribution['_id'], contribution['userID'])
    return jsonify(contribution)

# Get all contributions
@contributions.route('/api/contributions', methods=['GET'])
def getAllContributions():
    arr = list(map(lambda doc: doc, db))
    return jsonify(arr)

def getContribution(contributionID):
    return db[contributionID]

# Get contribution by Contribution ID
@contributions.route('/api/contributions/<contributionID>', methods=['GET'])
def getContributionRoute(contributionID):
    return getContribution(contributionID)

# Check if contribution exists in Contributions DB
@contributions.route('/api/contributions/check/<contributionID>', methods=['GET'])
def checkIfContributionExists(contributionID):
    if contributionID in db:
        return "true"
    return "false"

# Delete contribution from Contributions DB
@contributions.route('/api/contributions/delete/<contributionID>', methods=['GET'])
def deleteContribution(contributionID):
    if contributionID in db:
        contribution = db[contributionID]
        contribution.delete()
        return "true"
    return "false"

@contributions.route('/api/contributions/upload/<treeID>', methods=['POST'])
def uploadImage(treeID):
    # Upload image to IBM COS
    img = request.files["file"]
    timestamp = time.time()
    upload_image_bucket(bucket_name, f'{treeID}/{timestamp}.jpeg', img)
    # Return URL
    return cos_base_url+f'{treeID}/{timestamp}.jpeg'
