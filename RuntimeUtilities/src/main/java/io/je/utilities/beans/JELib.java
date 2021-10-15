package io.je.utilities.beans;

import io.je.utilities.runtimeobject.JEObject;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "JELibCollection")
public class JELib extends JEObject {

    private String filePath;

    private LibScope scope;


    public JELib()  {}
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LibScope getScope() {
        return scope;
    }

    public void setScope(LibScope scope) {
        this.scope = scope;
    }

}
