package org.dmfs.intellij.unclutter.functions.predicates;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.Optional;
import java.util.function.Predicate;


public final class CallOnInstanceOf implements Predicate<PsiMethodCallExpression>
{
    private final String delegate;


    public CallOnInstanceOf(String delegate)
    {
        this.delegate = delegate;
    }


    @Override
    public boolean test(PsiMethodCallExpression psiMethodCallExpression)
    {
        PsiClass psiType = JavaPsiFacade.getInstance(psiMethodCallExpression.getProject())
            .findClass(delegate, GlobalSearchScope.everythingScope(psiMethodCallExpression.getProject()));
        return psiType != null && Optional.ofNullable(psiMethodCallExpression.resolveMethod())
            .flatMap(e -> Optional.ofNullable(e.getContainingClass()))
            .stream()
            .anyMatch(psiClass -> psiType.equals(psiClass) || psiClass.isInheritor(psiType, true));
    }
}
