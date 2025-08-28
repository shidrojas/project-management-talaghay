from sqlalchemy import Column, Integer, String, Text, TIMESTAMP, ForeignKey
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from .database import Base

class Project(Base):
    __tablename__ = "project"

    projectID = Column(Integer, primary_key=True, index=True, autoincrement=True)
    project_title = Column(String(100), nullable=False)
    project_desc = Column(String(100), nullable=False)
    project_pass = Column(String(100), nullable=True)
    created_at = Column(TIMESTAMP, server_default=func.now())
    finished_at = Column(TIMESTAMP(6), nullable=True)
    project_status = Column(String(50), nullable=False)

    # One-to-Many relationship (Project → Tasks)
    tasks = relationship("Task", back_populates="project", cascade="all, delete")


class Task(Base):
    __tablename__ = "task"

    taskID = Column(Integer, primary_key=True, index=True, autoincrement=True)
    projID = Column(Integer, ForeignKey("project.projectID"), nullable=False)
    task_title = Column(String(100), nullable=False)
    task_comment = Column(Text, nullable=True)
    task_attachment = Column(String(255), nullable=True)
    created_at = Column(TIMESTAMP, server_default=func.now())
    passed_at = Column(TIMESTAMP(6), nullable=True)
    assigned_to = Column(String(100), nullable=False)
    task_status = Column(String(100), nullable=False)

    # Many-to-One relationship (Task → Project)
    project = relationship("Project", back_populates="tasks")
