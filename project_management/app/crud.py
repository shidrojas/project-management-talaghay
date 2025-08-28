from sqlalchemy.orm import Session
from sqlalchemy import func
from . import models, schemas

def get_tasks(db: Session, skip: int = 0, limit: int = 100):
    return db.query(models.Task).offset(skip).limit(limit).all()

def create_task(db: Session, task_data: dict):
    db_task = models.Task(
        task_title=task_data["task_title"],
        task_comment=task_data.get("task_comment"),
        assigned_to=task_data["assigned_to"],
        task_status=task_data["task_status"],
        task_attachment=task_data.get("task_attachment")  # ✅ This line now saves the file
    )
    db.add(db_task)
    db.commit()
    db.refresh(db_task)
    return db_task

def update_task_status(db: Session, task_id: int, new_status: str):
    db_task = db.query(models.Task).filter(models.Task.taskID == task_id).first()
    if db_task:
        db_task.task_status = new_status
        if new_status.lower() == "done":
            db_task.passed_at = func.now()
        db.commit()
        db.refresh(db_task)
    return db_task
