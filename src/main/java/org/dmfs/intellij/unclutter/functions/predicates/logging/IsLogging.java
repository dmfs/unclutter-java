package org.dmfs.intellij.unclutter.functions.predicates.logging;

import com.intellij.psi.PsiMethodCallExpression;

import org.dmfs.intellij.unclutter.UnclutterFoldingSettings;
import org.dmfs.intellij.unclutter.functions.predicates.CallOn;
import org.dmfs.intellij.unclutter.functions.predicates.CallTo;

import java.util.Set;
import java.util.function.Predicate;


public final class IsLogging
{
    private static final Set<String> ANDROID_DEBUG_LOG_METHODS = Set.of("v", "d", "i");
    private static final Set<String> JAVA_DEBUG_LOG_METHODS = Set.of("fine", "finer", "finest", "entering", "exiting", "config", "info");
    private static final Set<String> SLF4J_DEBUG_LOG_METHODS = Set.of("debug", "info", "trace");

    private static final Set<String> ANDROID_ERROR_LOG_METHODS = Set.of("w", "e");
    private static final Set<String> JAVA_ERROR_LOG_METHODS = Set.of("log", "logp", "logrb", "severe", "throwing", "warning");
    private static final Set<String> SLF4J_ERROR_LOG_METHODS = Set.of("error", "warn");


    public enum LogFolding
    {
        NONE(ignored -> false),

        DEBUG(new CallOn("android.util.Log").and(new CallTo(ANDROID_DEBUG_LOG_METHODS::contains))
            .or(new CallOn("java.util.logging.Logger").and(new CallTo(JAVA_DEBUG_LOG_METHODS::contains)))
            .or(new CallOn("org.slf4j.Logger").and(new CallTo(SLF4J_DEBUG_LOG_METHODS::contains)))),

        ERROR(new CallOn("android.util.Log").and(new CallTo(ANDROID_ERROR_LOG_METHODS::contains))
            .or(new CallOn("java.util.logging.Logger").and(new CallTo(JAVA_ERROR_LOG_METHODS::contains)))
            .or(new CallOn("org.slf4j.Logger").and(new CallTo(SLF4J_ERROR_LOG_METHODS::contains)))),

        ALL(DEBUG.predicate.or(ERROR.predicate));

        public final Predicate<PsiMethodCallExpression> predicate;


        LogFolding(Predicate<PsiMethodCallExpression> predicate)
        {
            this.predicate = predicate;
        }


        public static LogFolding forSettings(UnclutterFoldingSettings.State settings)
        {
            if (settings.isDebugLogging() && settings.isErrorLogging())
            {
                return ALL;
            }
            if (settings.isDebugLogging())
            {
                return DEBUG;
            }
            if (settings.isErrorLogging())
            {
                return ERROR;
            }
            return NONE;
        }
    }
}
