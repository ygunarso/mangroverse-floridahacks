from flask import current_app, Blueprint, render_template, request, jsonify
from database import get_database
import time
import csv
import pandas as pd
import random
from list import adjectives


zones = Blueprint('zones', __name__, static_folder='static')
db = get_database('zones')

# Seed zones in zone DB
@zones.route('/api/zones/seed', methods=['GET'])
def seedZones():
    print("test")
    col_list = ['plot_name', 'lat1', 'lon1']
    df = pd.read_csv("Florida_tree_measure.csv", usecols=col_list)
    df = df.drop_duplicates()
    for index, row in df.iterrows():
        zone = {
            "name": random.choice(adjectives).capitalize() + " Zone " + str(index),
            "plot_name": row['plot_name'],
            "latitude": row['lat1'],
            "longitude": row['lon1'],
            "city": '',
            "state": '',
            "radius": 500, # meters
            "treesPlanted": [],
            "capacity": 100,
            "preservedBy": ''
        }
        doc = db.create_document(zone)
        zone['_id'] = doc['_id']
    return "true"

def createZone(data):
    zone = {
        "name": random.choice(adjectives).capitalize() + " Zone",
        "latitude": data['latitude'],
        "longitude": data['longitude'],
        "city": data['city'],
        "state": data['state'],
        "radius": data['radius'],
        "treesPlanted": [],
        "capacity": data['capacity'],
        "preservedBy": data['preservedBy']
    }
    doc = db.create_document(zone)
    zone['_id'] = doc['_id']
    print(zone)
    return jsonify(zone)

# Create a zone in zone DB
@zones.route('/api/zones/create', methods=['POST'])
def createZoneRoute():
    data = request.json
    return createZone(data)

# Get all zones
@zones.route('/api/zones', methods=['GET'])
def getAllZones():
    arr = list(map(lambda doc: doc, db))
    return jsonify(arr)

# Get zone by Zone ID
@zones.route('/api/zones/<zoneID>', methods=['GET'])
def getZone(zoneID):
    return db[zoneID]

# Check if zone exists in Zones DB
@zones.route('/api/zones/check/<zoneID>', methods=['GET'])
def checkIfZoneExists(zoneID):
    if zoneID in db:
        return "true"
    return "false"

# Delete zone from Zones DB
@zones.route('/api/zones/delete/<zoneID>', methods=['GET'])
def deleteZone(zoneID):
    if zoneID in db:
        zone = db[zoneID]
        zone.delete()
        return "true"
    return "false"
