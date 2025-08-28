from fastapi import APIRouter, Depends, HTTPException, Body, Query
from sqlalchemy.orm import Session
from passlib.context import CryptContext
from typing import Optional, List
from .. import models, database, schemas

router = APIRouter(prefix="/projects", tags=["projects"])

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

def get_db():
    db = database.SessionLocal()
    try:
        yield db
    finally:
        db.close()

# ✅ Create project (accepts both JSON body and query params)
@router.post("/", response_model=schemas.Project)
def create_project(
    project_title: str = Query(..., description="Project title"),
    project_desc: str = Query(..., description="Project description"),
    project_pass: Optional[str] = Query(None, description="Password for project"),
    db: Session = Depends(get_db),
    project_body: Optional[schemas.ProjectCreate] = Body(None)
):
    # If JSON body is provided, use that
    if project_body:
        project_title = project_body.project_title
        project_desc = project_body.project_desc
        project_pass = project_body.project_pass

    # Hash password if provided
    hashed_pass = pwd_context.hash(project_pass) if project_pass else None

    new_project = models.Project(
        project_title=project_title,
        project_desc=project_desc,
        project_pass=hashed_pass,
        project_status="Active"   # always Active by default
    )

    db.add(new_project)
    db.commit()
    db.refresh(new_project)
    return new_project



# ✅ Get projects (all or with filters)
@router.get("/", response_model=List[schemas.Project])
def get_projects(
    projectID: Optional[int] = Query(None),
    project_title: Optional[str] = Query(None),
    project_desc: Optional[str] = Query(None),
    project_status: Optional[str] = Query(None),
    db: Session = Depends(get_db)
):
    query = db.query(models.Project)

    if projectID is not None:
        query = query.filter(models.Project.projectID == projectID)
    if project_title is not None:
        query = query.filter(models.Project.project_title.ilike(f"%{project_title}%"))
    if project_desc is not None:
        query = query.filter(models.Project.project_desc.ilike(f"%{project_desc}%"))
    if project_status is not None:
        query = query.filter(models.Project.project_status == project_status)

    results = query.all()

    if not results:
        raise HTTPException(status_code=404, detail="No project found with given filter")

    return results

# ✅ Update project
@router.put("/{project_id}", response_model=schemas.Project)
def update_project(
    project_id: int,
    project_title: Optional[str] = Query(None),
    project_desc: Optional[str] = Query(None),
    project_status: Optional[str] = Query(None),
    project_pass: Optional[str] = Query(None),
    db: Session = Depends(get_db)
):
    project = db.query(models.Project).filter(models.Project.projectID == project_id).first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found")

    if project_title is not None:
        project.project_title = project_title
    if project_desc is not None:
        project.project_desc = project_desc
    if project_status is not None:
        project.project_status = project_status
    if project_pass is not None:
        project.project_pass = pwd_context.hash(project_pass)

    db.commit()
    db.refresh(project)
    return project


# ✅ Delete project
@router.delete("/{project_id}")
def delete_project(project_id: int, db: Session = Depends(get_db)):
    project = db.query(models.Project).filter(models.Project.projectID == project_id).first()
    if not project:
        raise HTTPException(status_code=404, detail="Project not found")

    db.delete(project)
    db.commit()
    return {"message": "Project deleted successfully"}
