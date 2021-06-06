package org.dmfs.intellij.unclutter.functions;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiNewExpression;

import org.dmfs.intellij.unclutter.UnclutterFoldingSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;


public class PsiNewExpressionFunction implements Function<PsiNewExpression, List<FoldingDescriptor>>
{
    public final static Pattern FQN_PATTERN = Pattern.compile("(new\\s+)((([\\w\\d_]+)\\s*\\.\\s*)*)([\\w\\d_]+)\\s*([^(\\[]*>)?\\s*([(\\[])");

    private final UnclutterFoldingSettings.State settings;


    public PsiNewExpressionFunction(UnclutterFoldingSettings.State settings)
    {
        this.settings = settings;
    }


    @Override
    public List<FoldingDescriptor> apply(PsiNewExpression psiNewExpression)
    {
        FoldingGroup group = FoldingGroup.newGroup(psiNewExpression.getText());

        TextRange range = psiNewExpression.getTextRange();
        int start = range.getStartOffset();
        String text = psiNewExpression.getText().substring(start - psiNewExpression.getTextOffset(), range.getEndOffset() - psiNewExpression.getTextOffset());

        Matcher matcher = FQN_PATTERN.matcher(text);
        if (!matcher.lookingAt())
        {
            return emptyList();
        }

        List<FoldingDescriptor> result = new ArrayList<>();
        if (matcher.group(2) != null && !matcher.group(2).isEmpty() && settings.isNamespace())
        {
            result.add(new FoldingDescriptor(psiNewExpression,
                settings.isNewKeyword() ? start : start + matcher.start(2),
                start + matcher.end(2),
                group,
                "…"));
        }

        if (matcher.group(6) != null && !matcher.group(6).isEmpty() && settings.isGenericArguments())
        {
            result.add(new FoldingDescriptor(psiNewExpression,
                start + matcher.start(6),
                start + matcher.end(6),
                group,
                "⋄"));
        }

        PsiExpressionList args = psiNewExpression.getArgumentList();
        if (args == null)
        {
            return result;
        }

        if (settings.isNewKeyword())
        {
            // hide "new" keyword
            result.add(new FoldingDescriptor(psiNewExpression,
                start,
                start + matcher.end(1),
                group,
                ""));
            // "fold" braces
            if (args.isEmpty())
            {
                result.add(new FoldingDescriptor(psiNewExpression,
                    args.getTextRange().getStartOffset(),
                    args.getTextRange().getEndOffset(),
                    group,
                    "()"));
            }
            else
            {
                result.add(new FoldingDescriptor(psiNewExpression,
                    args.getTextRange().getStartOffset(),
                    args.getTextRange().getStartOffset() + 1,
                    group,
                    "("));
                result.add(new FoldingDescriptor(psiNewExpression,
                    args.getTextRange().getEndOffset() - 1,
                    args.getTextRange().getEndOffset(),
                    group,
                    ")"));
            }
        }
        return result;
    }
}
