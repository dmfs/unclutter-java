package org.dmfs.intellij.unclutter.functions;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.tree.TokenSet;

import org.dmfs.intellij.unclutter.UnclutterFoldingSettings;
import org.dmfs.intellij.unclutter.functions.predicates.CallOnInstanceOf;
import org.dmfs.intellij.unclutter.functions.predicates.CallTo;
import org.dmfs.intellij.unclutter.functions.predicates.IsA;

import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;


public final class ComparatorExpressionFunction implements Function<PsiBinaryExpression, List<FoldingDescriptor>>
{
    private static final TokenSet COMPARATOR_OPERATION_TOKENS = TokenSet.create(JavaTokenType.EQEQ, JavaTokenType.NE, JavaTokenType.LT, JavaTokenType.GT,
        JavaTokenType.LE, JavaTokenType.GE);
    private final UnclutterFoldingSettings.State settings;


    public ComparatorExpressionFunction(UnclutterFoldingSettings.State settings)
    {
        this.settings = settings;
    }


    @Override
    public List<FoldingDescriptor> apply(PsiBinaryExpression psiBinaryExpression)
    {
        if (!settings.isCompareTo() || !COMPARATOR_OPERATION_TOKENS.contains(psiBinaryExpression.getOperationTokenType()))
        {
            return emptyList();
        }

        PsiExpression right = psiBinaryExpression.getROperand();
        if (right == null || !right.textMatches("0"))
        {
            return emptyList();
        }

        PsiExpression left = psiBinaryExpression.getLOperand();
        if (!new IsA<>(PsiMethodCallExpression.class,
            new CallOnInstanceOf("java.lang.Comparable")
                .and(new CallTo("compareTo"))).test(left))
        {
            return emptyList();
        }

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) left;
        if (methodCall.getMethodExpression().getQualifier() == null || methodCall.getArgumentList().getExpressionCount() != 1)
        {
            return emptyList();
        }

        FoldingGroup group = FoldingGroup.newGroup(psiBinaryExpression.getText());

        return List.of(
            new FoldingDescriptor(psiBinaryExpression,
                methodCall.getMethodExpression().getQualifier().getTextRange().getEndOffset(),
                methodCall.getArgumentList().getTextRange().getStartOffset() + 1,
                group,
                " " + psiBinaryExpression.getOperationSign().getText() + " "),
            new FoldingDescriptor(psiBinaryExpression,
                methodCall.getArgumentList().getTextRange().getEndOffset() - 1,
                psiBinaryExpression.getTextRange().getEndOffset(),
                group,
                ""));

    }
}
