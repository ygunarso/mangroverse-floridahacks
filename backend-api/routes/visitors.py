from flask import current_app, Blueprint, render_template, request, jsonify
from database import get_database

visitors = Blueprint('visitors', __name__, static_folder='static')
db = get_database('test')

@visitors.route('/')
def root():
    return current_app.send_static_file('index.html')

# /* Endpoint to greet and add a new visitor to database.
# * Send a POST request to localhost:8000/api/visitors with body
# * {
# *     "name": "Bob"
# * }
# */
@visitors.route('/api/visitors', methods=['GET'])
def get_visitor():
    return jsonify(list(map(lambda doc: doc['name'], db)))

# /**
#  * Endpoint to get a JSON array of all the visitors in the database
#  * REST API example:
#  * <code>
#  * GET http://localhost:8000/api/visitors
#  * </code>
#  *
#  * Response:
#  * [ "Bob", "Jane" ]
#  * @return An array of all the visitor names
#  */
@visitors.route('/api/visitors', methods=['POST'])
def put_visitor():
    user = request.json['name']
    data = {'name':user}
    my_document = db.create_document(data)
    data['_id'] = my_document['_id']
    return jsonify(data)
