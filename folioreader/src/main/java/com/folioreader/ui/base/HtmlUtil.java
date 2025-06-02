package com.folioreader.ui.base;

import android.content.Context;

import com.folioreader.Config;
import com.folioreader.R;
import com.folioreader.util.FontFinder;

import java.io.File;

/**
 * @author gautam chibde on 14/6/17.
 */

public final class HtmlUtil {

    /**
     * Function modifies input html string by adding extra css,js and font information.
     *
     * @param context     Activity Context
     * @param htmlContent input html raw data
     * @return modified raw html string
     */
    public static String getHtmlContent(Context context, String htmlContent, Config config) {

        String cssPath =
                String.format(context.getString(R.string.css_tag), "file:///android_asset/css/Style.css");

        String jsPath = String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/jsface.min.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/jquery-3.4.1.min.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/rangy-core.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/rangy-highlighter.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/rangy-classapplier.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/rangy-serializer.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/Bridge.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/rangefix.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/readium-cfi.umd.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag_method_call),
                "setMediaOverlayStyleColors('#C0ED72','#C0ED72')") + "\n";

        jsPath = jsPath
                + "<meta name=\"viewport\" content=\"height=device-height, user-scalable=no\" />";

        String fontNameArabic = config.getFontArabic();
        String fontNameLatin = config.getFontLatin();

        System.out.println("Font family arabic: " + fontNameArabic);
        System.out.println("Font family latin: " + fontNameLatin);

        // Inject CSS & user font style
        String toInject = "\n" + cssPath + "\n" + jsPath + "\n";

        File arabicFontFile = FontFinder.getFontFile(fontNameArabic, context);
        File latinFontFile = FontFinder.getFontFile(fontNameLatin, context);
//        if (arabicFontFile != null) {
//            System.out.println("Injected user font into CSS");
//            System.out.println("  - path latin: " + arabicFontFile.getAbsolutePath());
//            System.out.println("  - family latin: '" + fontNameArabic + "'");
//            toInject += "<style>\n";
//            toInject += "@font-face {\n";
//            toInject += "  font-family: '" + fontNameArabic + "';\n";
//            toInject += "  src: url('file://" + arabicFontFile.getAbsolutePath() + "');\n";
//            toInject += "}\n";
//            toInject += ".custom-font, .custom-font p, .custom-font div, .custom-font span, .custom-font h1, .custom-font strong {\n";
//            toInject += "  font-family: '" + fontNameArabic + "', sans-serif;\n";
//            toInject += "}\n";
//            toInject += "\n</style>";
//        }

        if (latinFontFile != null) {
            System.out.println("Injected user font into CSS");
            System.out.println("  - path latin: " + latinFontFile.getAbsolutePath());
            System.out.println("  - family latin: '" + fontNameLatin + "'");
            toInject += "<style>\n";
            toInject += "@font-face {\n";
            toInject += "  font-family: '" + fontNameLatin + "';\n";
            toInject += "  src: url('file://" + latinFontFile.getAbsolutePath() + "');\n";
            toInject += "}\n";
            toInject += ".custom-font, .custom-font p, .custom-font div, .custom-font span, .custom-font h1, .custom-font strong {\n";
            toInject += "  font-family: '" + fontNameLatin + "', sans-serif;\n";
            toInject += "}\n";
            toInject += "\n</style>";
        }


        toInject += "</head>";

        htmlContent = htmlContent.replace("</head>", toInject);

//        System.out.println("HTML CONTENT FIRST" + htmlContent);

        String classes = "custom-font";

        if (config.isNightMode()) {
            classes += " nightMode";
        }

        switch (config.getFontSize()) {
            case 1:
                classes += " textSizeOne";
                break;
            case 2:
                classes += " textSizeTwo";
                break;
            case 3:
                classes += " textSizeThree";
                break;
            case 4:
                classes += " textSizeFour";
                break;
            case 5:
                classes += " textSizeFive";
                break;
            case 6:
                classes += " textSizeSix";
                break;
            case 7:
                classes += " textSizeSeven";
                break;
            case 8:
                classes += " textSizeEight";
                break;
            case 9:
                classes += " textSizeNine";
                break;
            case 10:
                classes += " textSizeTen";
                break;
            default:
                break;
        }

//        String styles = "font-family: '" + fontNameArabic + "';";
        String styles = "font-family: '" + fontNameLatin + "';";

        htmlContent = htmlContent.replace("<html",
                "<html class=\"" + classes + "\"" +
                        " style=\"" + styles + "\"" +
                        " onclick=\"onClickHtml()\"");

//        System.out.println("HTML CONTENT SECOND" + htmlContent);

        return htmlContent;
    }
}
