from fastapi import APIRouter, Response
from ..db.db_connection import db
from ..helpers.yaml_dumper import YamlDumper
import yaml
import uuid

router = APIRouter(
    prefix="/instance",
)


@router.get("/{instance_id}/meta-data")
async def get_instance_meta_data(instance_id: str):

    meta_data_query = {
        "selector": {
            "_id": {
                "$eq": uuid.UUID(instance_id).hex
            }
        },
        "fields": [
            "meta-data"
        ]
    }
    meta_data_query_result = list(db.find(meta_data_query))
    meta_data = meta_data_query_result[0]["meta-data"]

    return Response(
        content=yaml.dump(
            meta_data,
            indent=2,
            Dumper=YamlDumper
        ),
        media_type="text/plain"
    )
