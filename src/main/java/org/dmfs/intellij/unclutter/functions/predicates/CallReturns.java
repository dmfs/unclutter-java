package org.dmfs.intellij.unclutter.functions.predicates;

import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiType;

import java.util.Optional;
import java.util.function.Predicate;


public final class CallReturns implements Predicate<PsiMethodCallExpression>
{
    private final PsiType type;


    public CallReturns(PsiType type)
    {
        this.type = type;
    }


    @Override
    public boolean test(PsiMethodCallExpression psiMethodCallExpression)
    {
        return Optional.ofNullable(psiMethodCallExpression.resolveMethod())
            .flatMap(e -> Optional.ofNullable(e.getReturnType()))
            .filter(type::isAssignableFrom)
            .isPresent();
    }
}
