package org.dmfs.intellij.unclutter.functions;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiNewExpression;
import org.dmfs.intellij.unclutter.UnclutterFoldingSettings;

import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;


/**
 * Function to fold express-json code into something that's almost looking like JSON.
 */
public class ExpressExpressionFunction implements Function<PsiNewExpression, List<FoldingDescriptor>>
{

    private final UnclutterFoldingSettings.State settings;


    public ExpressExpressionFunction(UnclutterFoldingSettings.State settings)
    {
        this.settings = settings;
    }


    @Override
    public List<FoldingDescriptor> apply(PsiNewExpression psiNewExpression)
    {
        if (!settings.isExpressJson())
        {
            return emptyList();
        }
        FoldingGroup group = FoldingGroup.newGroup(psiNewExpression.getText());
        PsiJavaCodeReferenceElement classExpression = psiNewExpression.getClassReference();

        if (classExpression != null)
        {
            String qualifiedName = classExpression.getQualifiedName();
            if ("org.dmfs.express.json.elementary.Object".equals(qualifiedName))
            {
                PsiExpressionList args = psiNewExpression.getArgumentList();
                if (args != null)
                {
                    return asList(new FoldingDescriptor(psiNewExpression,
                            psiNewExpression.getTextRange().getStartOffset(),
                            args.getTextRange().getStartOffset() + 1,
                            group,
                            "{"),
                        new FoldingDescriptor(psiNewExpression,
                            args.getTextRange().getEndOffset() - 1,
                            args.getTextRange().getEndOffset(),
                            group,
                            "}"));
                }
            }
            if ("org.dmfs.express.json.elementary.Array".equals(qualifiedName))
            {
                PsiExpressionList args = psiNewExpression.getArgumentList();
                return asList(new FoldingDescriptor(psiNewExpression,
                        psiNewExpression.getTextRange().getStartOffset(),
                        args.getTextRange().getStartOffset() + 1,
                        group,
                        "["),
                    new FoldingDescriptor(psiNewExpression,
                        args.getTextRange().getEndOffset() - 1,
                        args.getTextRange().getEndOffset(),
                        group,
                        "]"));
            }
            if ("org.dmfs.express.json.elementary.Member".equals(qualifiedName))
            {
                PsiExpressionList args = psiNewExpression.getArgumentList();
                if (args != null && args.getExpressionCount() == 2)
                {
                    return asList(new FoldingDescriptor(psiNewExpression,
                            psiNewExpression.getTextRange().getStartOffset(),
                            args.getTextRange().getStartOffset() + 1,
                            group,
                            ""),
                        new FoldingDescriptor(psiNewExpression,
                            args.getExpressions()[0].getTextRange().getEndOffset(),
                            args.getExpressions()[1].getTextRange().getStartOffset(),
                            group,
                            ": "),
                        new FoldingDescriptor(psiNewExpression,
                            args.getTextRange().getEndOffset() - 1,
                            args.getTextRange().getEndOffset(),
                            group,
                            ""));
                }
            }
            if ("org.dmfs.express.json.elementary.Null".equals(qualifiedName))
            {
                return singletonList(new FoldingDescriptor(psiNewExpression,
                    psiNewExpression.getTextRange().getStartOffset(),
                    psiNewExpression.getTextRange().getEndOffset(),
                    group,
                    "null"));
            }
        }
        return emptyList();
    }
}
