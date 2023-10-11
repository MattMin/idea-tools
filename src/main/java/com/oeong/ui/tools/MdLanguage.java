package com.oeong.ui.tools;

import com.intellij.lang.Language;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/10/8 11:07
 */
public class MdLanguage extends Language {
    public static final MdLanguage INSTANCE = new MdLanguage();

    protected MdLanguage(String ID, String... mimeTypes) {
        super(INSTANCE, ID, mimeTypes);
    }

    private MdLanguage() {
        super("Md", "text/markdown");
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }

    public boolean hasPermissiveStrings() { return false; }
}
