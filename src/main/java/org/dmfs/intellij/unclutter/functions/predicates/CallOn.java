package org.dmfs.intellij.unclutter.functions.predicates;

import com.intellij.psi.PsiMethodCallExpression;

import java.util.Optional;
import java.util.function.Predicate;


public final class CallOn implements Predicate<PsiMethodCallExpression>
{
    private final Predicate<String> delegate;


    public CallOn(String className)
    {
        this(className::equals);
    }


    public CallOn(Predicate<String> delegate)
    {
        this.delegate = delegate;
    }


    @Override
    public boolean test(PsiMethodCallExpression psiMethodCallExpression)
    {
        return Optional.ofNullable(psiMethodCallExpression.resolveMethod())
            .flatMap(e -> Optional.ofNullable(e.getContainingClass()))
            .flatMap(c -> Optional.ofNullable(c.getQualifiedName()))
            .filter(delegate).isPresent();
    }
}
