from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from .routers import tasks, projects   # ✅ include projects
from .database import engine, Base
import os

# Create tables
Base.metadata.create_all(bind=engine)

app = FastAPI()

# CORS configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Static files for attachments
UPLOAD_DIR = "attachments"
os.makedirs(UPLOAD_DIR, exist_ok=True)
app.mount("/attachments", StaticFiles(directory=UPLOAD_DIR), name="attachments")

# Routers
app.include_router(tasks.router)
app.include_router(projects.router)   # ✅ added

@app.get("/")
def read_root():
    return {"message": "Welcome to the Task & Project Management API"}
