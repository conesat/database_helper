package com.hg.idea.plugins.util;

import com.hg.idea.plugins.model.EntityItem;
import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DataType;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.JBIterable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GenerateHelper {
    private static String tableName = "";
    private static String modelSrc, modelPkg, mapperSrc, mapperPkg, xmlSrc;
    private static boolean lombok = false, toString = false, swagger = false, mapper = false;
    private static List<EntityItem> pKeys = new ArrayList<>();

    public static class Builder {
        public Builder modelSrc(String modelSrc) {
            GenerateHelper.modelSrc = modelSrc;
            return this;
        }

        public Builder modelPkg(String modelPkg) {
            GenerateHelper.modelPkg = modelPkg;
            return this;
        }

        public Builder mapperSrc(String mapperSrc) {
            GenerateHelper.mapperSrc = mapperSrc;
            return this;
        }

        public Builder mapperPkg(String mapperPkg) {
            GenerateHelper.mapperPkg = mapperPkg;
            return this;
        }

        public Builder xmlSrc(String xmlSrc) {
            GenerateHelper.xmlSrc = xmlSrc;
            return this;
        }

        public Builder lombok(boolean lombok) {
            GenerateHelper.lombok = lombok;
            return this;
        }

        public Builder toString(boolean toString) {
            GenerateHelper.toString = toString;
            return this;
        }

        public Builder swagger(boolean swagger) {
            GenerateHelper.swagger = swagger;
            return this;
        }

        public Builder mapper(boolean mapper) {
            GenerateHelper.mapper = mapper;
            return this;
        }

        public void buderEntity(PsiElement psiElement) {
            GenerateHelper.buderEntity(psiElement);
        }
    }

    private static void buderEntity(PsiElement psiElement) {
        pKeys.clear();
        DbTable dbTable = (DbTable) psiElement;
        JBIterable<? extends DasColumn> columnsIter = DasUtil.getColumns(dbTable);
        List<? extends DasColumn> dasColumns = columnsIter.toList();
        List<EntityItem> entityItems = new ArrayList<>();
        for (DasColumn dasColumn : dasColumns) {
            DataType dataType = dasColumn.getDataType();
            EntityItem entityItem = new EntityItem();
            entityItem.setSqlName(dasColumn.getName());
            entityItem.setName(toJavaTypeName(dasColumn.getName()));
            entityItem.setSqlType(dataType.typeName);
            entityItem.setType(getType(dataType.typeName));
            entityItem.setRemarks(dasColumn.getComment());
            if (DasUtil.isPrimary(dasColumn)) {
                entityItem.setPkey(true);
                pKeys.add(entityItem);
            }
            entityItems.add(entityItem);
        }
        tableName = dbTable.getName();
        String className = toClassName(dbTable.getName());
        builderClass(className, entityItems);
        if (mapper) {
            builderMapper(className, entityItems);
            builderXml(className, entityItems);
        }
    }


    private static String toClassName(String name) {
        name = toJavaTypeName(name);
        name = (name.charAt(0) + "").toUpperCase() + name.substring(1);
        return name;
    }

    private static String toJavaTypeName(String name) {
        while (name.indexOf("_") >= 0) {
            int pos = name.indexOf("_");
            if (pos < name.length() - 1) {
                char r = name.charAt(pos + 1);
                name = name.replace("_" + r, (r + "").toUpperCase());
            }
        }
        return name;
    }


    private static String getType(String type) {
        switch (type) {
            case "CHAR":
                return "String";
            case "VARCHAR":
                return "String";
            case "LONGVARCHAR":
                return "String";
            case "NUMERIC":
                return "java.math.BigDecimal";
            case "DECIMAL":
                return "java.math.BigDecimal";
            case "BIT":
                return "boolean";
            case "BOOLEAN":
                return "boolean";
            case "TINYINT":
                return "byte";
            case "SMALLINT":
                return "short";
            case "INTEGER":
                return "INTEGER";
            case "BIGINT":
                return "long";
            case "REAL":
                return "float";
            case "FLOAT":
                return "double";
            case "BINARY":
                return "byte[]";
            case "VARBINARY":
                return "byte[]";
            case "LONGVARBINARY":
                return "byte[]";
            case "DATE":
                return "java.sql.Date";
            case "TIME":
                return "java.sql.Time";
            case "TIMESTAMP":
                return "java.sql.Timestamp";
            case "CLOB":
                return "Clob";
            case "BLOB":
                return "Blob";
            case "ARRAY":
                return "Array";
            case "DISTINCT":
                return "mapping of underlying type";
            case "STRUCT":
                return "Struct";
            case "REF":
                return "Ref";
            case "DATALINK":
                return "java.net.URL[color = red][ / color]";
        }
        return "String";
    }

    private static void builderClass(String className, List<EntityItem> entityItems) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package " + modelPkg + ";\n\n");
        StringBuilder methed = new StringBuilder();
        StringBuilder toStringBuilder = new StringBuilder();
        if (lombok) {
            stringBuilder.append("import lombok.Data;\n");
        }
        if (swagger) {
            stringBuilder.append("import io.swagger.annotations.ApiModel;\n" +
                    "import io.swagger.annotations.ApiModelProperty;\n");
        }

        stringBuilder.append("\n");
        stringBuilder.append("/**\n");
        stringBuilder.append(" * Created by Database Helper " + DataTools.getDateNowThroughDate() + "\n");
        stringBuilder.append(" */\n");

        if (swagger) {
            stringBuilder.append("@ApiModel(value=\"" + modelPkg + "." + className + "\")\n");
        }
        if (lombok) {
            stringBuilder.append("@Data\n");
        }
        stringBuilder.append("public class " + className + "{\n");
        for (EntityItem entityItem : entityItems) {
            if (swagger) {
                stringBuilder.append("    @ApiModelProperty(value=\"" + entityItem.getRemarks() + "\")\n");
            }
            stringBuilder.append("    private " + entityItem.getType() + " " + entityItem.getName() + ";\n\n");
            if (!lombok) {
                String name = entityItem.getName();
                name = (entityItem.getName().charAt(0) + "").toUpperCase() + name.substring(1);
                methed.append("    public void set" + name + "(" + entityItem.getType() + " " + entityItem.getName() + ") {\n" +
                        "        this." + entityItem.getName() + " = " + entityItem.getName() + ";\n" +
                        "    }\n\n");

                methed.append("    public " + entityItem.getType() + " get" + name + "() {\n" +
                        "        return " + entityItem.getName() + ";\n" +
                        "    }\n\n");
            }
            if (!lombok && toString) {
                if (toStringBuilder.toString().equals("")) {
                    toStringBuilder.append("            \"" + entityItem.getName() + "='\" + " + entityItem.getName() + " + '\\'' +\n");
                } else {
                    toStringBuilder.append("            \", " + entityItem.getName() + "='\" + " + entityItem.getName() + " + '\\'' +\n");
                }
            }
        }
        if (!lombok && toString) {
            stringBuilder.append(methed);
            stringBuilder.append("    @Override\n" +
                    "    public String toString() {\n");
            stringBuilder.append("        return \"" + className + "{\"+\n");
            stringBuilder.append(toStringBuilder);
            stringBuilder.append("             \"}\";\n");
            stringBuilder.append("        }\n");
        }
        stringBuilder.append("}");
        writeClass(modelSrc, className + ".java", stringBuilder);

    }

    private static void builderMapper(String className, List<EntityItem> entityItems) {
        StringBuilder mapperBuilder = new StringBuilder();
        mapperBuilder.append("package " + mapperPkg + ";\n\n");
        mapperBuilder.append("import org.apache.ibatis.annotations.Param;\n");
        mapperBuilder.append("import java.util.List;\n");
        mapperBuilder.append("import " + modelPkg + "." + className + ";\n");
        mapperBuilder.append("import org.apache.ibatis.annotations.Mapper;\n\n");

        mapperBuilder.append("\n");
        mapperBuilder.append("/**\n");
        mapperBuilder.append(" * Created by Database Helper " + DataTools.getDateNowThroughDate() + "\n");
        mapperBuilder.append(" */\n");

        mapperBuilder.append("@Mapper\n");

        mapperBuilder.append("public interface " + className + "Mapper {\n");

        String keys = "";
        for (EntityItem entityItem : pKeys) {
            if (!keys.equals("")) {
                keys += ",";
            }
            keys += "@Param(\"" + entityItem.getName() + "\")" + entityItem.getType() + " " + entityItem.getName();
        }
        mapperBuilder.append("        " + className + " selectByPrimaryKey  (" + keys + ");\n\n");
        mapperBuilder.append("        int deleteByPrimaryKey (" + keys + ");\n\n");
        mapperBuilder.append("        int insert (" + className + " record);\n\n");
        mapperBuilder.append("        int insertSelective (" + className + " record);\n\n");
        mapperBuilder.append("        int updateByPrimaryKeySelective (" + className + " record);\n\n");
        mapperBuilder.append("        int updateByPrimaryKey (" + className + " record);\n");

        mapperBuilder.append("}");

        writeClass(mapperSrc, className + "Mapper.java", mapperBuilder);
    }

    private static void builderXml(String className, List<EntityItem> entityItems) {
        StringBuilder xmlBuilder = new StringBuilder();
        String remaks = "";
        remaks += "        <!--\n";
        remaks += "            Created by Database Helper " + DataTools.getDateNowThroughDate() + "\n";
        remaks += "         -->\n";

        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuilder.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
        xmlBuilder.append("<mapper namespace=\"" + mapperPkg + "." + className + "Mapper\">\n");
        xmlBuilder.append("    <resultMap id=\"BaseResultMap\" type=\"" + modelPkg + "." + className + "\">\n");
        xmlBuilder.append(remaks);

        String xmlTag = "result";
        String items = "";
        String jdbcItems = "";
        String keys = "";
        String trimOne="";
        String trimTow="";
        String update="";
        String updateSelect="";
        for (EntityItem entityItem : entityItems) {
            if (!items.equals("")) {
                items += ",";
                jdbcItems += ",";
            }
            items += entityItem.getSqlName();
            jdbcItems += "#{" + entityItem.getName() + ",jdbcType=" + entityItem.getSqlType() + "}";
            if (entityItem.isPkey()) {
                if (!keys.equals("")) {
                    keys += " and ";
                }
                keys += entityItem.getSqlName() + " = #{" + entityItem.getName() + ",jdbcType=" + entityItem.getSqlType() + "}";
            }
            if (entityItem.isPkey()) {
                xmlTag = "id";
            } else {
                xmlTag = "result";
            }
            trimOne+="            <if test=\""+entityItem.getName()+" != null\">\n";
            trimOne+="                "+entityItem.getSqlName()+",\n";
            trimOne+="            </if>\n";
            trimTow+="            <if test=\""+entityItem.getName()+" != null\">\n";
            trimTow+="                #{"+entityItem.getName()+",jdbcType=" + entityItem.getSqlType() + "},\n";
            trimTow+="            </if>\n";
            updateSelect+="            <if test=\""+entityItem.getName()+" != null\">\n";
            updateSelect+="                "+entityItem.getSqlName()+" = #{"+entityItem.getName()+",jdbcType=" + entityItem.getSqlType() + "},\n";
            updateSelect+="            </if>\n";
            if (!update.equals("")){
                update+=",\n";
            }
            update+="            "+entityItem.getSqlName()+" = #{"+entityItem.getName()+",jdbcType="+entityItem.getSqlType()+"},";
            xmlBuilder.append("         <" + xmlTag + " column=\"" + entityItem.getSqlName() + "\" jdbcType=\"" + entityItem.getSqlType() + "\" property=\"" + entityItem.getName() + "\"/>\n");
        }
        xmlBuilder.append("    </resultMap>\n");
        xmlBuilder.append("    <sql id=\"Base_Column_List\">\n");
        xmlBuilder.append(remaks);
        xmlBuilder.append("        " + items + "\n");
        xmlBuilder.append("    </sql>\n\n");

        /*selectByPrimaryKey*/
        xmlBuilder.append("    <select id=\"selectByPrimaryKey\"  resultMap=\"BaseResultMap\">\n");
        xmlBuilder.append(remaks);
        xmlBuilder.append("        select\n");
        xmlBuilder.append("        <include refid=\"Base_Column_List\"/>\n");
        xmlBuilder.append("        from " + tableName + "\n");
        xmlBuilder.append("        where ");
        xmlBuilder.append(keys + "\n");
        xmlBuilder.append("    </select>\n\n");
        /*deleteByPrimaryKey*/
        xmlBuilder.append("    <delete id=\"deleteByPrimaryKey\">\n");
        xmlBuilder.append(remaks);
        xmlBuilder.append("        delete from " + tableName + "\n");
        xmlBuilder.append("        where " + keys + "\n");
        xmlBuilder.append("    </delete>\n\n");
        /*insert*/
        xmlBuilder.append("    <insert id=\"insert\" parameterType=\"" + modelPkg + "." + className + "\">\n");
        xmlBuilder.append(remaks);
        xmlBuilder.append("        insert into " + tableName + " \n");
        xmlBuilder.append("        (\n");
        xmlBuilder.append("          <include refid=\"Base_Column_List\"/>\n");
        xmlBuilder.append("        )\n");
        xmlBuilder.append("        values(" + jdbcItems + ")\n");
        xmlBuilder.append("    </insert >\n\n");
       /* insertSelective*/
        xmlBuilder.append("    <insert id=\"insertSelective\" parameterType=\"" + modelPkg + "." + className + "\">\n");
        xmlBuilder.append(remaks);
        xmlBuilder.append("        insert into " + tableName + " \n");
        xmlBuilder.append("        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        xmlBuilder.append(trimOne);
        xmlBuilder.append("        </trim>\n");
        xmlBuilder.append("        <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n");
        xmlBuilder.append(trimTow);
        xmlBuilder.append("        </trim>\n");
        xmlBuilder.append("    </insert>\n\n");
       /* updateByPrimaryKey*/
        xmlBuilder.append("    <update id=\"updateByPrimaryKey\" parameterType=\"" + modelPkg + "." + className + "\">\n");
        xmlBuilder.append(remaks);
        xmlBuilder.append("        update " + tableName + " \n");
        xmlBuilder.append("        set "+update+"\n");
        xmlBuilder.append("        where "+keys+"\n");
        xmlBuilder.append("    </update>\n\n");
        /*updateByPrimaryKeySelective*/
        xmlBuilder.append("    <update id=\"updateByPrimaryKeySelective\" parameterType=\"" + modelPkg + "." + className + "\">\n");
        xmlBuilder.append(remaks);
        xmlBuilder.append("        update " + tableName + " \n");
        xmlBuilder.append("        <set>\n");
        xmlBuilder.append(updateSelect);
        xmlBuilder.append("        </set>\n");
        xmlBuilder.append("        where " + keys + "\n");
        xmlBuilder.append("    </update>\n\n");

        xmlBuilder.append("</mapper>");


        writeClass(xmlSrc, className + "Mapper.xml", xmlBuilder);
    }

    private static void writeClass(String src, String name, StringBuilder stringBuilder) {
        try {
            File path = new File(src); // 相对路径，如果没有则要建立一个新的output.txt文件
            if (!path.exists()) {
                path.mkdirs();
            }
            File writeName = new File(src + "/" + name);
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(writeName), "UTF-8");
            write.write(stringBuilder.toString());
            write.close();
        } catch (Exception e) {
            System.out.println("发生错误:" + e.getLocalizedMessage());
        }
    }

}
