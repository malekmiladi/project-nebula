import json
import os
import uuid

from confluent_kafka import Consumer
from dotenv import load_dotenv

from db.databaseConnection import db


def main():
    load_dotenv(".env")
    conf = {
        'bootstrap.servers': f"{os.getenv('KAFKA_SERVER_HOSTNAME'):{os.getenv('KAFKA_SERVER_PORT')}}",
        'group.id': os.getenv("PROJECT_NEBULA_MESSAGE_QUEUE_GROUP_ID")
    }
    topic = os.getenv("PROJECT_NEBULA_MESSAGE_QUEUE_TOPIC")
    consumer = Consumer(conf)
    consumer.subscribe([topic])
    while True:
        try:
            msg = consumer.poll(1.0)
            if msg is None:
                continue
            json_message = json.loads(msg.value())
            instance_id = json_message["instanceId"]
            instance_config = json.loads(json_message["instanceConfig"])
            instance_config.update({"_id": uuid.UUID(instance_id).hex})
            db.save(instance_config)
        except KeyboardInterrupt:
            break
    consumer.close()


if __name__ == "__main__":
    main()
