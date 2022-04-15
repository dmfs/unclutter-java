package org.dmfs.intellij.unclutter;

import com.intellij.application.options.editor.CodeFoldingOptionsProvider;
import com.intellij.openapi.options.BeanConfigurable;


public class UnclutterFoldingOptionsProvider extends BeanConfigurable<UnclutterFoldingSettings.State> implements CodeFoldingOptionsProvider
{
    protected UnclutterFoldingOptionsProvider()
    {
        super(UnclutterFoldingSettings.getInstance().getState());
        UnclutterFoldingSettings.State settings = UnclutterFoldingSettings.getInstance().getState();
        setTitle("Unclutter Java");
        checkBox("Functional interfaces", settings::isFunctionalInterfaces, settings::setFunctionalInterfaces);
        checkBox("\"new\" keyword", settings::isNewKeyword, settings::setNewKeyword);
        checkBox("Constructor generic arguments", settings::isGenericArguments, settings::setGenericArguments);
        checkBox("Constructor qualification", settings::isNamespace, settings::setNamespace);
        checkBox("express-json", settings::isExpressJson, settings::setExpressJson);
        checkBox("Info, debug & trace logging", settings::isDebugLogging, settings::setDebugLogging);
        checkBox("Warn & error logging", settings::isErrorLogging, settings::setErrorLogging);
        checkBox("compareTo", settings::isCompareTo, settings::setCompareTo);

        createComponent();
    }
}