package io.je.utilities.beans;

import io.je.utilities.runtimeobject.JEObject;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "JEMethodCollection")
public class JEMethod extends JEObject {

    List<JEField> inputs;

    List<String> imports;

    String returnType;

    String code;

    String scope;

    boolean compiled;

    public JEMethod() {
    }

    public List<JEField> getInputs() {
        return inputs;
    }

    public void setInputs(List<JEField> inputs) {
        this.inputs = inputs;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isCompiled() {
        return compiled;
    }

    public void setCompiled(boolean compiled) {
        this.compiled = compiled;
    }
}
