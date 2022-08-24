package io.je.runtime.models;

import io.je.utilities.beans.ClassAuthor;

public class ClassModel {

    String classId;
    String className;
    String classPath;
    ClassAuthor classAuthor;


    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public ClassAuthor getClassAuthor() {
        return classAuthor;
    }

    public void setClassAuthor(ClassAuthor classAuthor) {
        this.classAuthor = classAuthor;
    }

}
