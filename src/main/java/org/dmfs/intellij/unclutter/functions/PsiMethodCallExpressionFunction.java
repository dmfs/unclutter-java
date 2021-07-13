package org.dmfs.intellij.unclutter.functions;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.psi.*;

import org.dmfs.intellij.unclutter.UnclutterFoldingSettings;
import org.dmfs.intellij.unclutter.functions.predicates.IsAnnotated;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;


public class PsiMethodCallExpressionFunction implements Function<PsiMethodCallExpression, List<FoldingDescriptor>>
{
    private final UnclutterFoldingSettings.State settings;


    public PsiMethodCallExpressionFunction(UnclutterFoldingSettings.State settings)
    {
        this.settings = settings;
    }


    @Override
    public List<FoldingDescriptor> apply(PsiMethodCallExpression psiMethodCallExpression)
    {
        if (!settings.isFunctionalInterfaces())
        {
            return emptyList();
        }

        boolean isFunctional =
            Optional.of(psiMethodCallExpression)
                .flatMap(e -> Optional.ofNullable(e.resolveMethod()))
                .stream()
                .filter(m -> !m.isConstructor())
                .filter(m -> !m.hasModifierProperty(PsiModifier.STATIC) && !m.hasModifierProperty(PsiModifier.DEFAULT))
                .flatMap(m -> Stream.concat(Stream.of(m), stream(m.findDeepestSuperMethods())))
                .map(PsiJvmMember::getContainingClass)
                .filter(c -> c != null && c.getQualifiedName() != null)
                .anyMatch(new IsAnnotated("java.lang.FunctionalInterface"));

        if (!isFunctional)
        {
            return emptyList();
        }

        PsiReferenceExpression methodExpression = psiMethodCallExpression.getMethodExpression();
        if (methodExpression.getQualifierExpression() == null)
        {
            // this can probably happen for calls to other members without "this."
            return emptyList();
        }

        FoldingGroup group = FoldingGroup.newGroup(psiMethodCallExpression.getText());

        PsiExpressionList arguments = psiMethodCallExpression.getArgumentList();
        if (arguments.isEmpty())
        {
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
