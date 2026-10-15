package UI;
import javax.swing.JButton;
import java.awt.Color;

/**
 *
 * @author joaco
 */
public class DefaultBtn extends JButton {
    
    public DefaultBtn() {
        super();
        Color colorBtn = new Color(201,168,106);
        this.setBackground(colorBtn);
        
        this.setBorderPainted(false);
        
        Color colorBtnFont = new Color(60,63,65);
        this.setForeground(colorBtnFont);
        
        java.awt.Font textFont = new java.awt.Font("Verdana", 0, 64);
        this.setFont(textFont);
        
        this.putClientProperty("JButton.buttonType", "roundRect");
        this.putClientProperty("JComponent.outline", colorBtnFont);
        this.putClientProperty("FlatClientProperties.STYLE", "borderWidth: 100; arc; 20");
        
        this.setIcon(new javax.swing.ImageIcon("/home/joaco/Desktop/ITI 2026/Programación Avanzada/FelipeBanco/Cliente/src/main/recursos/Botones/suiguiente.png")); // NOI18N

        this.setBorder(null);
        
       this.setContentAreaFilled(false);
       this.setFocusPainted(false);

        
    }
    
    
}
