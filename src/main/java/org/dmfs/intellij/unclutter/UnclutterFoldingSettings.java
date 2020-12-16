package org.dmfs.intellij.unclutter;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;


@com.intellij.openapi.components.State(name = "org.dmfs.UnclutterFoldingSettings", storages = @Storage("editor.codeinsight.xml"))
public class UnclutterFoldingSettings implements PersistentStateComponent<UnclutterFoldingSettings.State>
{
    private final State unclutterSettings = new State();


    @NotNull
    @Override
    public UnclutterFoldingSettings.State getState()
    {
        return unclutterSettings;
    }


    @NotNull
    public static UnclutterFoldingSettings getInstance()
    {
        return ServiceManager.getService(UnclutterFoldingSettings.class);
    }


    @Override
    public void loadState(State settings)
    {
        unclutterSettings.functionalInterfaces = settings.functionalInterfaces;
        unclutterSettings.newKeyword = settings.newKeyword;
        unclutterSettings.genericArguments = settings.genericArguments;
        unclutterSettings.namespace = settings.namespace;
        unclutterSettings.expressJson = settings.expressJson;
    }


    public static final class State
    {
        private boolean functionalInterfaces = true;
        private boolean newKeyword = true;
        private boolean genericArguments = true;
        private boolean namespace = true;
        private boolean expressJson = true;


        public boolean isFunctionalInterfaces()
        {
            return functionalInterfaces;
        }


        public void setFunctionalInterfaces(boolean functionalInterfaces)
        {
            this.functionalInterfaces = functionalInterfaces;
        }


        public boolean isGenericArguments()
        {
            return genericArguments;
        }


        public void setGenericArguments(boolean genericArguments)
        {
            this.genericArguments = genericArguments;
        }


        public boolean isNamespace()
        {
            return namespace;
        }


        public void setNamespace(boolean namespace)
        {
            this.namespace = namespace;
        }


        public boolean isNewKeyword()
        {
            return newKeyword;
        }


        public void setNewKeyword(boolean newKeyword)
        {
            this.newKeyword = newKeyword;
        }


        public boolean isExpressJson()
        {
            return expressJson;
        }


        public void setExpressJson(boolean expressJson)
        {
            this.expressJson = expressJson;
        }
    }
}
