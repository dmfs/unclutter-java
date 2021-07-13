package org.dmfs.intellij.unclutter.functions.predicates;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;

import java.util.Optional;
import java.util.function.Predicate;


public final class IsAnnotated implements Predicate<PsiClass>
{
    private final String annotationFqn;
    private final Predicate<? super PsiAnnotation> delegate;


    public IsAnnotated(String annotationFqn)
    {
        this(annotationFqn, c -> true);
    }


    public IsAnnotated(String annotationFqn, Predicate<? super PsiAnnotation> delegate)
    {
        this.annotationFqn = annotationFqn;
        this.delegate = delegate;
    }


    @Override
    public boolean test(PsiClass t)
    {
        return Optional.ofNullable(t.getAnnotation(annotationFqn)).filter(delegate).isPresent();
    }
}
