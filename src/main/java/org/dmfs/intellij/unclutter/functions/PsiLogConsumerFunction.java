package org.dmfs.intellij.unclutter.functions;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLambdaExpression;
import com.intellij.psi.PsiMethodCallExpression;

import org.dmfs.intellij.unclutter.UnclutterFoldingSettings;
import org.dmfs.intellij.unclutter.functions.predicates.IsA;

import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.dmfs.intellij.unclutter.functions.predicates.logging.IsLogging.isLogExpression;


public class PsiLogConsumerFunction implements Function<PsiLambdaExpression, List<FoldingDescriptor>>
{
    private final UnclutterFoldingSettings.State settings;


    public PsiLogConsumerFunction(UnclutterFoldingSettings.State settings)
    {
        this.settings = settings;
    }


    @Override
    public List<FoldingDescriptor> apply(PsiLambdaExpression psiLambdaExpression)
    {
        if (!settings.isLogging())
        {
            return emptyList();
        }

        if (!new IsA<PsiElement, PsiMethodCallExpression>(PsiMethodCallExpression.class, isLogExpression()).test(psiLambdaExpression.getBody()))
        {
            return emptyList();
        }

        return singletonList(new FoldingDescriptor(psiLambdaExpression,
            psiLambdaExpression.getTextRange().getStartOffset(),
            psiLambdaExpression.getTextRange().getEndOffset(),
            null,
            "/*log*/"));
    }
}
