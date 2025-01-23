import json
import os
import uuid

import couchdb
from confluent_kafka import Consumer, Producer

from dotenv import load_dotenv

def main():
    load_dotenv(".env")
    couch = couchdb.Server(f"http://{os.getenv('PROJECT_NEBULA_COUCHDB_USER')}:{os.getenv('PROJECT_NEBULA_COUCHDB_PASSWORD')}@{os.getenv('PROJECT_NEBULA_COUCHDB_HOSTNAME')}:{os.getenv('PROJECT_NEBULA_COUCHDB_PORT')}")
    try:
        db = couch.create("cloud-init-config")
    except couchdb.http.PreconditionFailed:
        db = couch["cloud-init-config"]

    conf = {
        'bootstrap.servers': f"{os.getenv('KAFKA_SERVER_HOSTNAME')}:{os.getenv('KAFKA_SERVER_PORT')}",
        'group.id': os.getenv("PROJECT_NEBULA_MESSAGE_QUEUE_GROUP_ID")
    }

    consumer_topic = os.getenv("PROJECT_NEBULA_MESSAGE_QUEUE_CONSUMER_TOPIC")
    producer_topic = os.getenv("PROJECT_NEBULA_MESSAGE_QUEUE_PRODUCER_TOPIC")
    consumer = Consumer(conf)
    consumer.subscribe([consumer_topic])

    producer = Producer(conf)
    while True:
        try:
            msg = consumer.poll(1.0)
            if msg is None:
                continue
            json_message = json.loads(msg.value())
            print(json_message["config"])
            instance_id = json_message["id"]
            auth_token = str(uuid.uuid4())
            instance_config = json.loads(json_message["config"])
            instance_config.update({"_id": uuid.UUID(instance_id).hex})
            instance_config.update({"auth_token": auth_token})
            db.save(instance_config)
            producer.produce(topic=producer_topic, value=f'{{"id": "{instance_id}", "authToken": "{auth_token}"}}')
        except KeyboardInterrupt:
            break
    consumer.close()


if __name__ == "__main__":
    main()
