from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime

# ---------- Project Schemas ----------
class ProjectBase(BaseModel):
    project_title: str
    project_desc: str
    project_pass: Optional[str] = None  # plain text (will be hashed before saving)
    project_status: Optional[str] = "Active"  # default set automatically

class ProjectCreate(ProjectBase):
    pass

class Project(ProjectBase):
    projectID: int
    created_at: datetime
    finished_at: Optional[datetime] = None

    class Config:
        orm_mode = True


# ---------- Task Schemas ----------
class TaskBase(BaseModel):
    projID: int   # ✅ Foreign key link to Project
    task_title: str
    task_comment: Optional[str] = None
    assigned_to: str
    task_status: str
    task_attachment: Optional[str] = None  # filename

class Task(TaskBase):
    taskID: int
    created_at: datetime
    passed_at: Optional[datetime] = None

    class Config:
        orm_mode = True
