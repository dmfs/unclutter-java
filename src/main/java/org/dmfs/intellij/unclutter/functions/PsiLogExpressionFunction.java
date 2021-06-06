package org.dmfs.intellij.unclutter.functions;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.psi.PsiMethodCallExpression;

import org.dmfs.intellij.unclutter.UnclutterFoldingSettings;

import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.dmfs.intellij.unclutter.functions.predicates.logging.IsLogging.isLogExpression;


public class PsiLogExpressionFunction implements Function<PsiMethodCallExpression, List<FoldingDescriptor>>
{
    private final UnclutterFoldingSettings.State settings;


    public PsiLogExpressionFunction(UnclutterFoldingSettings.State settings)
    {
        this.settings = settings;
    }


    @Override
    public List<FoldingDescriptor> apply(PsiMethodCallExpression psiMethodCallExpression)
    {
        if (!settings.isLogging())
        {
            return emptyList();
        }

        if (!isLogExpression().test(psiMethodCallExpression))
        {
            return emptyList();
        }

        return singletonList(new FoldingDescriptor(psiMethodCallExpression,
            psiMethodCallExpression.getTextRange().getStartOffset(),
            psiMethodCallExpression.getTextRange().getEndOffset(),
            null,
            "/*log*/"));
    }
}
