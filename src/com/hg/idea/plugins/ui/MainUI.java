package com.hg.idea.plugins.ui;

import com.hg.idea.plugins.config.Config;
import com.hg.idea.plugins.util.FormatSetting;
import com.hg.idea.plugins.util.GenerateHelper;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import org.jdom.Element;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

import static com.intellij.ui.LoadingNode.getText;

public class MainUI extends JFrame {
    private Config config;
    private PsiElement[] psiElements;
    private Project project;
    private AnActionEvent anActionEvent;
    private static MainUI mainUI;
    private TextFieldWithBrowseButton textFieldWithBrowseButton1, textFieldWithBrowseButton2, textFieldWithBrowseButton3, textFieldWithBrowseButton4, textFieldWithBrowseButton5;
    private JCheckBox lombok, swagger, toString,mapper;

    public MainUI(AnActionEvent anActionEvent, boolean isSingleTable) {
        this.anActionEvent = anActionEvent;
        this.project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        this.psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        config = new Config(project.getProjectFile().getParent().getParent());
        initView("Generate Class");
        initData();
    }

    private void initData() {

    }


    private void initView(String title) {
        if (mainUI == null) {
            mainUI = this;
        } else {
            mainUI.pack();
            mainUI.setVisible(true);
            return;
        }


        initJPanel();//初始化选择界面
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                mainUI = null;
            }
        });
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screensize.getWidth();
        int height = (int) screensize.getHeight();
        this.setLocation(width/2-300,height/2-200);
        this.pack();
        this.setVisible(true);
    }


    private void initJPanel() {
        FormatSetting formatSetting = FormatSetting.getInstance(project);

        Box rootBox = Box.createVerticalBox();//根容器

        //路径选部分
        Box box1 = createChooseBox();
        box1.add(createTitle("model src"));
        textFieldWithBrowseButton1 = createSrcChoose();
        if (formatSetting.getModelPkg().equals("")){
            textFieldWithBrowseButton1.setText(config.getProjectSrc().getPath() + "/src");
        }else {
            textFieldWithBrowseButton1.setText(config.getProjectSrc().getPath()+ "/"+formatSetting.getModelPkg().replace(".","/"));
        }
        box1.add(textFieldWithBrowseButton1);

        Box box2 = createChooseBox();
        box2.add(createTitle("model pkg"));
        textFieldWithBrowseButton2 = createPackageChoose();
        textFieldWithBrowseButton2.setText(formatSetting.getModelPkg());
        box2.add(textFieldWithBrowseButton2);

        Box mapperBox=createChooseBox();
        mapper = createJCheckBox("generate mapper");
        if (formatSetting.getToString().equals("false")) {
            mapper.setSelected(false);
            textFieldWithBrowseButton3.setEnabled(false);
            textFieldWithBrowseButton4.setEnabled(false);
            textFieldWithBrowseButton5.setEnabled(false);
        }else {
            mapper.setSelected(true);
        }
        mapper.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (mapper.isSelected()){
                    textFieldWithBrowseButton3.setEnabled(true);
                    textFieldWithBrowseButton4.setEnabled(true);
                    textFieldWithBrowseButton5.setEnabled(true);
                }else {
                    textFieldWithBrowseButton3.setEnabled(false);
                    textFieldWithBrowseButton4.setEnabled(false);
                    textFieldWithBrowseButton5.setEnabled(false);
                }
            }
        });
        mapperBox.add(mapper);

        Box box3 = createChooseBox();
        box3.add(createTitle("mapper src"));
        textFieldWithBrowseButton3 = createSrcChoose();
        if (formatSetting.getMapperPkg().equals("")){
            textFieldWithBrowseButton3.setText(config.getProjectSrc().getPath() + "/src");
        }else {
            textFieldWithBrowseButton3.setText(config.getProjectSrc().getPath()+ "/"+formatSetting.getMapperPkg().replace(".","/"));
        }
        box3.add(textFieldWithBrowseButton3);

        Box box4 = createChooseBox();
        box4.add(createTitle("mapper pkg"));
        textFieldWithBrowseButton4 = createPackageChoose();
        textFieldWithBrowseButton4.setText(formatSetting.getMapperPkg());
        box4.add(textFieldWithBrowseButton4);

        Box box5 = createChooseBox();
        box5.add(createTitle("xml src"));
        textFieldWithBrowseButton5 = createSrcChoose();

        if (formatSetting.getXmlSrc().equals("")){
            textFieldWithBrowseButton5.setText(config.getProjectSrc().getPath() + "/src");
        }else {
            if (formatSetting.getXmlSrc().indexOf(config.getProjectSrc().toString().substring(7).replace("/","\\"))>=0){
                textFieldWithBrowseButton5.setText(formatSetting.getXmlSrc());
            }else {
                textFieldWithBrowseButton5.setText(config.getProjectSrc().getPath() + "/src");
            }
        }
        box5.add(textFieldWithBrowseButton5);


        rootBox.add(box1);
        rootBox.add(box2);
        rootBox.add(mapperBox);
        rootBox.add(box3);
        rootBox.add(box4);
        rootBox.add(box5);
        //路径选择部分-end

        //设置部分
        Box settingBox = Box.createHorizontalBox();
        JLabel settingTitle = new JLabel("setting");
        settingTitle.setBorder(new EmptyBorder(10, 20, 0, 0)); // 设置边距
        settingBox.add(settingTitle);

        Box sBox1 = Box.createHorizontalBox();
        lombok = createJCheckBox("lombok");
        if (formatSetting.getLombok().equals("true")) {
            lombok.setSelected(true);
        }
        swagger = createJCheckBox("swagger");
        if (formatSetting.getSwagger().equals("true")) {
            swagger.setSelected(true);
        }
        toString = createJCheckBox("toString");
        if (formatSetting.getToString().equals("true")) {
            toString.setSelected(true);
        }
        sBox1.add(lombok);
        sBox1.add(swagger);
        sBox1.add(toString);

        rootBox.add(settingBox);
        rootBox.add(sBox1);
        //设置部分-end

        //完成部分
        Box finishBox = Box.createHorizontalBox();

        JButton cancel = createJButton("cancel");
        JButton ok = createJButton("ok");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainUI.this.dispose();
            }
        });
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textFieldWithBrowseButton1.getText().toString().equals("")) {
                    Messages.showMessageDialog(project, "model src should not be empty", "messages", Messages.getInformationIcon());
                } else if (textFieldWithBrowseButton2.getText().toString().equals("")) {
                    Messages.showMessageDialog(project, "model pkg should not be empty", "messages", Messages.getInformationIcon());
                } else {
                    try {
                        Element element = new Element("FormatSetting");
                        element.setAttribute("modelSrc", textFieldWithBrowseButton1.getText().toString());
                        element.setAttribute("modelPkg", textFieldWithBrowseButton2.getText().toString());
                        element.setAttribute("mapperSrc", textFieldWithBrowseButton3.getText().toString());
                        element.setAttribute("mapperPkg", textFieldWithBrowseButton4.getText().toString());
                        element.setAttribute("xmlSrc", textFieldWithBrowseButton5.getText().toString());
                        element.setAttribute("lombok", lombok.isSelected() + "");
                        element.setAttribute("toString", toString.isSelected() + "");
                        element.setAttribute("swagger", swagger.isSelected() + "");
                        element.setAttribute("mapper", mapper.isSelected() + "");
                        formatSetting.loadState(element);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    GenerateHelper.Builder builder=  new GenerateHelper.Builder()
                            .modelSrc(textFieldWithBrowseButton1.getText().toString())
                            .modelPkg(textFieldWithBrowseButton2.getText().toString())
                            .mapperSrc(textFieldWithBrowseButton3.getText().toString())
                            .mapperPkg(textFieldWithBrowseButton4.getText().toString())
                            .xmlSrc(textFieldWithBrowseButton5.getText().toString())
                            .lombok(lombok.isSelected())
                            .toString(toString.isSelected())
                            .swagger(swagger.isSelected())
                            .mapper(mapper.isSelected());

                    for (PsiElement psiElement:psiElements){
                        builder.buderEntity(psiElement);
                    }
                    project.getBaseDir().refresh(false,true);
                    MainUI.this.dispose();
                    Messages.showMessageDialog(project, "generate finish", "done", Messages.getInformationIcon());
                }

            }
        });
        finishBox.add(ok);
        finishBox.add(cancel);

        rootBox.add(finishBox);

        rootBox.setBorder(new EmptyBorder(0, 0, 10, 0)); // 设置边距
        //完成部分
        this.add(rootBox);
    }

    public JButton createJButton(String title) {
        JButton jButton = new JButton(title);
        jButton.setBorder(new EmptyBorder(0, 10, 0, 10)); // 设置边距
        return jButton;
    }

    public Box createChooseBox() {
        Box box = Box.createHorizontalBox();
        box.setBorder(new EmptyBorder(10, 0, 0, 20)); // 设置边距
        return box;
    }

    public JCheckBox createJCheckBox(String title) {
        JCheckBox checkBox = new JCheckBox(title);
        checkBox.setBorder(new EmptyBorder(10, 10, 10, 10)); // 设置边距
        return checkBox;
    }


    public JLabel createTitle(String title) {
        JLabel label = new JLabel(title);
        label.setBorder(new EmptyBorder(0, 20, 0, 10)); // 设置边距
        label.setPreferredSize(new Dimension(120, 25));
        return label;
    }

    public TextFieldWithBrowseButton createSrcChoose() {
        TextFieldWithBrowseButton textFieldWithBrowseButton = new TextFieldWithBrowseButton();
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        textFieldWithBrowseButton.setPreferredSize(new Dimension(400, 25));
        textFieldWithBrowseButton.addBrowseFolderListener(new TextBrowseFolderListener(fileChooserDescriptor) {
        });
        return textFieldWithBrowseButton;
    }

    public TextFieldWithBrowseButton createPackageChoose() {
        TextFieldWithBrowseButton textFieldWithBrowseButton = new TextFieldWithBrowseButton();
        textFieldWithBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final PackageChooserDialog chooser = new PackageChooserDialog("select pkg", project);
                chooser.selectPackage(getText());
                if (chooser.showAndGet()) {
                    final PsiPackage aPackage = chooser.getSelectedPackage();
                    if (aPackage != null) {
                        textFieldWithBrowseButton.setText(aPackage.getQualifiedName());
                    }
                }
            }
        });
        textFieldWithBrowseButton.setPreferredSize(new Dimension(400, 25));
        return textFieldWithBrowseButton;
    }
}