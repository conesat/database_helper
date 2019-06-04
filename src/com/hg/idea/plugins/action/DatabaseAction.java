package com.hg.idea.plugins.action;

import com.hg.idea.plugins.ui.MainUI;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbElement;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.database.psi.DbTable;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;

import java.util.List;
import java.util.Objects;

public class DatabaseAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            Messages.showMessageDialog("Please select one or more tables", "Notice", Messages.getInformationIcon());
            return;
        }
        for (PsiElement psiElement : psiElements) {
            if (!(psiElement instanceof DbTable)) {
                //Messages.showMessageDialog("最多/少选一个表", "提示", Messages.getInformationIcon());
                return;
            }
        }
        boolean isSingleTable = psiElements.length == 1;
        new MainUI(e, isSingleTable);

    }

    private boolean areElementsOfOneDataSource(Project project, List<DbElement> dbElements, DbDataSource dataSource) {
        return dbElements.stream().map(dbElement -> DbPsiFacade.getInstance(project).findDataSource(dbElement)).filter(Objects::nonNull)
                .allMatch(dbDatSource -> dbDatSource.equals(dataSource));
    }
}
