package com.hg.idea.plugins.util;


import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;


@State(
        name = "FormatSetting",
        storages = {@Storage(
                value = "com.hg.idea.plugins",
                file = "$APP_CONFIG$/format.xml"
        )}
)
public class FormatSetting implements PersistentStateComponent<Element> {

    private static String modelSrc, modelPkg, mapperSrc, mapperPkg, xmlSrc,lombok , toString, swagger,mapper;

    public FormatSetting() {
    }

    public static FormatSetting getInstance(Project project) {
        return ServiceManager.getService(project, FormatSetting.class);
    }

    @Nullable
    public Element getState() {
        Element element = new Element("FormatSetting");
        element.setAttribute("modelSrc", this.getModelSrc());
        element.setAttribute("modelPkg", this.getModelPkg());
        element.setAttribute("mapperSrc", this.getMapperSrc());
        element.setAttribute("mapperPkg", this.getMapperPkg());
        element.setAttribute("xmlSrc", this.getXmlSrc());
        element.setAttribute("lombok", this.getLombok());
        element.setAttribute("toString", this.getToString());
        element.setAttribute("swagger", this.getSwagger());
        element.setAttribute("mapper", this.getMapper());
        return element;
    }

    public void loadState(Element state) {
        this.setModelSrc(state.getAttributeValue("modelSrc"));
        this.setModelPkg(state.getAttributeValue("modelPkg"));
        this.setMapperSrc(state.getAttributeValue("mapperSrc"));
        this.setMapperPkg(state.getAttributeValue("mapperPkg"));
        this.setXmlSrc(state.getAttributeValue("xmlSrc"));
        this.setSwagger(state.getAttributeValue("swagger"));
        this.setLombok(state.getAttributeValue("lombok"));
        this.setToString(state.getAttributeValue("toString"));
        this.setMapper(state.getAttributeValue("mapper"));
    }

    public static String getModelSrc() {
        return modelSrc;
    }

    public static void setModelSrc(String modelSrc) {
        FormatSetting.modelSrc = modelSrc;
    }

    public static String getModelPkg() {
        return modelPkg;
    }

    public static void setModelPkg(String modelPkg) {
        FormatSetting.modelPkg = modelPkg;
    }

    public static String getMapperSrc() {
        return mapperSrc;
    }

    public static void setMapperSrc(String mapperSrc) {
        FormatSetting.mapperSrc = mapperSrc;
    }

    public static String getMapperPkg() {
        return mapperPkg;
    }

    public static void setMapperPkg(String mapperPkg) {
        FormatSetting.mapperPkg = mapperPkg;
    }

    public static String getXmlSrc() {
        return xmlSrc;
    }

    public static void setXmlSrc(String xmlSrc) {
        FormatSetting.xmlSrc = xmlSrc;
    }

    public static String getLombok() {
        return lombok;
    }

    public static void setLombok(String lombok) {
        FormatSetting.lombok = lombok;
    }

    public static String getToString() {
        return toString;
    }

    public static void setToString(String toString) {
        FormatSetting.toString = toString;
    }

    public static String getSwagger() {
        return swagger;
    }

    public static void setSwagger(String swagger) {
        FormatSetting.swagger = swagger;
    }

    public static String getMapper() {
        return mapper;
    }

    public static void setMapper(String mapper) {
        FormatSetting.mapper = mapper;
    }
}