package org.dmfs.intellij.unclutter.functions.predicates.logging;

import com.intellij.psi.PsiMethodCallExpression;

import org.dmfs.intellij.unclutter.functions.predicates.CallOn;
import org.dmfs.intellij.unclutter.functions.predicates.CallTo;

import java.util.Set;
import java.util.function.Predicate;


public final class IsLogging
{
    private static final Set<String> ANDROID_LOG_METHODS = Set.of("v", "d", "i", "w", "e");
    private static final Set<String> JAVA_LOG_METHODS = Set.of("log", "logp", "logrb", "fine", "finer", "finest", "entering", "exiting", "config", "info",
        "severe", "throwing", "warning");
    private static final Set<String> SLF4J_LOG_METHODS = Set.of("debug", "error", "info", "trace", "warn");


    public static Predicate<PsiMethodCallExpression> isLogExpression()
    {
        return
            new CallOn("android.util.Log").and(new CallTo(ANDROID_LOG_METHODS::contains))
                .or(new CallOn("java.util.logging.Logger").and(new CallTo(JAVA_LOG_METHODS::contains)))
                .or(new CallOn("org.slf4j.Logger").and(new CallTo(SLF4J_LOG_METHODS::contains)));
    }
}
