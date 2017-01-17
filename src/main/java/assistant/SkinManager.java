package assistant;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.SubstanceBorderPainter;
import org.jvnet.substance.button.SubstanceButtonShaper;
import org.jvnet.substance.painter.SubstanceGradientPainter;
import org.jvnet.substance.skin.SkinInfo;
import org.jvnet.substance.skin.SubstanceSkin;
import org.jvnet.substance.theme.ThemeInfo;
import org.jvnet.substance.watermark.SubstanceWatermark;

public class SkinManager {
    private ConfigManager config = new ConfigManager();

    public void initialize() throws Exception {
        String skin = ObjectUtils.toString(config.get("UISkin")).trim();
        String theme = ObjectUtils.toString(config.get("UITheme")).trim();
        
        if(skin.equalsIgnoreCase("default")) //java default
            return;
        
        UIManager.setLookAndFeel(new SubstanceLookAndFeel());
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        
        if(StringUtils.isNotEmpty(theme))
            SubstanceLookAndFeel.setCurrentTheme(theme);
        else if(StringUtils.isNotEmpty(skin))
            SubstanceLookAndFeel.setSkin((SubstanceSkin)newInstance(skin));
        String watermark = ObjectUtils.toString(config.get("UIWatermark")).trim();
        if(StringUtils.isNotEmpty(watermark))
            SubstanceLookAndFeel.setCurrentWatermark((SubstanceWatermark)newInstance(watermark));
        String buttonShaper = ObjectUtils.toString(config.get("UIButtonShaper")).trim();
        if(StringUtils.isNotEmpty(buttonShaper))
            SubstanceLookAndFeel.setCurrentButtonShaper((SubstanceButtonShaper)newInstance(buttonShaper));
        String borderPainter = ObjectUtils.toString(config.get("UIBorderPainter")).trim();
        if(StringUtils.isNotEmpty(borderPainter))
            SubstanceLookAndFeel.setCurrentBorderPainter((SubstanceBorderPainter)newInstance(borderPainter));
        String gradientPainter = ObjectUtils.toString(config.get("UIGradientPainter")).trim();
        if(StringUtils.isNotEmpty(gradientPainter))
            SubstanceLookAndFeel.setCurrentGradientPainter((SubstanceGradientPainter)newInstance(gradientPainter));
    }
    
    public void saveCurrent() {
        try {
            SkinInfo currentSkin = SubstanceLookAndFeel.getAllSkins().get(SubstanceLookAndFeel.getCurrentThemeName());
            ThemeInfo currentTheme = SubstanceLookAndFeel.getAllThemes().get(SubstanceLookAndFeel.getCurrentThemeName());
            config.set("UISkin", currentSkin != null ? currentSkin.getClassName() : "");
            config.set("UITheme", currentTheme != null ? currentTheme.getClassName() : "");
            config.set("UIWatermark", SubstanceLookAndFeel.getCurrentWatermark().getClass().getName());
            config.set("UIButtonShaper", SubstanceLookAndFeel.getCurrentButtonShaper().getClass().getName());
            config.set("UIBorderPainter", SubstanceLookAndFeel.getCurrentBorderPainter().getClass().getName());
            config.set("UIGradientPainter", SubstanceLookAndFeel.getCurrentGradientPainter().getClass().getName());
            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private Object newInstance(String className) throws Exception {
        return Class.forName(className).getConstructor(null).newInstance(null);
    }
}
