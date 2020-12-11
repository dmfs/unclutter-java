package org.dmfs.intellij.unclutter.functions;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.psi.*;
import org.dmfs.intellij.unclutter.UnclutterFoldingSettings;

import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class PsiMethodCallExpressionFunction implements Function<PsiMethodCallExpression, List<FoldingDescriptor>> {
    private final UnclutterFoldingSettings.State settings;

    public PsiMethodCallExpressionFunction(UnclutterFoldingSettings.State settings) {
        this.settings = settings;
    }

    @Override
    public List<FoldingDescriptor> apply(PsiMethodCallExpression psiMethodCallExpression) {
        if (!settings.isFunctionalInterfaces()) {
            return emptyList();
        }

        PsiMethod method = psiMethodCallExpression.resolveMethod();

        if (method == null) {
            return emptyList();
        }

        PsiClass clazz = method.getContainingClass();

        PsiReferenceExpression methodExpression = psiMethodCallExpression.getMethodExpression();

        if (clazz == null
                || method.isConstructor()
                || methodExpression.getQualifierExpression() == null
                || (clazz.isInterface() && clazz.getMethods().length != 1
                || clazz.getMethods().length - clazz.getConstructors().length != 1)) {
            return emptyList();
        }

        FoldingGroup group = FoldingGroup.newGroup(psiMethodCallExpression.getText());

        PsiExpressionList arguments = psiMethodCallExpression.getArgumentList();
        if (arguments.isEmpty()) {
            return singletonList(new FoldingDescriptor(psiMethodCallExpression,
                    methodExpression.getReferenceNameElement().getTextRange().getStartOffset() - 1,
                    arguments.getTextRange().getEndOffset(),
                    group,
                    "()"));
        }

        return asList(
                new FoldingDescriptor(psiMethodCallExpression,
                        methodExpression.getReferenceNameElement().getTextRange().getStartOffset() - 1,
                        arguments.getTextRange().getStartOffset() + 1,
                        group,
                        "("),
                new FoldingDescriptor(psiMethodCallExpression,
                        arguments.getTextRange().getEndOffset() - 1,
                        arguments.getTextRange().getEndOffset(),
                        group,
                        ")"));
    }
}
