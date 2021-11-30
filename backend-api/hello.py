from flask import Flask
from routes.visitors import visitors
from routes.trees import trees
from routes.nurseries import nurseries
from routes.users import users
from routes.zones import zones
from routes.orders import orders
from routes.contributions import contributions
from database import get_client
import atexit
import os

def create_app():
    app = Flask(__name__, static_url_path='')
    app.register_blueprint(visitors)
    app.register_blueprint(trees)
    app.register_blueprint(nurseries)
    app.register_blueprint(contributions)
    app.register_blueprint(users)
    app.register_blueprint(zones)
    app.register_blueprint(orders)
    return app

client = get_client()

# On IBM Cloud Cloud Foundry, get the port number from the environment variable PORT
# When running this app on the local machine, default the port to 8000
port = int(os.getenv('PORT', 8000))

@atexit.register
def shutdown():
    if client:
        client.disconnect()

if __name__ == '__main__':
    app = create_app()
    app.run(host='0.0.0.0', port=port, debug=True)
