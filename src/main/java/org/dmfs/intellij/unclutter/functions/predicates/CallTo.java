package org.dmfs.intellij.unclutter.functions.predicates;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;

import java.util.Optional;
import java.util.function.Predicate;


public final class CallTo implements Predicate<PsiMethodCallExpression>
{
    private final Predicate<String> delegate;


    public CallTo(String methodName)
    {
        this(methodName::equals);
    }


    public CallTo(Predicate<String> delegate)
    {
        this.delegate = delegate;
    }


    @Override
    public boolean test(PsiMethodCallExpression psiMethodCallExpression)
    {
        return Optional.ofNullable(psiMethodCallExpression.resolveMethod())
            .map(PsiMethod::getName)
            .filter(delegate).isPresent();
    }
}
