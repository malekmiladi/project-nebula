import os

import couchdb

couch = couchdb.Server(f"http://{os.getenv('PROJECT_NEBULA_COUCHDB_USER')}:{os.getenv('PROJECT_NEBULA_COUCHDB_PASSWORD')}@{os.getenv('PROJECT_NEBULA_COUCHDB_HOSTNAME')}:{os.getenv('PROJECT_NEBULA_COUCHDB_PORT')}")
try:
    db = couch.create("cloud-init-config")
except couchdb.http.PreconditionFailed:
    db = couch["cloud-init-config"]