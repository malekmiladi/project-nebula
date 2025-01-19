import os
from pathlib import Path

from dotenv import load_dotenv

DOT_ENV_FILE_PATH = Path(__file__).resolve().parent / ".env"
load_dotenv(DOT_ENV_FILE_PATH)

from fastapi import FastAPI
from .routers import user_data, vendor_data, metadata, instance_config

app = FastAPI()
app.include_router(user_data.router)
app.include_router(vendor_data.router)
app.include_router(metadata.router)
app.include_router(instance_config.router)

@app.get("/")
async def root():
    return {"_service": "cloud-init-datasource", "_version": 1.0}