import os
import shutil
from uuid import uuid4
from fastapi import APIRouter, Depends, UploadFile, File, Form, HTTPException
from fastapi.responses import FileResponse
from sqlalchemy.orm import Session
from typing import Optional, List
from .. import models, schemas, database

router = APIRouter(prefix="/tasks", tags=["tasks"])

UPLOAD_DIR = "attachments"
os.makedirs(UPLOAD_DIR, exist_ok=True)

def get_db():
    db = database.SessionLocal()
    try:
        yield db
    finally:
        db.close()

def save_attachment_file(upload_file: UploadFile) -> str:
    file_ext = os.path.splitext(upload_file.filename)[1]
    unique_filename = f"{uuid4().hex}{file_ext}"
    file_path = os.path.join(UPLOAD_DIR, unique_filename)
    try:
        with open(file_path, "wb") as out_file:
            upload_file.file.seek(0)
            shutil.copyfileobj(upload_file.file, out_file)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"File error: {str(e)}")
    return unique_filename

# ✅ CREATE task
@router.post("/", response_model=schemas.Task)
def create_task(
    projID: int = Form(None),  # ✅ Foreign key required
    task_title: str = Form(...),
    task_comment: str = Form(None),
    assigned_to: str = Form(...),
    task_status: str = Form(...),
    task_attachment: UploadFile = File(None),
    db: Session = Depends(get_db)
):
    # Ensure project exists
    project = db.query(models.Project).filter(models.Project.projectID == projID).first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found")

    filename = save_attachment_file(task_attachment) if task_attachment else None
    new_task = models.Task(
        projID=projID,
        task_title=task_title,
        task_comment=task_comment,
        assigned_to=assigned_to,
        task_status=task_status,
        task_attachment=filename
    )
    if task_status.lower() == "done":
        from sqlalchemy.sql import func
        new_task.passed_at = func.now()

    db.add(new_task)
    db.commit()
    db.refresh(new_task)
    return new_task

# ✅ GET tasks with filters
@router.get("/", response_model=List[schemas.Task])
def get_tasks(
    taskID: Optional[int] = None,
    task_title: Optional[str] = None,
    assigned_to: Optional[str] = None,
    task_status: Optional[str] = None,
    projID: Optional[int] = None,
    db: Session = Depends(get_db)
):
    query = db.query(models.Task)
    if taskID is not None:
        query = query.filter(models.Task.taskID == taskID)
    if task_title is not None:
        query = query.filter(models.Task.task_title == task_title)
    if assigned_to is not None:
        query = query.filter(models.Task.assigned_to == assigned_to)
    if task_status is not None:
        query = query.filter(models.Task.task_status == task_status)
    if projID is not None:
        query = query.filter(models.Task.projID == projID)
    return query.all()

# ✅ UPDATE task
@router.put("/{task_id}", response_model=schemas.Task)
def update_task(
    task_id: int,
    projID: int = Form(None),
    task_title: str = Form(...),
    task_comment: str = Form(None),
    assigned_to: str = Form(...),
    task_status: str = Form(...),
    task_attachment: UploadFile = File(None),
    db: Session = Depends(get_db)
):
    task = db.query(models.Task).filter(models.Task.taskID == task_id).first()
    if not task:
        raise HTTPException(status_code=404, detail="Task not found")

    # Ensure project exists
    project = db.query(models.Project).filter(models.Project.projectID == projID).first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found")

    task.projID = projID
    task.task_title = task_title
    task.task_comment = task_comment
    task.assigned_to = assigned_to
    task.task_status = task_status

    if task_status.lower() == "done":
        from sqlalchemy.sql import func
        task.passed_at = func.now()

    if task_attachment:
        filename = save_attachment_file(task_attachment)
        task.task_attachment = filename

    db.commit()
    db.refresh(task)
    return task

# ✅ DELETE task
from sqlalchemy import or_

@router.delete("/{identifier}")
def delete_task(identifier: str, db: Session = Depends(get_db)):
    query = db.query(models.Task)
    if identifier.isdigit():
        task = query.filter(or_(models.Task.taskID == int(identifier),
                                models.Task.task_title == identifier)).first()
    else:
        task = query.filter(models.Task.task_title == identifier).first()
    if not task:
        raise HTTPException(status_code=404, detail="Task not found")
    db.delete(task)
    db.commit()
    return {"message": "Task deleted successfully"}
