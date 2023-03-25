package org.dmfs.intellij.unclutter.functions;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;

import org.dmfs.intellij.unclutter.UnclutterFoldingSettings;
import org.dmfs.intellij.unclutter.functions.predicates.CallOn;
import org.dmfs.intellij.unclutter.functions.predicates.CallReturns;
import org.dmfs.intellij.unclutter.functions.predicates.CallTo;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;


/**
 * Function to fold express-json code into something that's almost looking like JSON.
 */
public class ConfidenceCallFunction implements Function<PsiMethodCallExpression, List<FoldingDescriptor>>
{
    private final static Pattern CAMELCASE = Pattern.compile("[a-z][A-Z]");

    private final UnclutterFoldingSettings.State settings;


    public ConfidenceCallFunction(UnclutterFoldingSettings.State settings)
    {
        this.settings = settings;
    }


    @Override
    public List<FoldingDescriptor> apply(PsiMethodCallExpression psiMethodCallExpression)
    {
        if (!settings.isConfidence())
        {
            return emptyList();
        }

        Optional<PsiType> qualityType = typeOf(psiMethodCallExpression.getProject(), "org.saynotobugs.confidence.Quality");
        Optional<PsiType> verifiableType = typeOf(psiMethodCallExpression.getProject(), "org.saynotobugs.confidence.junit5.engine.Verifiable");

        return qualityType.map(CallReturns::new)
            .map(p -> verifiableType.map(CallReturns::new).map(v -> v.or(p)).orElse(p))
            .map(p -> p.or(new CallTo("assertThat").and(new CallOn("org.saynotobugs.confidence.Assertion"))))
            .filter(p -> p.test(psiMethodCallExpression))
            .map(ignored ->
            {
                FoldingGroup group = FoldingGroup.newGroup(psiMethodCallExpression.getText());
                PsiExpression methodExpression = psiMethodCallExpression.getMethodExpression();
                PsiExpressionList args = psiMethodCallExpression.getArgumentList();

                String name = psiMethodCallExpression.resolveMethod().getName();
                String nameLower = name.toLowerCase();

                String resultingName;
                if (!name.equals(nameLower))
                {
                    Matcher m = CAMELCASE.matcher(name);
                    StringBuilder newName = new StringBuilder();
                    int start = 0;
                    while (m.find())
                    {
                        newName.append(nameLower, start, m.start() + 1);
                        newName.append(" ");
                        start = m.end() - 1;
                    }
                    newName.append(nameLower.substring(start));
                    resultingName = newName.toString();
                }
                else
                {
                    resultingName = name;
                }

                return
                    asList(
                        new FoldingDescriptor(psiMethodCallExpression,
                            methodExpression.getTextRange().getStartOffset(),
                            methodExpression.getTextRange().getEndOffset(),
                            group,
                            resultingName),
                        new FoldingDescriptor(psiMethodCallExpression,
                            args.getTextRange().getStartOffset(),
                            args.getTextRange().getStartOffset() + 1,
                            group,
                            " "),
                        new FoldingDescriptor(psiMethodCallExpression,
                            args.getTextRange().getEndOffset() - 1,
                            args.getTextRange().getEndOffset(),
                            group,
                            ""));
            })
            .orElse(emptyList());
    }


    private Optional<PsiType> typeOf(Project project, String className)
    {
        return Optional.ofNullable(JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.everythingScope(project)))
            .map(psiClass -> JavaPsiFacade.getInstance(project).getElementFactory().createType(psiClass));

    }
}
