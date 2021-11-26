package io.je.classbuilder.models;

import java.util.ArrayList;
import java.util.List;

public class MethodModel {
    String methodName;

    List<FieldModel> inputs;

    List<String> imports;

    String returnType;

    String methodScope;

    String methodVisibility;

    String code;

    String id;

    String createdBy;

    String modifiedBy;

    String createdAt;

    String modifiedAt;

    public MethodModel() {inputs = new ArrayList<>(); imports = new ArrayList<>();}
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        if (methodName != null)
            this.methodName = methodName;
    }

    public List<FieldModel> getInputs() {
        return inputs;
    }

    public void setInputs(List<FieldModel> inputs) {
        if (inputs != null)
            this.inputs = inputs;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        if (returnType != null)
            this.returnType = returnType;
    }

    public String getMethodScope() {
        return methodScope;
    }

    public void setMethodScope(String methodScope) {
        if (methodScope != null)
            this.methodScope = methodScope;
    }

    public String getMethodVisibility() {
        return methodVisibility;
    }

    public void setMethodVisibility(String methodVisibility) {
        if (methodVisibility != null)
            this.methodVisibility = methodVisibility;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if (code != null)
            this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id != null)
            this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        if (createdBy != null)
            this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        if (modifiedBy != null)
            this.modifiedBy = modifiedBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        if (createdAt != null)
            this.createdAt = createdAt;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        if (modifiedAt != null)
            this.modifiedAt = modifiedAt;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        if (imports != null)
            this.imports = imports;
    }
}
