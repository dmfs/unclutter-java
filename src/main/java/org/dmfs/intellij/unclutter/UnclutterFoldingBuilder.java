/*
 * Copyright (c) 2018 Patrick Scheibe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.dmfs.intellij.unclutter;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import org.dmfs.intellij.unclutter.functions.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


/**
 * Folds invocations of constructors and functional interface methods.
 */
public class UnclutterFoldingBuilder extends FoldingBuilderEx
{

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick)
    {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        collectRegionsRecursively(root, document, descriptors);
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }


    private void collectRegionsRecursively(@NotNull final PsiElement node,
        @NotNull final Document document,
        @NotNull List<FoldingDescriptor> descriptors)
    {
        UnclutterFoldingSettings.State settings = UnclutterFoldingSettings.getInstance().getState();

        PsiMethodCallExpressionFunction methodCallFunction = new PsiMethodCallExpressionFunction(settings);
        PsiNewExpressionFunction newFunction = new PsiNewExpressionFunction(settings);
        ExpressExpressionFunction expressFunction = new ExpressExpressionFunction(settings);
        PsiLogConsumerFunction logConsumerFunction = new PsiLogConsumerFunction(settings);
        PsiLogStatementFunction logStatementFunction = new PsiLogStatementFunction(settings);
        ComparatorExpressionFunction comparatorExpressionFunction = new ComparatorExpressionFunction(settings);

        PsiTreeUtil.findChildrenOfAnyType(node,
                PsiMethodCallExpression.class,
                PsiNewExpression.class,
                PsiExpressionStatement.class,
                PsiBinaryExpression.class,
                PsiLambdaExpression.class)
            .forEach(
                expression -> {
                    if (expression instanceof PsiMethodCallExpression)
                    {
                        descriptors.addAll(methodCallFunction.apply((PsiMethodCallExpression) expression));
                    }
                    if (expression instanceof PsiBinaryExpression)
                    {
                        descriptors.addAll(comparatorExpressionFunction.apply((PsiBinaryExpression) expression));
                    }
                    if (expression instanceof PsiNewExpression)
                    {
                        descriptors.addAll(expressFunction.apply((PsiNewExpression) expression));
                        descriptors.addAll(newFunction.apply((PsiNewExpression) expression));
                    }
                    if (expression instanceof PsiLambdaExpression)
                    {
                        descriptors.addAll(logConsumerFunction.apply((PsiLambdaExpression) expression));
                    }
                    if (expression instanceof PsiExpressionStatement)
                    {
                        descriptors.addAll(logStatementFunction.apply((PsiExpressionStatement) expression));
                    }
                }
            );
    }


    @Nullable
    @Override
    public String getPlaceholderText(@NotNull final ASTNode node)
    {
        return "¯\\_(ツ)_/¯";
    }


    @Override
    public boolean isCollapsedByDefault(@NotNull final ASTNode node)
    {
        return true;
    }

}

