package com.robotlife.compiler.inject;

/**
 *
 */
public class ClassGenerator {

    private final String classPackage;
    private final String className;
    private final String targetClass;
    private final String FIELD_MAP_KEY_SUFFIX = "FIELD_MAP_KEY_";
    private final String FIELD_NAME_LIST = "FIELD_NAME_LIST";
    private String[] fieldNameArray = null;

    public ClassGenerator(String classPackage, String className, String targetClass, String[] fieldNameArray) {
        this.classPackage = classPackage;
        this.className = className;
        this.targetClass = targetClass;
        this.fieldNameArray = fieldNameArray;
    }

    public String getFqcn() {
        return classPackage + "." + className;
    }

    public String brewJava() throws Exception{
        StringBuilder builder = new StringBuilder("package " + this.classPackage + ";\n");
        builder.append("import java.util.Map;\n");
        builder.append("import java.util.HashMap;\n");
        builder.append("import com.robotlife.compiler.inject.BeanToMapI;\n");

        String action = "extends";

        builder.append("public class " + this.className + " " + action + " " + this.targetClass + " implements " + BeanToMapI.class.getSimpleName() + "<" + targetClass + ">" + " { \n");

        createField(fieldNameArray, builder);
        createGetMethod(builder);
        createToMapMethod(builder);

        builder.append(" public " + this.className + "()" + " { \n" + " }\n");
        builder.append(" }\n");

        return builder.toString();
    }

    private void createField(String[] fieldNameArray, StringBuilder builder) {
        if (fieldNameArray != null) {
            for (int i = 0; i < fieldNameArray.length; i++) {
                String fieldName = fieldNameArray[i];
                fieldNameArray[i] = fieldName;
                builder.append("public static final String " + FIELD_MAP_KEY_SUFFIX + fieldName.toUpperCase() + "=" + "\"" + fieldName  + "\"" + ";");
            }
        }

        if (fieldNameArray != null) {
            builder.append("public static final String[] " + FIELD_NAME_LIST + "=" + "{");
            for (int i = 0; i < fieldNameArray.length; i++) {
                if (i != (fieldNameArray.length - 1)) {
                    builder.append("\"" + fieldNameArray[i] + "\"" + ",");
                } else {
                    builder.append("\"" + fieldNameArray[i] + "\"");
                }
            }
            builder.append("};");
        }
    }

    private void createGetMethod(StringBuilder builder) {
        if (fieldNameArray != null) {
            for (int i = 0; i < fieldNameArray.length; i++) {
                String fieldName = fieldNameArray[i];
                int position = targetClass.lastIndexOf('.');
                String paramName = targetClass.substring(position + 1).toLowerCase();
                builder.append("public String " + createGetMethodName(fieldName) + "(" + targetClass + " " + paramName + ")" + "{");
                builder.append("return String.valueOf(" + paramName + "." + createGetMethodName(fieldName) +"()");
                builder.append(");");
                builder.append("}");
            }
        }
    }

    private String createGetMethodName(String name) {
        return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private void createToMapMethod(StringBuilder builder) {
        int position = targetClass.lastIndexOf('.');
        String paramName = targetClass.substring(position + 1).toLowerCase();

        builder.append("@Override public Map<String, String> toMap(" + targetClass + " " + paramName +") {");
        if (fieldNameArray != null && fieldNameArray.length > 0) {
            String paramsMapName = "paramsMap";
            builder.append("Map<String, String> " + paramsMapName + " = new HashMap<String, String>();");
            for (int i = 0; i < fieldNameArray.length; i++) {
                String fieldName = fieldNameArray[i];
                builder.append(paramsMapName + ".put(" + "\"" + fieldName + "\""+ "," + createGetMethodName(fieldName) + "(" + paramName + ")" + ");");
            }
            builder.append("return " + paramsMapName + ";");
        } else {
            builder.append("return null;");
        }
        builder.append("}");
    }

}
