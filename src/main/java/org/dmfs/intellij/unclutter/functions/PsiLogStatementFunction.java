package org.dmfs.intellij.unclutter.functions;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiMethodCallExpression;

import org.dmfs.intellij.unclutter.UnclutterFoldingSettings;
import org.dmfs.intellij.unclutter.functions.predicates.IsA;
import org.dmfs.intellij.unclutter.functions.predicates.logging.IsLogging;

import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;


public class PsiLogStatementFunction implements Function<PsiExpressionStatement, List<FoldingDescriptor>>
{
    private final UnclutterFoldingSettings.State settings;


    public PsiLogStatementFunction(UnclutterFoldingSettings.State settings)
    {
        this.settings = settings;
    }


    @Override
    public List<FoldingDescriptor> apply(PsiExpressionStatement psiStatement)
    {
        if (!new IsA<PsiExpression, PsiMethodCallExpression>(PsiMethodCallExpression.class, IsLogging.LogFolding.forSettings(settings).predicate).test(
            psiStatement.getExpression()))
        {
            return emptyList();
        }

        return singletonList(new FoldingDescriptor(psiStatement,
            psiStatement.getTextRange().getStartOffset(),
            psiStatement.getTextRange().getEndOffset(),
            null,
            "/*log*/"));
    }
}
